package vn.nhom14.qlns.dto;

import jakarta.validation.constraints.*;

public record NhaXuatBanRequest(
        @NotBlank @Size(max = 150) String tenNhaXuatBan,
        @Size(max = 500) String diaChi,
        @Pattern(regexp = "^$|[0-9+() .-]{8,20}$") String soDienThoai,
        @Email @Size(max = 150) String email,
        @NotNull Boolean trangThai
) {}
