package vn.nhom14.qlns.service;

import java.math.*;
import java.sql.*;
import java.time.*;
import java.time.temporal.ChronoUnit;
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
public class RentalService {
    private static final Set<String> STATES=Set.of("CHO_XAC_NHAN","DA_DUYET","CHO_THANH_TOAN","CHO_NHAN_SACH","DANG_THUE","HOAN_THANH","DA_HUY","MAT_SACH","TRANH_CHAP");
    private static final int MAX_ACTIVE_COPIES=5;
    private final JdbcTemplate jdbc;private final CurrentActorService actors;private final PaymentService payments;private final boolean sqlite;
    public RentalService(JdbcTemplate jdbc,CurrentActorService actors,PaymentService payments){
        this.jdbc=jdbc;this.actors=actors;this.payments=payments;
        this.sqlite=Boolean.TRUE.equals(jdbc.execute((org.springframework.jdbc.core.ConnectionCallback<Boolean>) connection ->
                connection.getMetaData().getDatabaseProductName().toLowerCase(Locale.ROOT).contains("sqlite")));
    }

    public List<RentalResponse> list(Authentication authentication,String state){
        var actor=actors.require(authentication);String normalized=state==null?"":state.strip();
        if(!normalized.isEmpty()&&!STATES.contains(normalized))throw error(HttpStatus.BAD_REQUEST,"Trạng thái phiếu thuê không hợp lệ");
        String sql="SELECT p.id FROM phieu_thue p WHERE 1=1";List<Object> args=new ArrayList<>();
        if(!actor.staff()){sql+=" AND p.khach_hang_id=?";args.add(actor.requireCustomer());}
        if(!normalized.isEmpty()){sql+=" AND p.trang_thai=?";args.add(normalized);}
        sql+=" ORDER BY p.id DESC";
        return jdbc.query(sql,(result,index)->result.getLong(1),args.toArray()).stream().map(id -> getInternal(Objects.requireNonNull(id))).toList();
    }
    public RentalResponse get(Authentication authentication,long id){
        var actor=actors.require(authentication);RentalResponse rental=getInternal(id);
        if(!actor.staff()&&!Objects.equals(actor.requireCustomer(),rental.khachHangId()))throw error(HttpStatus.NOT_FOUND,"Không tìm thấy phiếu thuê");
        return rental;
    }

