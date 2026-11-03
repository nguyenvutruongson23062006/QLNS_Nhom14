package vn.nhom14.qlns.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record MembershipResponse(long id, long khachHangId, String hoTenKhachHang,
        String maHoiVien, LocalDate ngayBatDau, LocalDate ngayHetHan, String trangThai,
        BigDecimal phiThanhVien, Card card, Delivery delivery, PaymentResponse payment) {
    public record Card(long id, String maThe, String phuongThucNhan, String trangThai) {}
    public record Delivery(String tenNguoiNhan, String soDienThoai, String diaChi,
            BigDecimal phiGiao, String maVanDon, String trangThai) {}
}
