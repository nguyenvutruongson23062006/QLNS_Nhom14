package vn.nhom14.qlns.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
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
public class MembershipService {
    public static final BigDecimal ANNUAL_FEE=new BigDecimal("50000.00");
    private static final Set<String> CARD_STATES=Set.of("CHO_PHAT_HANH","CHO_NHAN","DANG_GIAO","DA_KICH_HOAT","DA_HUY");
    private final JdbcTemplate jdbc;
    private final CurrentActorService actors;
    private final PaymentService payments;
    private final boolean sqlite;

    public MembershipService(JdbcTemplate jdbc,CurrentActorService actors,PaymentService payments) {
        this.jdbc=jdbc;this.actors=actors;this.payments=payments;
        this.sqlite=Boolean.TRUE.equals(jdbc.execute((org.springframework.jdbc.core.ConnectionCallback<Boolean>) connection ->
                connection.getMetaData().getDatabaseProductName().toLowerCase(Locale.ROOT).contains("sqlite")));
    }

    public MembershipResponse mine(Authentication authentication) {
        long customerId=actors.require(authentication).requireCustomer();
        var ids=jdbc.query("SELECT id FROM hoi_vien WHERE khach_hang_id=?",(result,index)->result.getLong(1),customerId);
        if(ids.isEmpty())throw error(HttpStatus.NOT_FOUND,"Bạn chưa đăng ký hội viên");
        return get(ids.getFirst());
    }

    public List<MembershipResponse> list(Authentication authentication,String state) {
        actors.require(authentication).requireStaff();
        String normalized=state==null?"":state.strip();
        if(!normalized.isEmpty()&&!Set.of("CHO_THANH_TOAN","DANG_HOAT_DONG","HET_HAN","DA_HUY").contains(normalized))
            throw error(HttpStatus.BAD_REQUEST,"Trạng thái hội viên không hợp lệ");
        List<Long> ids=normalized.isEmpty()
                ?jdbc.query("SELECT id FROM hoi_vien ORDER BY id DESC",(result,index)->result.getLong(1))
                :jdbc.query("SELECT id FROM hoi_vien WHERE trang_thai=? ORDER BY id DESC",(result,index)->result.getLong(1),normalized);
        return ids.stream().map(id -> get(Objects.requireNonNull(id))).toList();
    }

    @Transactional(timeout=10)
    public MembershipResponse register(Authentication authentication,Long requestedCustomerId,MembershipRegistrationRequest request) {
        var actor=actors.require(authentication);long customerId;
        if(requestedCustomerId==null)customerId=actor.requireCustomer();
        else {actor.requireStaff();customerId=requestedCustomerId;}
        var customers=jdbc.query(lock("SELECT ho_ten,so_dien_thoai,dia_chi FROM khach_hang WHERE id=?"),
                (result,index)->new Customer(result.getString(1),result.getString(2),result.getString(3)),customerId);
        if(customers.isEmpty())throw error(HttpStatus.NOT_FOUND,"Không tìm thấy khách hàng");
        if(jdbc.queryForObject("SELECT COUNT(*) FROM hoi_vien WHERE khach_hang_id=?",Integer.class,customerId)>0)
            throw error(HttpStatus.CONFLICT,"Khách hàng đã có hồ sơ hội viên");
        Customer customer=customers.getFirst();String deliveryMethod=request.phuongThucNhan();
        String recipient=normalize(request.tenNguoiNhan()),phone=normalize(request.soDienThoai()),address=normalize(request.diaChi());
        if(deliveryMethod.equals("GIAO_HANG")) {
            if(recipient==null)recipient=normalize(customer.name());
            if(phone==null)phone=normalize(customer.phone());
            if(address==null)address=normalize(customer.address());
            if(recipient==null||phone==null||address==null)
                throw error(HttpStatus.BAD_REQUEST,"Nhận thẻ qua giao hàng cần đủ người nhận, số điện thoại và địa chỉ");
        }
        String membershipCode=code("HV",28);long membershipId=insert("""
                INSERT INTO hoi_vien(khach_hang_id,ma_hoi_vien,trang_thai,phi_thanh_vien)
                VALUES(?,?,'CHO_THANH_TOAN',?)
                """,statement->{statement.setLong(1,customerId);statement.setString(2,membershipCode);statement.setBigDecimal(3,ANNUAL_FEE);});
        String cardCode=code("THE",32);long cardId=insert("""
                INSERT INTO the_thanh_vien(hoi_vien_id,ma_the,token_quet,phuong_thuc_nhan,trang_thai)
                VALUES(?,?,?,?,'CHO_PHAT_HANH')
                """,statement->{statement.setLong(1,membershipId);statement.setString(2,cardCode);
            statement.setString(3,UUID.randomUUID().toString());statement.setString(4,deliveryMethod);});
        if(deliveryMethod.equals("GIAO_HANG"))jdbc.update("""
                INSERT INTO giao_the(the_thanh_vien_id,ten_nguoi_nhan,so_dien_thoai,dia_chi,phi_giao,trang_thai)
                VALUES(?,?,?,?,0,'CHO_GIAO')
                """,cardId,recipient,phone,address);
        String paymentCode=code("TT",30);String content="QLNS "+paymentCode;
        jdbc.update("""
                INSERT INTO thanh_toan(ma_thanh_toan,khach_hang_id,muc_dich,tham_chieu_loai,tham_chieu_id,
                    phuong_thuc,so_tien,noi_dung_chuyen_khoan,trang_thai)
                VALUES(?,?,'HOI_VIEN','HOI_VIEN',?,?,?,?, 'CHO_DOI_SOAT')
                """,paymentCode,customerId,membershipId,request.phuongThucThanhToan(),ANNUAL_FEE,content);
        return get(membershipId);
    }