    @Transactional(timeout=10)
    public RentalResponse create(Authentication authentication,Long requestedCustomerId,RentalRequest request){
        var actor=actors.require(authentication);long customerId;String channel;
        if(requestedCustomerId==null){customerId=actor.requireCustomer();channel="ONLINE";}
        else {actor.requireStaff();customerId=requestedCustomerId;channel="TAI_QUAY";}
        try{UUID.fromString(request.clientRequestId());}catch(IllegalArgumentException ex){throw error(HttpStatus.BAD_REQUEST,"Mã gửi yêu cầu không hợp lệ");}
        if(jdbc.query(lock("SELECT id FROM khach_hang WHERE id=?"),(result,index)->result.getLong(1),customerId).isEmpty())
            throw error(HttpStatus.NOT_FOUND,"Không tìm thấy khách hàng");
        Customer customer=activeMember(customerId);
        SortedMap<Long,Integer> quantities=new TreeMap<>();int requested=0;
        for(var item:request.items()){int quantity=quantities.merge(item.sachId(),item.soLuong(),(left,right) -> Objects.requireNonNull(left)+Objects.requireNonNull(right));if(quantity>5)throw error(HttpStatus.BAD_REQUEST,"Mỗi đầu sách thuê tối đa 5 bản");requested+=item.soLuong();}
        int active=jdbc.queryForObject("SELECT COUNT(*) FROM chi_tiet_phieu_thue c JOIN phieu_thue p ON p.id=c.phieu_thue_id WHERE p.khach_hang_id=? AND p.trang_thai IN ('CHO_XAC_NHAN','DA_DUYET','CHO_THANH_TOAN','CHO_NHAN_SACH','DANG_THUE','TRANH_CHAP')",Integer.class,customerId);
        if(active+requested>MAX_ACTIVE_COPIES)throw error(HttpStatus.CONFLICT,"Mỗi hội viên chỉ được giữ tối đa "+MAX_ACTIVE_COPIES+" bản sách đang thuê");
        var existing=jdbc.query("SELECT id FROM phieu_thue WHERE khach_hang_id=? AND client_request_id=?",(result,index)->result.getLong(1),customerId,request.clientRequestId().toLowerCase(Locale.ROOT));
        if(!existing.isEmpty()){RentalResponse previous=getInternal(existing.getFirst());verifyReplay(previous,request,quantities);return previous;}
        String method=request.phuongThucNhan();String recipient=normalize(request.tenNguoiNhan()),phone=normalize(request.soDienThoaiNhan()),address=normalize(request.diaChiGiao());
        if(method.equals("GIAO_HANG")){
            if(recipient==null)recipient=customer.name();if(phone==null)phone=customer.phone();if(address==null)address=customer.address();
            if(recipient==null||phone==null||address==null)throw error(HttpStatus.BAD_REQUEST,"Giao sách cần đủ người nhận, số điện thoại và địa chỉ");
        }
        List<Allocation> allocations=new ArrayList<>();BigDecimal rentalTotal=BigDecimal.ZERO,depositTotal=BigDecimal.ZERO;
        for(var entry:quantities.entrySet()){
            var configs=jdbc.query(lock("""
                    SELECT c.*,s.ma_sach,s.ten_sach FROM cau_hinh_thue_sach c JOIN sach s ON s.id=c.sach_id
                    WHERE c.sach_id=? AND c.cho_thue=TRUE AND s.trang_thai=TRUE
                    """),(result,index)->new Config(result.getLong("sach_id"),result.getString("ma_sach"),result.getString("ten_sach"),
                    result.getBigDecimal("gia_thue_ngay"),result.getBigDecimal("tien_coc"),result.getInt("so_ngay_mac_dinh"),
                    result.getInt("so_lan_gia_han"),result.getBigDecimal("phi_tre_ngay")),entry.getKey());
            if(configs.isEmpty())throw error(HttpStatus.CONFLICT,"Một sách đã ngừng cho thuê hoặc chưa được cấu hình");
            Config config=configs.getFirst();var copies=jdbc.query(lock("SELECT id,ma_ban_sao,tinh_trang FROM ban_sao_sach WHERE sach_id=? AND trang_thai='SAN_SANG' AND hoat_dong=TRUE ORDER BY id LIMIT "+entry.getValue()),
                    (result,index)->new Copy(result.getLong(1),result.getString(2),result.getString(3)),entry.getKey());
            if(copies.size()!=entry.getValue())throw error(HttpStatus.CONFLICT,"Sách "+config.name()+" không còn đủ bản cho thuê");
            for(Copy copy:copies)allocations.add(new Allocation(config,copy));
            rentalTotal=rentalTotal.add(config.dailyRate().multiply(BigDecimal.valueOf(config.days())).multiply(BigDecimal.valueOf(entry.getValue())));
            depositTotal=depositTotal.add(config.deposit().multiply(BigDecimal.valueOf(entry.getValue())));
        }
        String code=code("PT",30);BigDecimal finalRental=rentalTotal,finalDeposit=depositTotal;String finalRecipient=recipient,finalPhone=phone,finalAddress=address;
        long rentalId=insert("""
                INSERT INTO phieu_thue(ma_phieu_thue,khach_hang_id,kenh_tao,trang_thai,client_request_id,
                    tong_tien_thue,tong_tien_coc,phuong_thuc_nhan,ten_nguoi_nhan,so_dien_thoai_nhan,dia_chi_giao,ghi_chu)
                VALUES(?,?,?,'CHO_XAC_NHAN',?,?,?,?,?,?,?,?)
                """,statement->{statement.setString(1,code);statement.setLong(2,customerId);statement.setString(3,channel);
            statement.setString(4,request.clientRequestId().toLowerCase(Locale.ROOT));statement.setBigDecimal(5,finalRental);statement.setBigDecimal(6,finalDeposit);
            statement.setString(7,method);statement.setString(8,finalRecipient);statement.setString(9,finalPhone);statement.setString(10,finalAddress);statement.setString(11,normalize(request.ghiChu()));});
        for(Allocation allocation:allocations){
            if(jdbc.update("UPDATE ban_sao_sach SET trang_thai='DA_GIU' WHERE id=? AND trang_thai='SAN_SANG'",allocation.copy().id())!=1)
                throw error(HttpStatus.CONFLICT,"Bản sách vừa được người khác giữ, vui lòng thử lại");
            Config config=allocation.config();jdbc.update("""
                    INSERT INTO chi_tiet_phieu_thue(phieu_thue_id,ban_sao_id,gia_thue_ngay,tien_coc,phi_tre_ngay,
                        ngay_hen_tra,tinh_trang_truoc,so_ngay_thue,so_lan_gia_han,so_lan_gia_han_toi_da)
                    VALUES(?,?,?,?,?,?,?,?,0,?)
                    """,rentalId,allocation.copy().id(),config.dailyRate(),config.deposit(),config.lateRate(),
                    LocalDate.now().plusDays(config.days()),allocation.copy().condition(),config.days(),config.maxRenewals());
        }
        BigDecimal amount=rentalTotal.add(depositTotal);if(amount.signum()>0)createPayment(customerId,rentalId,"PHIEU_THUE","THUE_SACH",amount,request.phuongThucThanhToan());
        log(rentalId,null,"CHO_XAC_NHAN",actor.accountId(),"Tạo yêu cầu từ kênh "+channel);
        return getInternal(rentalId);
    }

