package vn.nhom14.qlns.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SachResponse(
        Long id,
        String maSach,
        String tenSach,
        String tacGia,
        String isbn,
        Integer namXuatBan,
        BigDecimal giaBan,
        Integer soLuongTon,
        String moTa,
        String hinhAnh,
        Boolean trangThai,
        Long danhMucId,
        Long nhaXuatBanId,
        LocalDateTime ngayTao
) {}