    @Transactional(timeout=10)
    public MembershipResponse changeCardStatus(Authentication authentication,long membershipId,CardStatusRequest request) {
        actors.require(authentication).requireStaff();String next=request.trangThai().strip();
        if(!CARD_STATES.contains(next))throw error(HttpStatus.BAD_REQUEST,"Trạng thái thẻ không hợp lệ");
        var rows=jdbc.query(lock("""
                SELECT t.id,t.phuong_thuc_nhan,t.trang_thai,h.trang_thai AS hoi_vien_trang_thai
                FROM the_thanh_vien t JOIN hoi_vien h ON h.id=t.hoi_vien_id WHERE h.id=?
                """),(result,index)->new CardState(result.getLong(1),result.getString(2),result.getString(3),result.getString(4)),membershipId);
        if(rows.isEmpty())throw error(HttpStatus.NOT_FOUND,"Không tìm thấy thẻ hội viên");
        CardState card=rows.getFirst();
        if(!card.membershipState().equals("DANG_HOAT_DONG"))throw error(HttpStatus.CONFLICT,"Chỉ phát hành thẻ sau khi phí hội viên đã được đối soát");
        if(card.state().equals(next))return get(membershipId);
        boolean allowed=switch(card.state()) {
            case "CHO_PHAT_HANH" -> next.equals(card.method().equals("TAI_QUAY")?"CHO_NHAN":"DANG_GIAO")||next.equals("DA_HUY");
            case "CHO_NHAN","DANG_GIAO" -> next.equals("DA_KICH_HOAT")||next.equals("DA_HUY");
            default -> false;
        };
        if(!allowed)throw error(HttpStatus.CONFLICT,"Không thể chuyển thẻ từ "+card.state()+" sang "+next);
        if(next.equals("DANG_GIAO")&&request.maVanDon()!=null&&!request.maVanDon().isBlank())
            jdbc.update("UPDATE giao_the SET trang_thai='DANG_GIAO',ma_van_don=? WHERE the_thanh_vien_id=?",request.maVanDon().strip(),card.id());
        else if(next.equals("DA_KICH_HOAT")&&card.method().equals("GIAO_HANG"))
            jdbc.update("UPDATE giao_the SET trang_thai='DA_GIAO',ngay_giao=CURRENT_TIMESTAMP WHERE the_thanh_vien_id=?",card.id());
        else if(next.equals("DA_HUY")&&card.method().equals("GIAO_HANG"))
            jdbc.update("UPDATE giao_the SET trang_thai='DA_HUY' WHERE the_thanh_vien_id=?",card.id());
        jdbc.update("UPDATE the_thanh_vien SET trang_thai=?,ngay_phat_hanh=CASE WHEN ngay_phat_hanh IS NULL THEN CURRENT_TIMESTAMP ELSE ngay_phat_hanh END,ngay_kich_hoat=CASE WHEN ?='DA_KICH_HOAT' THEN CURRENT_TIMESTAMP ELSE ngay_kich_hoat END WHERE id=?",
                next,next,card.id());
        return get(membershipId);
    }