    @Transactional(timeout=10)
    public RentalResponse approve(Authentication authentication,long id){
        var actor=actors.require(authentication);actor.requireStaff();RentalState rental=locked(id);
        if(!rental.state().equals("CHO_XAC_NHAN"))throw error(HttpStatus.CONFLICT,"Phiếu không còn chờ xác nhận");
        PaymentResponse payment=payments.getLatestForReference("PHIEU_THUE",id);
        String next=payment==null||payment.trangThai().equals("DA_THANH_TOAN")?"CHO_NHAN_SACH":"CHO_THANH_TOAN";
        jdbc.update("UPDATE phieu_thue SET trang_thai=?,nhan_vien_duyet_id=?,ngay_duyet=CURRENT_TIMESTAMP WHERE id=?",next,actor.accountId(),id);
        log(id,"CHO_XAC_NHAN",next,actor.accountId(),null);return getInternal(id);
    }

    @Transactional(timeout=10)
    public RentalResponse handover(Authentication authentication,long id){
        var actor=actors.require(authentication);actor.requireStaff();RentalState rental=locked(id);
        if(!rental.state().equals("CHO_NHAN_SACH"))throw error(HttpStatus.CONFLICT,"Phiếu chưa sẵn sàng giao sách");
        for(long copyId:copyIds(id))if(jdbc.update("UPDATE ban_sao_sach SET trang_thai='DANG_THUE' WHERE id=? AND trang_thai='DA_GIU'",copyId)!=1)
            throw error(HttpStatus.CONFLICT,"Trạng thái bản sách không còn hợp lệ");
        var details=jdbc.query("SELECT id,so_ngay_thue FROM chi_tiet_phieu_thue WHERE phieu_thue_id=?",(result,index)->new long[]{result.getLong(1),result.getInt(2)},id);
        for(long[] detail:details)jdbc.update("UPDATE chi_tiet_phieu_thue SET ngay_hen_tra=? WHERE id=?",LocalDate.now().plusDays(detail[1]),detail[0]);
        jdbc.update("UPDATE phieu_thue SET trang_thai='DANG_THUE',nhan_vien_giao_id=?,ngay_giao=CURRENT_TIMESTAMP WHERE id=?",actor.accountId(),id);
        log(id,"CHO_NHAN_SACH","DANG_THUE",actor.accountId(),null);return getInternal(id);
    }

