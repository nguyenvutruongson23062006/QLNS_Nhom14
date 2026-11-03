package vn.nhom14.qlns.service;

import java.math.*;
import java.nio.charset.StandardCharsets;
import java.security.*;
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
import vn.nhom14.qlns.security.CurrentActorService;

@Service
@Transactional(readOnly=true,timeout=10)
public class UsedBookService {
    private static final BigDecimal MAX_TOTAL=new BigDecimal("9999999999999.99");
    private static final String SELECT="""
            SELECT sc.id,sc.ban_sao_id,b.ma_ban_sao,b.sach_id,s.ma_sach,s.ten_sach,s.tac_gia,
                COALESCE(sc.hinh_anh,s.hinh_anh) AS hinh_anh,s.gia_ban AS gia_goc,
                sc.gia_de_xuat,sc.gia_ban,sc.tinh_trang,sc.mo_ta,sc.trang_thai
            FROM sach_cu sc JOIN ban_sao_sach b ON b.id=sc.ban_sao_id JOIN sach s ON s.id=b.sach_id
            """;
    private final JdbcTemplate jdbc;private final CurrentActorService actors;private final OrderService orders;private final boolean sqlite;
    public UsedBookService(JdbcTemplate jdbc,CurrentActorService actors,OrderService orders){this.jdbc=jdbc;this.actors=actors;this.orders=orders;this.sqlite=Boolean.TRUE.equals(jdbc.execute((org.springframework.jdbc.core.ConnectionCallback<Boolean>) c->c.getMetaData().getDatabaseProductName().toLowerCase(Locale.ROOT).contains("sqlite")));}

    public List<UsedBookResponse> listForSale(){return jdbc.query(SELECT+" WHERE sc.trang_thai='DANG_BAN' ORDER BY sc.ngay_duyet DESC,sc.id DESC",this::map);}
    public List<UsedBookResponse> manage(Authentication auth){actors.require(auth).requireStaff();return jdbc.query(SELECT+" ORDER BY sc.id DESC",this::map);}
    public List<UsedBookResponse> candidates(Authentication auth){actors.require(auth).requireStaff();return jdbc.query("""
            SELECT 0 AS id,b.id AS ban_sao_id,b.ma_ban_sao,b.sach_id,s.ma_sach,s.ten_sach,s.tac_gia,
                s.hinh_anh,s.gia_ban AS gia_goc,0 AS gia_de_xuat,NULL AS gia_ban,b.tinh_trang,
                b.ghi_chu AS mo_ta,'UNG_VIEN' AS trang_thai
            FROM ban_sao_sach b JOIN sach s ON s.id=b.sach_id
            WHERE b.trang_thai='SAN_SANG' AND b.hoat_dong=TRUE AND b.tinh_trang IN ('NHU_MOI','TOT','KHA','CU')
              AND NOT EXISTS(SELECT 1 FROM sach_cu sc WHERE sc.ban_sao_id=b.id)
              AND EXISTS(SELECT 1 FROM chi_tiet_phieu_thue ct WHERE ct.ban_sao_id=b.id AND ct.ngay_tra_thuc_te IS NOT NULL)
            ORDER BY s.ten_sach,b.id
            """,(r,i)->candidate(r));}

