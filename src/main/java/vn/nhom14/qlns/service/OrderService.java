package vn.nhom14.qlns.service;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.*;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import vn.nhom14.qlns.dto.*;

@Service
@Transactional(readOnly=true,timeout=10)
public class OrderService {
    private static final Set<String> STATUSES=Set.of("CHO_XAC_NHAN","DA_XAC_NHAN","DANG_GIAO","HOAN_THANH","DA_HUY");
    private static final BigDecimal MAX_TOTAL=new BigDecimal("9999999999999.99");
    private static final String HEADERS="SELECT o.*,k.ho_ten AS ho_ten_khach_hang FROM don_hang o JOIN khach_hang k ON k.id=o.khach_hang_id ";
    private final JdbcTemplate jdbc;
    private final boolean sqlite;
    public OrderService(JdbcTemplate jdbc){
        this.jdbc=jdbc;
        this.sqlite=Boolean.TRUE.equals(jdbc.execute((org.springframework.jdbc.core.ConnectionCallback<Boolean>) c ->
                c.getMetaData().getDatabaseProductName().toLowerCase(Locale.ROOT).contains("sqlite")));
    }

    private record Actor(long id,String role,Long customerId){boolean staff(){return role.equals("ADMIN")||role.equals("NHAN_VIEN");}}
    private Actor actor(Authentication auth){
        if(auth==null||!auth.isAuthenticated())throw error(HttpStatus.UNAUTHORIZED,"Vui lòng đăng nhập");
        var users=jdbc.query("""
                SELECT t.id,v.ma_vai_tro,k.id AS customer_id FROM tai_khoan t
                JOIN vai_tro v ON v.id=t.vai_tro_id LEFT JOIN khach_hang k ON k.tai_khoan_id=t.id
                WHERE t.ten_dang_nhap=? AND t.trang_thai=TRUE
                """,(r,i)->{
                    Number customer=(Number)r.getObject("customer_id");
                    return new Actor(r.getLong("id"),r.getString("ma_vai_tro"),customer==null?null:customer.longValue());
                },auth.getName());
        if(users.isEmpty())throw error(HttpStatus.UNAUTHORIZED,"Tài khoản không còn hoạt động");
        Actor user=users.getFirst();
        if(!user.staff()&&(!user.role().equals("KHACH_HANG")||user.customerId()==null))
            throw error(HttpStatus.FORBIDDEN,"Tài khoản không có hồ sơ khách hàng");
        return user;
    }

    public PageResponse<OrderResponse> list(Authentication auth,int page,int size,String q,String status){
        Actor user=actor(auth);
        if(page<0||page>100000||size<1||size>100||q==null||q.length()>150)
            throw error(HttpStatus.BAD_REQUEST,"Phân trang hoặc từ khóa không hợp lệ");
        String state=status==null?"":status.strip();
        if(!state.isEmpty()&&!STATUSES.contains(state))throw error(HttpStatus.BAD_REQUEST,"Trạng thái đơn hàng không hợp lệ");
        String where=" WHERE 1=1";List<Object> args=new ArrayList<>();
        if(!user.staff()){where+=" AND o.khach_hang_id=?";args.add(user.customerId());}
        if(!state.isEmpty()){where+=" AND o.trang_thai=?";args.add(state);}
        if(!q.isBlank()){
            String pattern="%"+q.strip().toLowerCase(Locale.ROOT).replace("!","!!").replace("%","!%").replace("_","!_")+"%";
            where+=" AND (LOWER(o.ma_don_hang) LIKE ? ESCAPE '!' OR LOWER(o.ten_nguoi_nhan) LIKE ? ESCAPE '!' OR o.so_dien_thoai_nhan LIKE ? ESCAPE '!')";
            args.addAll(List.of(pattern,pattern,pattern));
        }
        long total=jdbc.queryForObject("SELECT COUNT(*) FROM don_hang o"+where,Long.class,args.toArray());
        args.add(size);args.add((long)page*size);
        var headers=jdbc.query(HEADERS+where+" ORDER BY o.id DESC LIMIT ? OFFSET ?",this::header,args.toArray());
        return new PageResponse<>(withItems(headers),page,size,total,(int)((total+size-1)/size));
    }
    public OrderResponse get(Authentication auth,Long id){return getFor(actor(auth),id);}
    public OrderResponse recover(Authentication auth,String requestId){
        Actor user=actor(auth);
        if(!user.role().equals("KHACH_HANG"))throw error(HttpStatus.FORBIDDEN,"Chỉ khách hàng được tra cứu lần đặt hàng");
        if(requestId==null||!requestId.matches("[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}"))
            throw error(HttpStatus.BAD_REQUEST,"Mã gửi đơn không hợp lệ");
        var ids=jdbc.query("SELECT id FROM don_hang WHERE ma_don_hang=? AND khach_hang_id=?",(r,i)->r.getLong(1),orderCode(user.customerId(),requestId),user.customerId());
        if(ids.isEmpty())throw error(HttpStatus.NOT_FOUND,"Chưa tìm thấy đơn hàng cho lần gửi này");
        return getFor(user,ids.getFirst());
    }
    private OrderResponse getFor(Actor user,Long id){
        var headers=jdbc.query(HEADERS+" WHERE o.id=?",this::header,id);
        if(headers.isEmpty())throw error(HttpStatus.NOT_FOUND,"Không tìm thấy đơn hàng");
        OrderResponse order=headers.getFirst();requireOwnerOrStaff(user,order.khachHangId());return withItems(headers).getFirst();
    }