    @Transactional(timeout=10)
    public RentalResponse cancel(Authentication authentication,long id){
        var actor=actors.require(authentication);RentalState rental=locked(id);requireOwner(actor,rental.customerId());
        if(!Set.of("CHO_XAC_NHAN","CHO_THANH_TOAN","CHO_NHAN_SACH").contains(rental.state()))throw error(HttpStatus.CONFLICT,"Phiếu đã giao hoặc đã kết thúc nên không thể hủy");
        if(!actor.staff()&&rental.state().equals("CHO_NHAN_SACH"))throw error(HttpStatus.CONFLICT,"Phiếu đã sẵn sàng giao; vui lòng liên hệ cửa hàng để hủy");
        if(jdbc.queryForObject("SELECT COUNT(*) FROM thanh_toan WHERE tham_chieu_id=? AND tham_chieu_loai='PHIEU_THUE' AND trang_thai='DA_THANH_TOAN'",Integer.class,id)>0)
            throw error(HttpStatus.CONFLICT,"Khoản thuê đã thanh toán; cần xử lý hoàn tiền trước khi hủy");
        for(long copyId:copyIds(id))jdbc.update("UPDATE ban_sao_sach SET trang_thai='SAN_SANG' WHERE id=? AND trang_thai='DA_GIU'",copyId);
        jdbc.update("UPDATE thanh_toan SET trang_thai='THAT_BAI',ghi_chu='Phiếu thuê đã hủy' WHERE tham_chieu_id=? AND tham_chieu_loai='PHIEU_THUE' AND trang_thai IN ('CHO_THANH_TOAN','CHO_DOI_SOAT')",id);
        jdbc.update("UPDATE phieu_thue SET trang_thai='DA_HUY' WHERE id=?",id);log(id,rental.state(),"DA_HUY",actor.accountId(),null);return getInternal(id);
    }

    @Transactional(timeout=10)
    public RentalResponse renew(Authentication authentication,long id,RentalRenewRequest request){
        var actor=actors.require(authentication);RentalState rental=locked(id);requireOwner(actor,rental.customerId());
        if(!rental.state().equals("DANG_THUE"))throw error(HttpStatus.CONFLICT,"Chỉ phiếu đang thuê mới được gia hạn");
        if(jdbc.queryForObject("SELECT COUNT(*) FROM thanh_toan WHERE tham_chieu_id=? AND tham_chieu_loai='GIA_HAN' AND trang_thai IN ('CHO_THANH_TOAN','CHO_DOI_SOAT')",Integer.class,id)>0)
            throw error(HttpStatus.CONFLICT,"Phiếu đang có phí gia hạn chờ đối soát");
        var details=jdbc.query(lock("SELECT id,gia_thue_ngay,ngay_hen_tra,so_lan_gia_han,so_lan_gia_han_toi_da FROM chi_tiet_phieu_thue WHERE phieu_thue_id=?"),
                (result,index)->new Renewal(result.getLong(1),result.getBigDecimal(2),LocalDate.parse(result.getString(3)),result.getInt(4),result.getInt(5)),id);
        BigDecimal fee=BigDecimal.ZERO;for(Renewal detail:details){
            if(detail.used()>=detail.maximum())throw error(HttpStatus.CONFLICT,"Một bản sách đã hết lượt gia hạn");
            fee=fee.add(detail.rate().multiply(BigDecimal.valueOf(request.soNgay())));
        }
        if(fee.signum()>0)createPayment(rental.customerId(),id,"GIA_HAN","THUE_SACH",fee,request.phuongThucThanhToan(),"GIA_HAN:"+request.soNgay());
        else {
            for(Renewal detail:details)jdbc.update("UPDATE chi_tiet_phieu_thue SET ngay_hen_tra=?,so_lan_gia_han=so_lan_gia_han+1 WHERE id=?",detail.due().plusDays(request.soNgay()),detail.id());
            log(id,"DANG_THUE","DANG_THUE",actor.accountId(),"Gia hạn miễn phí "+request.soNgay()+" ngày");
        }
        return getInternal(id);
    }

