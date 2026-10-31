package vn.nhom14.qlns.dto;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
public record OrderResponse(Long id,String maDonHang,Long khachHangId,String hoTenKhachHang,
    String trangThai,String tenNguoiNhan,String soDienThoaiNhan,String diaChiGiao,
    BigDecimal tongTien,String ghiChu,LocalDateTime ngayDat,List<Item> items) {
    public record Item(Long sachId,String maSach,String tenSach,String hinhAnh,
                       int soLuong,BigDecimal donGia,BigDecimal thanhTien) {}
}
