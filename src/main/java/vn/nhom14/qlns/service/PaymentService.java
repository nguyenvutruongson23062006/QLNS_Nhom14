package vn.nhom14.qlns.service;

import java.net.URLEncoder;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import vn.nhom14.qlns.dto.PaymentReconcileRequest;
import vn.nhom14.qlns.dto.PaymentResponse;
import vn.nhom14.qlns.security.CurrentActorService;

@Service
@Transactional(readOnly=true,timeout=10)
public class PaymentService {
    private static final Set<String> STATES=Set.of("CHO_THANH_TOAN","CHO_DOI_SOAT","DA_THANH_TOAN","THAT_BAI","DA_HOAN_TIEN");
    private final JdbcTemplate jdbc;
    private final CurrentActorService actors;
    private final boolean sqlite;
    private final String bankBin;
    private final String accountNumber;
    private final String accountName;

    public PaymentService(JdbcTemplate jdbc, CurrentActorService actors,
            @Value("${QLNS_VIETQR_BANK_BIN:970422}") String bankBin,
            @Value("${QLNS_VIETQR_ACCOUNT_NUMBER:0387196095}") String accountNumber,
            @Value("${QLNS_VIETQR_ACCOUNT_NAME:VUONG NGUYEN TRUONG HUNG}") String accountName) {
        this.jdbc=jdbc;this.actors=actors;this.bankBin=bankBin.strip();
        this.accountNumber=accountNumber.strip();this.accountName=accountName.strip();
        this.sqlite=Boolean.TRUE.equals(jdbc.execute((org.springframework.jdbc.core.ConnectionCallback<Boolean>) connection ->
                connection.getMetaData().getDatabaseProductName().toLowerCase(Locale.ROOT).contains("sqlite")));
    }

    public List<PaymentResponse> list(Authentication authentication,String state) {
        actors.require(authentication).requireStaff();
        String normalized=state==null?"":state.strip();
        if(!normalized.isEmpty()&&!STATES.contains(normalized))
            throw error(HttpStatus.BAD_REQUEST,"Trạng thái thanh toán không hợp lệ");
        if(normalized.isEmpty()) return jdbc.query("SELECT * FROM thanh_toan ORDER BY id DESC",this::map);
        return jdbc.query("SELECT * FROM thanh_toan WHERE trang_thai=? ORDER BY id DESC",this::map,normalized);
    }

    public PaymentResponse getLatestForReference(String type,long referenceId) {
        var rows=jdbc.query("SELECT * FROM thanh_toan WHERE tham_chieu_loai=? AND tham_chieu_id=? ORDER BY id DESC LIMIT 1",
                this::map,type,referenceId);
        return rows.isEmpty()?null:rows.getFirst();
    }

    @Transactional(timeout=10)
    public PaymentResponse createOrderPayment(long customerId,long orderId,BigDecimal amount) {
        PaymentResponse existing=getLatestForReference("DON_HANG",orderId);
        if(existing!=null)return existing;
        String paymentCode="TT"+UUID.randomUUID().toString().replace("-","").substring(0,28).toUpperCase(Locale.ROOT);
        jdbc.update("""
                INSERT INTO thanh_toan(ma_thanh_toan,khach_hang_id,muc_dich,tham_chieu_loai,tham_chieu_id,
                    phuong_thuc,so_tien,noi_dung_chuyen_khoan,trang_thai)
                VALUES(?,?,'DON_HANG','DON_HANG',?,'VIETQR',?,?,'CHO_DOI_SOAT')
                """,paymentCode,customerId,orderId,amount,"QLNS "+paymentCode);
        return getLatestForReference("DON_HANG",orderId);
    }