    @Transactional(timeout=10)
    public UsedBookResponse create(Authentication auth,UsedBookCreateRequest request){
        actors.require(auth).requireStaff();var rows=jdbc.query(lock("""
                SELECT b.id,b.tinh_trang,b.trang_thai,s.gia_ban FROM ban_sao_sach b JOIN sach s ON s.id=b.sach_id
                WHERE b.id=?
                """),(r,i)->new Copy(r.getLong(1),r.getString(2),r.getString(3),r.getBigDecimal(4)),request.banSaoId());
        if(rows.isEmpty())throw error(HttpStatus.NOT_FOUND,"Không tìm thấy bản sao");Copy copy=rows.getFirst();
        if(!copy.state().equals("SAN_SANG")||!Set.of("NHU_MOI","TOT","KHA","CU").contains(copy.condition()))throw error(HttpStatus.CONFLICT,"Bản sao chưa đủ điều kiện bán sách cũ");
        if(jdbc.queryForObject("SELECT COUNT(*) FROM chi_tiet_phieu_thue WHERE ban_sao_id=? AND ngay_tra_thuc_te IS NOT NULL",Integer.class,copy.id())==0)throw error(HttpStatus.CONFLICT,"Chỉ bản sao đã từng được thuê và hoàn trả mới được đưa vào sách cũ");
        BigDecimal suggested=suggest(copy.original(),copy.condition());var key=new GeneratedKeyHolder();
        jdbc.update(connection->{PreparedStatement s=connection.prepareStatement("INSERT INTO sach_cu(ban_sao_id,gia_de_xuat,tinh_trang,mo_ta,hinh_anh) VALUES(?,?,?,?,?)",new String[]{"id"});s.setLong(1,copy.id());s.setBigDecimal(2,suggested);s.setString(3,copy.condition());s.setString(4,normalize(request.moTa()));s.setString(5,normalize(request.hinhAnh()));return s;},key);
        jdbc.update("UPDATE ban_sao_sach SET trang_thai='CHO_BAN_CU' WHERE id=? AND trang_thai='SAN_SANG'",copy.id());return get(Objects.requireNonNull(key.getKey()).longValue());
    }
    @Transactional(timeout=10)
    public UsedBookResponse approve(Authentication auth,long id,UsedBookApproveRequest request){
        var actor=actors.require(auth);actor.requireAdmin();UsedBookResponse item=locked(id);
        if(!item.trangThai().equals("CHO_DUYET"))throw error(HttpStatus.CONFLICT,"Chỉ danh mục chờ duyệt mới được mở bán");
        if(request.giaBan().compareTo(item.giaGoc())>=0)throw error(HttpStatus.BAD_REQUEST,"Giá sách cũ phải thấp hơn giá sách mới");
        jdbc.update("UPDATE sach_cu SET gia_ban=?,trang_thai='DANG_BAN',duyet_boi=?,ngay_duyet=CURRENT_TIMESTAMP WHERE id=?",request.giaBan(),actor.accountId(),id);return get(id);
    }
    @Transactional(timeout=10)
    public UsedBookResponse stop(Authentication auth,long id){
        actors.require(auth).requireStaff();UsedBookResponse item=locked(id);
        if(!Set.of("CHO_DUYET","DANG_BAN").contains(item.trangThai()))throw error(HttpStatus.CONFLICT,"Danh mục không còn có thể ngừng bán");
        jdbc.update("UPDATE sach_cu SET trang_thai='NGUNG_BAN' WHERE id=?",id);jdbc.update("UPDATE ban_sao_sach SET trang_thai='SAN_SANG' WHERE id=? AND trang_thai='CHO_BAN_CU'",item.banSaoId());return get(id);
    }
    @Transactional(timeout=10)
    public OrderResponse checkout(Authentication auth,UsedBookOrderRequest request){
        var actor=actors.require(auth);long customerId=actor.requireCustomer();SortedSet<Long> ids=new TreeSet<>(request.sachCuIds());
        if(ids.size()!=request.sachCuIds().size())throw error(HttpStatus.BAD_REQUEST,"Một cuốn sách cũ không thể xuất hiện hai lần trong đơn");
        jdbc.queryForObject(lock("SELECT id FROM khach_hang WHERE id=?"),Long.class,customerId);String code=orderCode(customerId,request.clientRequestId());
        var existing=jdbc.query("SELECT id FROM don_hang WHERE ma_don_hang=?",(r,i)->r.getLong(1),code);
        if(!existing.isEmpty()){
            OrderResponse previous=orders.get(auth,existing.getFirst());Set<Long> old=new TreeSet<>();previous.items().stream().filter(i->"SACH_CU".equals(i.loaiHang())).forEach(i->old.add(i.sachCuId()));
            if(!old.equals(ids)||!previous.tenNguoiNhan().equals(request.tenNguoiNhan().strip())||!previous.soDienThoaiNhan().equals(request.soDienThoaiNhan().strip())||!previous.diaChiGiao().equals(request.diaChiGiao().strip())||!Objects.equals(normalize(previous.ghiChu()),normalize(request.ghiChu())))throw error(HttpStatus.CONFLICT,"Mã gửi đơn đã được dùng cho nội dung khác");return previous;
        }
        List<UsedBookResponse> items=new ArrayList<>();BigDecimal total=BigDecimal.ZERO;
        for(long id:ids){UsedBookResponse item=locked(id);if(!item.trangThai().equals("DANG_BAN")||item.giaBan()==null)throw error(HttpStatus.CONFLICT,"Một cuốn sách cũ vừa được bán hoặc ngừng bán");items.add(item);total=total.add(item.giaBan());}
        if(total.compareTo(MAX_TOTAL)>0)throw error(HttpStatus.BAD_REQUEST,"Tổng tiền đơn hàng vượt giới hạn");
        BigDecimal orderTotal=total;var key=new GeneratedKeyHolder();
        jdbc.update(connection->{PreparedStatement s=connection.prepareStatement("INSERT INTO don_hang(ma_don_hang,khach_hang_id,ten_nguoi_nhan,so_dien_thoai_nhan,dia_chi_giao,tong_tien,ghi_chu) VALUES(?,?,?,?,?,?,?)",new String[]{"id"});s.setString(1,code);s.setLong(2,customerId);s.setString(3,request.tenNguoiNhan().strip());s.setString(4,request.soDienThoaiNhan().strip());s.setString(5,request.diaChiGiao().strip());s.setBigDecimal(6,orderTotal);s.setString(7,normalize(request.ghiChu()));return s;},key);
        long orderId=Objects.requireNonNull(key.getKey()).longValue();for(UsedBookResponse item:items){int changed=jdbc.update("UPDATE sach_cu SET trang_thai='DA_BAN' WHERE id=? AND trang_thai='DANG_BAN'",item.id());if(changed!=1)throw error(HttpStatus.CONFLICT,"Sách cũ vừa được khách khác đặt");jdbc.update("INSERT INTO chi_tiet_don_hang_sach_cu(don_hang_id,sach_cu_id,don_gia) VALUES(?,?,?)",orderId,item.id(),item.giaBan());}
        return orders.get(auth,orderId);
    }
    private UsedBookResponse locked(long id){var rows=jdbc.query(lock(SELECT+" WHERE sc.id=?"),this::map,id);if(rows.isEmpty())throw error(HttpStatus.NOT_FOUND,"Không tìm thấy sách cũ");return rows.getFirst();}
    private UsedBookResponse get(long id){var rows=jdbc.query(SELECT+" WHERE sc.id=?",this::map,id);if(rows.isEmpty())throw error(HttpStatus.NOT_FOUND,"Không tìm thấy sách cũ");return rows.getFirst();}
    private UsedBookResponse candidate(ResultSet r)throws SQLException{BigDecimal original=r.getBigDecimal("gia_goc");String condition=r.getString("tinh_trang");return new UsedBookResponse(0,r.getLong("ban_sao_id"),r.getString("ma_ban_sao"),r.getLong("sach_id"),r.getString("ma_sach"),r.getString("ten_sach"),r.getString("tac_gia"),r.getString("hinh_anh"),money(original),suggest(original,condition),null,condition,r.getString("mo_ta"),"UNG_VIEN");}
    private UsedBookResponse map(ResultSet r,int i)throws SQLException{return new UsedBookResponse(r.getLong("id"),r.getLong("ban_sao_id"),r.getString("ma_ban_sao"),r.getLong("sach_id"),r.getString("ma_sach"),r.getString("ten_sach"),r.getString("tac_gia"),r.getString("hinh_anh"),money(r.getBigDecimal("gia_goc")),money(r.getBigDecimal("gia_de_xuat")),money(r.getBigDecimal("gia_ban")),r.getString("tinh_trang"),r.getString("mo_ta"),r.getString("trang_thai"));}
    private static BigDecimal suggest(BigDecimal original,String condition){BigDecimal ratio=switch(condition){case "NHU_MOI"->new BigDecimal("0.70");case "TOT"->new BigDecimal("0.55");case "KHA"->new BigDecimal("0.40");default->new BigDecimal("0.25");};return original.multiply(ratio).setScale(0,RoundingMode.HALF_UP).setScale(2);}
    private static BigDecimal money(BigDecimal value){return value==null?null:value.setScale(2,RoundingMode.UNNECESSARY);}
    private String lock(String sql){return sqlite?sql:sql+" FOR UPDATE";}
    private static String normalize(String value){return value==null||value.isBlank()?null:value.strip();}
    private static String orderCode(long customerId,String requestId){try{byte[] hash=MessageDigest.getInstance("SHA-256").digest(("USED:"+customerId+":"+requestId.toLowerCase(Locale.ROOT)).getBytes(StandardCharsets.UTF_8));return "CU"+HexFormat.of().formatHex(hash).substring(0,28).toUpperCase(Locale.ROOT);}catch(NoSuchAlgorithmException e){throw new IllegalStateException(e);}}
    private static ResponseStatusException error(HttpStatus status,String message){return new ResponseStatusException(status,message);}
    private record Copy(long id,String condition,String state,BigDecimal original){}
}
