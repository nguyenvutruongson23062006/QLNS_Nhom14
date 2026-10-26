package vn.nhom14.qlns.dto;

import jakarta.validation.constraints.*;

public record KhachHangRequest(
        @NotBlank @Size(max = 150) String hoTen,
        @Pattern(regexp = "^$|[0-9+() .-]{8,20}$") String soDienThoai,
        @Email @Size(max = 150) String email,
        @Size(max = 500) String diaChi
) {}