    @Transactional(timeout=10)
    public RentalResponse returnBooks(Authentication authentication,long id,RentalReturnRequest request){
        var actor=actors.require(authentication);actor.requireStaff();RentalState rental=locked(id);
        if(!rental.state().equals("DANG_THUE"))throw error(HttpStatus.CONFLICT,"Phiếu không ở trạng thái đang thuê");
        Set<Long> expected=new HashSet<>(copyIds(id)),actual=new HashSet<>();for(var item:request.items())if(!actual.add(item.banSaoId()))throw error(HttpStatus.BAD_REQUEST,"Bản sách trả bị lặp");
        if(!expected.equals(actual))throw error(HttpStatus.BAD_REQUEST,"Phải kiểm nhận đầy đủ mọi bản sách trong phiếu");
        Map<Long,RentalReturnRequest.Item> input=new HashMap<>();request.items().forEach(item->input.put(item.banSaoId(),item));
        var details=jdbc.query(lock("SELECT id,ban_sao_id,ngay_hen_tra,phi_tre_ngay FROM chi_tiet_phieu_thue WHERE phieu_thue_id=?"),
                (result,index)->new ReturnLine(result.getLong(1),result.getLong(2),LocalDate.parse(result.getString(3)),result.getBigDecimal(4)),id);
        BigDecimal total=BigDecimal.ZERO;LocalDate today=LocalDate.now();
        for(ReturnLine detail:details){var item=input.get(detail.copyId());int late=(int)Math.max(0,ChronoUnit.DAYS.between(detail.due(),today));
            BigDecimal lateFee=detail.lateRate().multiply(BigDecimal.valueOf(late));total=total.add(lateFee).add(item.phiHuHong());
            jdbc.update("UPDATE chi_tiet_phieu_thue SET ngay_tra_thuc_te=?,tinh_trang_sau=?,so_ngay_tre=?,phi_tre=?,phi_hu_hong=? WHERE id=?",
                    today,item.tinhTrangSau(),late,lateFee,item.phiHuHong(),detail.id());
            String copyState=item.tinhTrangSau().equals("HU_HONG")?"BAO_TRI":"SAN_SANG";
            jdbc.update("UPDATE ban_sao_sach SET tinh_trang=?,trang_thai=? WHERE id=? AND trang_thai='DANG_THUE'",item.tinhTrangSau(),copyState,detail.copyId());
        }
        jdbc.update("UPDATE thanh_toan SET trang_thai='THAT_BAI',ghi_chu='Đã trả sách trước khi hoàn tất gia hạn' WHERE tham_chieu_id=? AND tham_chieu_loai='GIA_HAN' AND trang_thai IN ('CHO_THANH_TOAN','CHO_DOI_SOAT')",id);
        jdbc.update("UPDATE phieu_thue SET trang_thai='HOAN_THANH',tong_phi_phat=?,nhan_vien_nhan_id=?,ngay_hoan_tat=CURRENT_TIMESTAMP WHERE id=?",total,actor.accountId(),id);
        if(total.signum()>0)createPayment(rental.customerId(),id,"TRA_PHIEU_THUE","PHI_PHAT",total,"TIEN_MAT");
        log(id,"DANG_THUE","HOAN_THANH",actor.accountId(),"Đã kiểm nhận "+details.size()+" bản sách");return getInternal(id);
    }

    public List<RentalResponse> overdue(Authentication authentication){
        actors.require(authentication).requireStaff();return jdbc.query("SELECT DISTINCT p.id FROM phieu_thue p JOIN chi_tiet_phieu_thue c ON c.phieu_thue_id=p.id WHERE p.trang_thai='DANG_THUE' AND c.ngay_hen_tra<CURRENT_DATE ORDER BY p.id",
                (result,index)->result.getLong(1)).stream().map(id -> getInternal(Objects.requireNonNull(id))).toList();
    }

    private Customer activeMember(long customerId){
        var rows=jdbc.query("""
                SELECT k.ho_ten,k.so_dien_thoai,k.dia_chi,h.ngay_het_han,h.trang_thai,t.trang_thai AS the_trang_thai
                FROM khach_hang k LEFT JOIN hoi_vien h ON h.khach_hang_id=k.id
                LEFT JOIN the_thanh_vien t ON t.hoi_vien_id=h.id WHERE k.id=?
                """,(result,index)->new Customer(result.getString(1),normalize(result.getString(2)),normalize(result.getString(3)),
                result.getString(4),result.getString(5),result.getString(6)),customerId);
        if(rows.isEmpty())throw error(HttpStatus.NOT_FOUND,"Không tìm thấy khách hàng");Customer customer=rows.getFirst();
        if(!"DANG_HOAT_DONG".equals(customer.memberState())||customer.expiry()==null||LocalDate.parse(customer.expiry()).isBefore(LocalDate.now())||!"DA_KICH_HOAT".equals(customer.cardState()))
            throw error(HttpStatus.CONFLICT,"Cần thẻ hội viên đang hoạt động để thuê sách");
        return customer;
    }