    @Transactional(timeout=10)
    public PaymentResponse reconcile(Authentication authentication,long id,PaymentReconcileRequest request) {
        var actor=actors.require(authentication);actor.requireStaff();
        var rows=jdbc.query(lock("SELECT * FROM thanh_toan WHERE id=?"),this::map,id);
        if(rows.isEmpty())throw error(HttpStatus.NOT_FOUND,"Không tìm thấy khoản thanh toán");
        PaymentResponse payment=rows.getFirst();String providerReference=request.maGiaoDichNhaCungCap().strip();
        if(payment.trangThai().equals("DA_THANH_TOAN")) {
            if(Objects.equals(payment.maGiaoDichNhaCungCap(),providerReference))return payment;
            throw error(HttpStatus.CONFLICT,"Khoản thanh toán đã được đối soát bằng mã giao dịch khác");
        }
        if(!Set.of("CHO_THANH_TOAN","CHO_DOI_SOAT").contains(payment.trangThai()))
            throw error(HttpStatus.CONFLICT,"Khoản thanh toán không còn ở trạng thái chờ đối soát");
        if(jdbc.queryForObject("SELECT COUNT(*) FROM thanh_toan WHERE ma_giao_dich_nha_cung_cap=? AND id<>?",Integer.class,providerReference,id)>0)
            throw error(HttpStatus.CONFLICT,"Mã giao dịch đã được dùng để đối soát khoản khác");
        jdbc.update("""
                UPDATE thanh_toan SET trang_thai='DA_THANH_TOAN',ma_giao_dich_nha_cung_cap=?,
                xac_nhan_boi=?,ngay_xac_nhan=CURRENT_TIMESTAMP,
                ghi_chu=CASE WHEN tham_chieu_loai='GIA_HAN' THEN ghi_chu ELSE ? END WHERE id=?
                """,providerReference,actor.accountId(),normalize(request.ghiChu()),id);
        if(payment.mucDich().equals("HOI_VIEN"))activateMembership(payment.thamChieuId());
        else if(payment.mucDich().equals("THUE_SACH")&&payment.thamChieuLoai().equals("PHIEU_THUE"))
            releasePaidRental(payment.thamChieuId(),actor.accountId());
        else if(payment.mucDich().equals("THUE_SACH")&&payment.thamChieuLoai().equals("GIA_HAN"))
            applyPaidRenewal(payment.id(),payment.thamChieuId(),payment.soTien(),actor.accountId());
        return jdbc.query("SELECT * FROM thanh_toan WHERE id=?",this::map,id).getFirst();
    }

    private void activateMembership(long membershipId) {
        var states=jdbc.query(lock("SELECT trang_thai FROM hoi_vien WHERE id=?"),(result,index)->result.getString(1),membershipId);
        if(states.isEmpty())throw error(HttpStatus.CONFLICT,"Hồ sơ hội viên của khoản thanh toán không còn tồn tại");
        String state=states.getFirst();
        if(state.equals("DANG_HOAT_DONG"))return;
        if(!state.equals("CHO_THANH_TOAN"))throw error(HttpStatus.CONFLICT,"Hồ sơ hội viên không còn chờ thanh toán");
        LocalDate start=LocalDate.now();
        jdbc.update("UPDATE hoi_vien SET trang_thai='DANG_HOAT_DONG',ngay_bat_dau=?,ngay_het_han=?,ngay_cap_nhat=CURRENT_TIMESTAMP WHERE id=?",
                start,start.plusYears(1),membershipId);
    }

    private void releasePaidRental(long rentalId,long accountId) {
        var states=jdbc.query(lock("SELECT trang_thai FROM phieu_thue WHERE id=?"),(result,index)->result.getString(1),rentalId);
        if(states.isEmpty())throw error(HttpStatus.CONFLICT,"Phiếu thuê của khoản thanh toán không còn tồn tại");
        if(states.getFirst().equals("CHO_THANH_TOAN")) {
            jdbc.update("UPDATE phieu_thue SET trang_thai='CHO_NHAN_SACH' WHERE id=?",rentalId);
            jdbc.update("INSERT INTO nhat_ky_thue(phieu_thue_id,trang_thai_cu,trang_thai_moi,nguoi_thuc_hien_id,ghi_chu) VALUES(?,'CHO_THANH_TOAN','CHO_NHAN_SACH',?,'Đã đối soát phí thuê')",rentalId,accountId);
        }
    }

