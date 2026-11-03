package vn.nhom14.qlns.dto;

import java.math.BigDecimal;
import java.time.*;
import java.util.List;

public record RentalResponse(long id,String maPhieuThue,long khachHangId,String hoTenKhachHang,
        String kenhTao,String trangThai,String clientRequestId,LocalDateTime ngayTao,
        BigDecimal tongTienThue,BigDecimal tongTienCoc,BigDecimal tongPhiPhat,
        String phuongThucNhan,String tenNguoiNhan,String soDienThoaiNhan,String diaChiGiao,
        String ghiChu,List<Item> items,PaymentResponse payment) {
    public record Item(long chiTietId,long banSaoId,String maBanSao,long sachId,String maSach,String tenSach,
            BigDecimal giaThueNgay,BigDecimal tienCoc,BigDecimal phiTreNgay,int soNgayThue,
            int soLanGiaHan,int soLanGiaHanToiDa,LocalDate ngayHenTra,LocalDate ngayTraThucTe,
            String tinhTrangTruoc,String tinhTrangSau,int soNgayTre,BigDecimal phiTre,BigDecimal phiHuHong) {}
}