    @Transactional(timeout=10)
    public OrderResponse checkout(Authentication auth,OrderRequest request){
        Actor user=actor(auth);
        if(!user.role().equals("KHACH_HANG"))throw error(HttpStatus.FORBIDDEN,"Chỉ tài khoản khách hàng được đặt hàng");
        SortedMap<Long,Integer> quantities=new TreeMap<>();
        for(OrderRequest.Item item:request.items()){
            int quantity=quantities.merge(item.sachId(),item.soLuong(),(left,right) -> Objects.requireNonNull(left)+Objects.requireNonNull(right));
            if(quantity>99)throw error(HttpStatus.BAD_REQUEST,"Mỗi sách được đặt tối đa 99 cuốn");
        }
        jdbc.queryForObject(lock("SELECT id FROM khach_hang WHERE id=?"),Long.class,user.customerId());
        String code=orderCode(user.customerId(),request.clientRequestId());
        var existing=jdbc.query("SELECT id FROM don_hang WHERE ma_don_hang=?",(r,i)->r.getLong(1),code);
        if(!existing.isEmpty()){
            OrderResponse previous=getFor(user,existing.getFirst());
            boolean same=previous.tenNguoiNhan().equals(request.tenNguoiNhan().strip())
                    &&previous.soDienThoaiNhan().equals(request.soDienThoaiNhan().strip())
                    &&previous.diaChiGiao().equals(request.diaChiGiao().strip())
                    &&Objects.equals(normalizeNote(previous.ghiChu()),normalizeNote(request.ghiChu()));
            Map<Long,Integer> previousItems=new TreeMap<>();previous.items().forEach(item->previousItems.put(item.sachId(),item.soLuong()));
            if(!same||!previousItems.equals(quantities))throw error(HttpStatus.CONFLICT,"Mã gửi đơn đã được dùng cho nội dung khác");
            return previous;
        }
        List<OrderResponse.Item> items=new ArrayList<>();BigDecimal total=BigDecimal.ZERO;
        for(var entry:quantities.entrySet()){
            var rows=jdbc.query(lock("""
                    SELECT s.*,d.trang_thai AS danh_muc_hoat_dong,n.trang_thai AS nxb_hoat_dong
                    FROM sach s JOIN danh_muc d ON d.id=s.danh_muc_id
                    JOIN nha_xuat_ban n ON n.id=s.nha_xuat_ban_id WHERE s.id=?
                    """),(r,i)->new Book(r.getLong("id"),r.getString("ma_sach"),r.getString("ten_sach"),r.getString("hinh_anh"),
                    r.getBigDecimal("gia_ban"),r.getInt("so_luong_ton"),r.getBoolean("trang_thai"),
                    r.getBoolean("danh_muc_hoat_dong"),r.getBoolean("nxb_hoat_dong")),entry.getKey());
            if(rows.isEmpty())throw error(HttpStatus.CONFLICT,"Một sách trong giỏ không còn tồn tại");
            Book book=rows.getFirst();
            if(!book.active()||!book.categoryActive()||!book.publisherActive())throw error(HttpStatus.CONFLICT,"Sách "+book.name()+" đang ngừng kinh doanh");
            if(book.stock()<entry.getValue())throw error(HttpStatus.CONFLICT,"Sách "+book.name()+" chỉ còn "+book.stock()+" cuốn");
            BigDecimal line=book.price().multiply(BigDecimal.valueOf(entry.getValue()));total=total.add(line);
            if(total.compareTo(MAX_TOTAL)>0)throw error(HttpStatus.BAD_REQUEST,"Tổng tiền đơn hàng vượt giới hạn");
            items.add(new OrderResponse.Item(book.id(),book.code(),book.name(),book.image(),entry.getValue(),book.price(),line,"SACH_MOI",null,null));
        }
        BigDecimal orderTotal=total;var key=new GeneratedKeyHolder();
        jdbc.update(connection->{
            PreparedStatement statement=connection.prepareStatement("""
                    INSERT INTO don_hang(ma_don_hang,khach_hang_id,ten_nguoi_nhan,so_dien_thoai_nhan,dia_chi_giao,tong_tien,ghi_chu)
                    VALUES(?,?,?,?,?,?,?)
                    """,new String[]{"id"});
            statement.setString(1,code);statement.setLong(2,user.customerId());statement.setString(3,request.tenNguoiNhan().strip());
            statement.setString(4,request.soDienThoaiNhan().strip());statement.setString(5,request.diaChiGiao().strip());
            statement.setBigDecimal(6,orderTotal);statement.setString(7,normalizeNote(request.ghiChu()));return statement;
        },key);
        long id=Objects.requireNonNull(key.getKey()).longValue();
        for(var item:items){
            int changed=jdbc.update("UPDATE sach SET so_luong_ton=so_luong_ton-? WHERE id=? AND so_luong_ton>=?",item.soLuong(),item.sachId(),item.soLuong());
            if(changed!=1)throw error(HttpStatus.CONFLICT,"Tồn kho vừa thay đổi, vui lòng đặt lại");
            jdbc.update("INSERT INTO chi_tiet_don_hang(don_hang_id,sach_id,so_luong,don_gia) VALUES(?,?,?,?)",id,item.sachId(),item.soLuong(),item.donGia());
        }
        return getFor(user,id);
    }