    private RentalResponse getInternal(long id){
        var rows=jdbc.query("SELECT p.*,k.ho_ten FROM phieu_thue p JOIN khach_hang k ON k.id=p.khach_hang_id WHERE p.id=?",(result,index)->{
            Timestamp created=result.getTimestamp("ngay_tao");return new RentalResponse(result.getLong("id"),result.getString("ma_phieu_thue"),result.getLong("khach_hang_id"),result.getString("ho_ten"),
                    result.getString("kenh_tao"),result.getString("trang_thai"),result.getString("client_request_id"),created.toLocalDateTime(),money(result.getBigDecimal("tong_tien_thue")),
                    money(result.getBigDecimal("tong_tien_coc")),money(result.getBigDecimal("tong_phi_phat")),result.getString("phuong_thuc_nhan"),result.getString("ten_nguoi_nhan"),
                    result.getString("so_dien_thoai_nhan"),result.getString("dia_chi_giao"),result.getString("ghi_chu"),List.of(),null);
        },id);if(rows.isEmpty())throw error(HttpStatus.NOT_FOUND,"Không tìm thấy phiếu thuê");RentalResponse header=rows.getFirst();
        var items=jdbc.query("""
                SELECT c.*,b.ma_ban_sao,s.id AS sach_id,s.ma_sach,s.ten_sach FROM chi_tiet_phieu_thue c
                JOIN ban_sao_sach b ON b.id=c.ban_sao_id JOIN sach s ON s.id=b.sach_id WHERE c.phieu_thue_id=? ORDER BY c.id
                """,(result,index)->new RentalResponse.Item(result.getLong("id"),result.getLong("ban_sao_id"),result.getString("ma_ban_sao"),result.getLong("sach_id"),result.getString("ma_sach"),result.getString("ten_sach"),
                money(result.getBigDecimal("gia_thue_ngay")),money(result.getBigDecimal("tien_coc")),money(result.getBigDecimal("phi_tre_ngay")),result.getInt("so_ngay_thue"),result.getInt("so_lan_gia_han"),
                result.getInt("so_lan_gia_han_toi_da"),LocalDate.parse(result.getString("ngay_hen_tra")),parseDate(result.getString("ngay_tra_thuc_te")),result.getString("tinh_trang_truoc"),result.getString("tinh_trang_sau"),
                result.getInt("so_ngay_tre"),money(result.getBigDecimal("phi_tre")),money(result.getBigDecimal("phi_hu_hong"))),id);
        return new RentalResponse(header.id(),header.maPhieuThue(),header.khachHangId(),header.hoTenKhachHang(),header.kenhTao(),header.trangThai(),header.clientRequestId(),header.ngayTao(),
                header.tongTienThue(),header.tongTienCoc(),header.tongPhiPhat(),header.phuongThucNhan(),header.tenNguoiNhan(),header.soDienThoaiNhan(),header.diaChiGiao(),header.ghiChu(),items,payments.getLatestForRental(id));
    }

