package vn.nhom14.qlns.dto;

import java.time.LocalDateTime;

public record KhachHangResponse(
        Long id,
        String hoTen,
        String soDienThoai,
        String email,
        String diaChi,
        LocalDateTime ngayTao,
        Long taiKhoanId
) {}