    private record Book(long id,String code,String name,String image,BigDecimal price,int stock,
                        boolean active,boolean categoryActive,boolean publisherActive){}
    private record LockedOrder(long customerId,String state){}
    @Transactional(timeout=10)
    public OrderResponse changeStatus(Authentication auth,Long id,String state){
        Actor user=actor(auth);
        if(state==null||!STATUSES.contains(state))throw error(HttpStatus.BAD_REQUEST,"Trạng thái đơn hàng không hợp lệ");
        var rows=jdbc.query(lock("SELECT khach_hang_id,trang_thai FROM don_hang WHERE id=?"),
                (r,i)->new LockedOrder(r.getLong(1),r.getString(2)),id);
        if(rows.isEmpty())throw error(HttpStatus.NOT_FOUND,"Không tìm thấy đơn hàng");
        LockedOrder order=rows.getFirst();requireOwnerOrStaff(user,order.customerId());
        if(!user.staff()&&!state.equals("DA_HUY"))throw error(HttpStatus.FORBIDDEN,"Khách hàng chỉ được hủy đơn chưa xác nhận");
        if(state.equals(order.state()))return getFor(user,id);
        if(!user.staff()&&!order.state().equals("CHO_XAC_NHAN"))throw error(HttpStatus.CONFLICT,"Đơn hàng đã được xử lý");
        boolean allowed=switch(order.state()){
            case "CHO_XAC_NHAN" -> state.equals("DA_XAC_NHAN")||state.equals("DA_HUY");
            case "DA_XAC_NHAN" -> state.equals("DANG_GIAO")||state.equals("DA_HUY");
            case "DANG_GIAO" -> state.equals("HOAN_THANH");
            default -> false;
        };
        if(!allowed)throw error(HttpStatus.CONFLICT,"Không thể chuyển từ "+order.state()+" sang "+state);
        if(state.equals("DA_HUY")){
            var items=jdbc.query("SELECT sach_id,so_luong FROM chi_tiet_don_hang WHERE don_hang_id=? ORDER BY sach_id",
                    (r,i)->new long[]{r.getLong(1),r.getInt(2)},id);
            for(long[] item:items){
                int stock=jdbc.queryForObject(lock("SELECT so_luong_ton FROM sach WHERE id=?"),Integer.class,item[0]);
                if((long)stock+item[1]>Integer.MAX_VALUE)throw error(HttpStatus.CONFLICT,"Tồn kho vượt giới hạn");
                jdbc.update("UPDATE sach SET so_luong_ton=so_luong_ton+? WHERE id=?",item[1],item[0]);
            }
            jdbc.update("""
                    UPDATE sach_cu SET trang_thai='DANG_BAN' WHERE id IN (
                        SELECT sach_cu_id FROM chi_tiet_don_hang_sach_cu WHERE don_hang_id=?)
                    AND trang_thai='DA_BAN'
                    """,id);
        }
        if(state.equals("HOAN_THANH")){
            jdbc.update("""
                    UPDATE ban_sao_sach SET trang_thai='DA_BAN' WHERE id IN (
                        SELECT sc.ban_sao_id FROM sach_cu sc JOIN chi_tiet_don_hang_sach_cu c ON c.sach_cu_id=sc.id
                        WHERE c.don_hang_id=?) AND trang_thai='CHO_BAN_CU'
                    """,id);
        }
        if(user.staff())jdbc.update("UPDATE don_hang SET trang_thai=?,nhan_vien_id=? WHERE id=?",state,user.id(),id);
        else jdbc.update("UPDATE don_hang SET trang_thai=? WHERE id=?",state,id);
        return getFor(user,id);
    }