    private void applyPaidRenewal(long paymentId,long rentalId,java.math.BigDecimal fee,long accountId) {
        String note=jdbc.queryForObject("SELECT ghi_chu FROM thanh_toan WHERE id=?",String.class,paymentId);
        if(note==null||!note.matches("GIA_HAN:[1-9][0-9]?"))throw error(HttpStatus.CONFLICT,"Khoản gia hạn thiếu số ngày hợp lệ");
        int days=Integer.parseInt(note.substring("GIA_HAN:".length()));
        var states=jdbc.query(lock("SELECT trang_thai FROM phieu_thue WHERE id=?"),(result,index)->result.getString(1),rentalId);
        if(states.isEmpty()||!states.getFirst().equals("DANG_THUE"))throw error(HttpStatus.CONFLICT,"Phiếu không còn ở trạng thái đang thuê để gia hạn");
        var details=jdbc.query(lock("SELECT id,ngay_hen_tra,so_lan_gia_han,so_lan_gia_han_toi_da FROM chi_tiet_phieu_thue WHERE phieu_thue_id=?"),
                (result,index)->new Object[]{result.getLong(1),result.getString(2),result.getInt(3),result.getInt(4)},rentalId);
        for(Object[] detail:details)if((int)detail[2]>=(int)detail[3])throw error(HttpStatus.CONFLICT,"Một bản sách đã hết lượt gia hạn");
        for(Object[] detail:details)jdbc.update("UPDATE chi_tiet_phieu_thue SET ngay_hen_tra=?,so_lan_gia_han=so_lan_gia_han+1 WHERE id=?",
                LocalDate.parse((String)detail[1]).plusDays(days),(long)detail[0]);
        jdbc.update("UPDATE phieu_thue SET tong_tien_thue=tong_tien_thue+? WHERE id=?",fee,rentalId);
        jdbc.update("INSERT INTO nhat_ky_thue(phieu_thue_id,trang_thai_cu,trang_thai_moi,nguoi_thuc_hien_id,ghi_chu) VALUES(?,'DANG_THUE','DANG_THUE',?,?)",
                rentalId,accountId,"Đã đối soát và gia hạn "+days+" ngày");
    }

    public PaymentResponse getLatestForRental(long rentalId) {
        var rows=jdbc.query("SELECT * FROM thanh_toan WHERE tham_chieu_id=? AND tham_chieu_loai IN ('PHIEU_THUE','GIA_HAN','TRA_PHIEU_THUE') ORDER BY id DESC LIMIT 1",this::map,rentalId);
        return rows.isEmpty()?null:rows.getFirst();
    }

    private PaymentResponse map(ResultSet result,int index)throws SQLException {
        var created=result.getTimestamp("ngay_tao");var confirmed=result.getTimestamp("ngay_xac_nhan");
        String method=result.getString("phuong_thuc");String content=result.getString("noi_dung_chuyen_khoan");
        var amount=result.getBigDecimal("so_tien").setScale(2,RoundingMode.UNNECESSARY);
        boolean configured=method.equals("VIETQR")&&bankBin.matches("[0-9]{6}")&&accountNumber.matches("[0-9]{3,30}");
        String qrUrl=null,note=null;
        if(method.equals("VIETQR")) {
            if(configured) {
                qrUrl="https://img.vietqr.io/image/"+bankBin+"-"+accountNumber+"-compact2.png?amount="
                        +amount.toPlainString()+"&addInfo="+encode(content)
                        +(accountName.isBlank()?"":"&accountName="+encode(accountName));
                note="QR được tạo theo cấu hình cửa hàng; thanh toán chỉ hoàn tất sau khi nhân viên đối soát.";
            } else note="Cửa hàng chưa cấu hình tài khoản VietQR. Khoản thanh toán vẫn ở trạng thái chờ đối soát.";
        }
        return new PaymentResponse(result.getLong("id"),result.getString("ma_thanh_toan"),result.getLong("khach_hang_id"),
                result.getString("muc_dich"),result.getString("tham_chieu_loai"),result.getLong("tham_chieu_id"),method,
                amount,content,result.getString("trang_thai"),
                result.getString("ma_giao_dich_nha_cung_cap"),created.toLocalDateTime(),
                confirmed==null?null:confirmed.toLocalDateTime(),qrUrl,configured,note);
    }

    private String lock(String sql){return sqlite?sql:sql+" FOR UPDATE";}
    private static String normalize(String value){return value==null||value.isBlank()?null:value.strip();}
    private static String encode(String value){return URLEncoder.encode(value,StandardCharsets.UTF_8);}
    private static ResponseStatusException error(HttpStatus status,String message){return new ResponseStatusException(status,message);}
}