    private MembershipResponse get(long id) {
        var rows=jdbc.query("""
                SELECT h.*,k.ho_ten,t.id AS the_id,t.ma_the,t.phuong_thuc_nhan,t.trang_thai AS the_trang_thai,
                    g.ten_nguoi_nhan,g.so_dien_thoai,g.dia_chi,g.phi_giao,g.ma_van_don,g.trang_thai AS giao_trang_thai
                FROM hoi_vien h JOIN khach_hang k ON k.id=h.khach_hang_id
                JOIN the_thanh_vien t ON t.hoi_vien_id=h.id
                LEFT JOIN giao_the g ON g.the_thanh_vien_id=t.id WHERE h.id=?
                """,(result,index)->{
            String start=result.getString("ngay_bat_dau"),end=result.getString("ngay_het_han");
            MembershipResponse.Card card=new MembershipResponse.Card(result.getLong("the_id"),result.getString("ma_the"),
                    result.getString("phuong_thuc_nhan"),result.getString("the_trang_thai"));
            MembershipResponse.Delivery delivery=result.getString("giao_trang_thai")==null?null:new MembershipResponse.Delivery(
                    result.getString("ten_nguoi_nhan"),result.getString("so_dien_thoai"),result.getString("dia_chi"),
                    money(result.getBigDecimal("phi_giao")),result.getString("ma_van_don"),result.getString("giao_trang_thai"));
            return new MembershipResponse(result.getLong("id"),result.getLong("khach_hang_id"),result.getString("ho_ten"),
                    result.getString("ma_hoi_vien"),start==null?null:LocalDate.parse(start),end==null?null:LocalDate.parse(end),
                    result.getString("trang_thai"),money(result.getBigDecimal("phi_thanh_vien")),card,delivery,null);
        },id);
        if(rows.isEmpty())throw error(HttpStatus.NOT_FOUND,"Không tìm thấy hội viên");
        MembershipResponse member=rows.getFirst();PaymentResponse payment=payments.getLatestForReference("HOI_VIEN",id);
        return new MembershipResponse(member.id(),member.khachHangId(),member.hoTenKhachHang(),member.maHoiVien(),member.ngayBatDau(),
                member.ngayHetHan(),member.trangThai(),member.phiThanhVien(),member.card(),member.delivery(),payment);
    }

    private long insert(String sql,SqlBinder binder) {
        var key=new GeneratedKeyHolder();jdbc.update(connection->{PreparedStatement statement=connection.prepareStatement(sql,new String[]{"id"});
            binder.bind(statement);return statement;},key);return Objects.requireNonNull(key.getKey()).longValue();
    }
    private interface SqlBinder { void bind(PreparedStatement statement)throws SQLException; }
    private record Customer(String name,String phone,String address) {}
    private record CardState(long id,String method,String state,String membershipState) {}
    private String lock(String sql){return sqlite?sql:sql+" FOR UPDATE";}
    private static String code(String prefix,int randomLength){return prefix+UUID.randomUUID().toString().replace("-","").substring(0,randomLength).toUpperCase(Locale.ROOT);}
    private static String normalize(String value){return value==null||value.isBlank()?null:value.strip();}
    private static BigDecimal money(BigDecimal value){return value==null?null:value.setScale(2,RoundingMode.UNNECESSARY);}
    private static ResponseStatusException error(HttpStatus status,String message){return new ResponseStatusException(status,message);}
}
