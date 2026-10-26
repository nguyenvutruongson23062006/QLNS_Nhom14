package vn.nhom14.qlns.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record SachRequest(
        @NotBlank @Size(max = 30) String maSach,
        @NotBlank @Size(max = 255) String tenSach,
        @NotBlank @Size(max = 150) String tacGia,
        @Size(max = 20) String isbn,
        @Min(1000) @Max(9999) Integer namXuatBan,
        @NotNull @DecimalMin("0.00") @Digits(integer = 13, fraction = 2) BigDecimal giaBan,
        @NotNull @Min(0) Integer soLuongTon,
         String moTa,
        @Size(max = 500) String hinhAnh,
        @NotNull Boolean trangThai,
        @NotNull @Positive Long danhMucId,
        @NotNull @Positive Long nhaXuatBanId
) {}
