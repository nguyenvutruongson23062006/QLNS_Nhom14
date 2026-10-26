package vn.nhom14.qlns.dto;

import jakarta.validation.constraints.*;

public record DanhMucRequest(
        @NotBlank @Size(max = 100) String tenDanhMuc,
        @Size(max = 1000) String moTa,
        @NotNull Boolean trangThai
) {}
