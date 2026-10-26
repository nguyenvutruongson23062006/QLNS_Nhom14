package vn.nhom14.qlns.dto;


public record NhaXuatBanResponse(
        Long id,
        String tenNhaXuatBan,
        String diaChi,
        String soDienThoai,
        String email,
        Boolean trangThai
) {}