    private String lock(String sql){return sqlite?sql:sql+" FOR UPDATE";}
    private void requireOwnerOrStaff(Actor user,long customerId){
        if(!user.staff()&&!Objects.equals(user.customerId(),customerId))throw error(HttpStatus.NOT_FOUND,"Không tìm thấy đơn hàng");
    }
    private OrderResponse header(ResultSet r,int index)throws SQLException{
        return new OrderResponse(r.getLong("id"),r.getString("ma_don_hang"),r.getLong("khach_hang_id"),r.getString("ho_ten_khach_hang"),
                r.getString("trang_thai"),r.getString("ten_nguoi_nhan"),r.getString("so_dien_thoai_nhan"),r.getString("dia_chi_giao"),
                r.getBigDecimal("tong_tien"),r.getString("ghi_chu"),r.getTimestamp("ngay_dat").toLocalDateTime(),List.of());
    }
    private List<OrderResponse> withItems(List<OrderResponse> headers){
        if(headers.isEmpty())return headers;Map<Long,List<OrderResponse.Item>> items=new HashMap<>();
        String placeholders=String.join(",",Collections.nCopies(headers.size(),"?"));
        jdbc.query("""
                SELECT c.don_hang_id,c.sach_id,s.ma_sach,s.ten_sach,s.hinh_anh,c.so_luong,c.don_gia
                FROM chi_tiet_don_hang c JOIN sach s ON s.id=c.sach_id WHERE c.don_hang_id IN (
                """+placeholders+") ORDER BY c.don_hang_id,c.sach_id",r->{
            int qty=r.getInt("so_luong");BigDecimal price=r.getBigDecimal("don_gia");
            items.computeIfAbsent(r.getLong("don_hang_id"),k->new ArrayList<>()).add(new OrderResponse.Item(r.getLong("sach_id"),
                    r.getString("ma_sach"),r.getString("ten_sach"),r.getString("hinh_anh"),qty,price,price.multiply(BigDecimal.valueOf(qty)),"SACH_MOI",null,null));
        },headers.stream().map(order -> Objects.requireNonNull(order).id()).toArray());
        jdbc.query("""
                SELECT c.don_hang_id,sc.id AS sach_cu_id,b.sach_id,s.ma_sach,s.ten_sach,
                    COALESCE(sc.hinh_anh,s.hinh_anh) AS hinh_anh,c.don_gia,sc.tinh_trang
                FROM chi_tiet_don_hang_sach_cu c JOIN sach_cu sc ON sc.id=c.sach_cu_id
                JOIN ban_sao_sach b ON b.id=sc.ban_sao_id JOIN sach s ON s.id=b.sach_id
                WHERE c.don_hang_id IN (
                """+placeholders+") ORDER BY c.don_hang_id,sc.id",r->{
            BigDecimal price=r.getBigDecimal("don_gia");items.computeIfAbsent(r.getLong("don_hang_id"),k->new ArrayList<>()).add(
                    new OrderResponse.Item(r.getLong("sach_id"),r.getString("ma_sach"),r.getString("ten_sach"),r.getString("hinh_anh"),1,price,price,"SACH_CU",r.getLong("sach_cu_id"),r.getString("tinh_trang")));
        },headers.stream().map(order -> Objects.requireNonNull(order).id()).toArray());
        return headers.stream().map(o->new OrderResponse(o.id(),o.maDonHang(),o.khachHangId(),o.hoTenKhachHang(),o.trangThai(),o.tenNguoiNhan(),
                o.soDienThoaiNhan(),o.diaChiGiao(),o.tongTien(),o.ghiChu(),o.ngayDat(),List.copyOf(items.getOrDefault(o.id(),List.of())))).toList();
    }
    private static ResponseStatusException error(HttpStatus status,String message){return new ResponseStatusException(status,message);}
    private static String normalizeNote(String value){return value==null||value.isBlank()?null:value.strip();}
    private static String orderCode(long customerId,String requestId){
        try{
            byte[] hash=MessageDigest.getInstance("SHA-256").digest((customerId+":"+requestId.toLowerCase(Locale.ROOT)).getBytes(StandardCharsets.UTF_8));
            return "DH"+HexFormat.of().formatHex(hash).substring(0,28).toUpperCase(Locale.ROOT);
        }catch(NoSuchAlgorithmException impossible){throw new IllegalStateException(impossible);}
    }
}
