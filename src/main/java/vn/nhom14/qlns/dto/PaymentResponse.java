package vn.nhom14.qlns.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentResponse(long id, String maThanhToan, long khachHangId, String mucDich,
        String thamChieuLoai, long thamChieuId, String phuongThuc, BigDecimal soTien,
        String noiDungChuyenKhoan, String trangThai, String maGiaoDichNhaCungCap,
        LocalDateTime ngayTao, LocalDateTime ngayXacNhan, String qrImageUrl,
        boolean qrConfigured, String qrNote) {
}