    private void verifyReplay(RentalResponse previous,RentalRequest request,SortedMap<Long,Integer> quantities){
        Map<Long,Integer> old=new TreeMap<>();previous.items().forEach(item->old.merge(item.sachId(),1,(left,right) -> Objects.requireNonNull(left)+Objects.requireNonNull(right)));
        PaymentResponse initialPayment=payments.getLatestForReference("PHIEU_THUE",previous.id());
        String oldPayment=initialPayment==null?request.phuongThucThanhToan():initialPayment.phuongThuc();
        if(!old.equals(quantities)||!previous.phuongThucNhan().equals(request.phuongThucNhan())||!oldPayment.equals(request.phuongThucThanhToan())
                ||!Objects.equals(normalize(previous.ghiChu()),normalize(request.ghiChu())))
            throw error(HttpStatus.CONFLICT,"Mã gửi yêu cầu đã được dùng cho nội dung khác");
    }
    private RentalState locked(long id){var rows=jdbc.query(lock("SELECT khach_hang_id,trang_thai FROM phieu_thue WHERE id=?"),(result,index)->new RentalState(result.getLong(1),result.getString(2)),id);if(rows.isEmpty())throw error(HttpStatus.NOT_FOUND,"Không tìm thấy phiếu thuê");return rows.getFirst();}
    private List<Long> copyIds(long id){return jdbc.query("SELECT ban_sao_id FROM chi_tiet_phieu_thue WHERE phieu_thue_id=? ORDER BY ban_sao_id",(result,index)->result.getLong(1),id);}
    private void requireOwner(CurrentActorService.Actor actor,long customerId){if(!actor.staff()&&!Objects.equals(actor.requireCustomer(),customerId))throw error(HttpStatus.NOT_FOUND,"Không tìm thấy phiếu thuê");}
    private void createPayment(long customerId,long rentalId,String referenceType,String purpose,BigDecimal amount,String method){createPayment(customerId,rentalId,referenceType,purpose,amount,method,null);}
    private void createPayment(long customerId,long rentalId,String referenceType,String purpose,BigDecimal amount,String method,String note){String paymentCode=code("TT",30);jdbc.update("""
            INSERT INTO thanh_toan(ma_thanh_toan,khach_hang_id,muc_dich,tham_chieu_loai,tham_chieu_id,phuong_thuc,so_tien,noi_dung_chuyen_khoan,trang_thai)
            VALUES(?,?,?,?,?,?,?,?,'CHO_DOI_SOAT')
            """,paymentCode,customerId,purpose,referenceType,rentalId,method,amount,"QLNS "+paymentCode);
        if(note!=null)jdbc.update("UPDATE thanh_toan SET ghi_chu=? WHERE ma_thanh_toan=?",note,paymentCode);}
    private void log(long rentalId,String oldState,String newState,Long accountId,String note){jdbc.update("INSERT INTO nhat_ky_thue(phieu_thue_id,trang_thai_cu,trang_thai_moi,nguoi_thuc_hien_id,ghi_chu) VALUES(?,?,?,?,?)",rentalId,oldState,newState,accountId,note);}
    private long insert(String sql,SqlBinder binder){var key=new GeneratedKeyHolder();jdbc.update(connection->{PreparedStatement statement=connection.prepareStatement(sql,new String[]{"id"});binder.bind(statement);return statement;},key);return Objects.requireNonNull(key.getKey()).longValue();}
    private interface SqlBinder{void bind(PreparedStatement statement)throws SQLException;}
    private record Customer(String name,String phone,String address,String expiry,String memberState,String cardState){}
    private record Config(long bookId,String code,String name,BigDecimal dailyRate,BigDecimal deposit,int days,int maxRenewals,BigDecimal lateRate){}
    private record Copy(long id,String code,String condition){}
    private record Allocation(Config config,Copy copy){}
    private record RentalState(long customerId,String state){}
    private record Renewal(long id,BigDecimal rate,LocalDate due,int used,int maximum){}
    private record ReturnLine(long id,long copyId,LocalDate due,BigDecimal lateRate){}
    private String lock(String sql){return sqlite?sql:sql+" FOR UPDATE";}
    private static LocalDate parseDate(String value){return value==null?null:LocalDate.parse(value);}
    private static BigDecimal money(BigDecimal value){return value==null?null:value.setScale(2,RoundingMode.UNNECESSARY);}
    private static String normalize(String value){return value==null||value.isBlank()?null:value.strip();}
    private static String code(String prefix,int length){return prefix+UUID.randomUUID().toString().replace("-","").substring(0,length).toUpperCase(Locale.ROOT);}
    private static ResponseStatusException error(HttpStatus status,String message){return new ResponseStatusException(status,message);}
}
