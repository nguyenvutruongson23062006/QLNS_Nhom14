package vn.nhom14.qlns.dto;

import jakarta.validation.constraints.*;

public record RentalRenewRequest(@Min(1) @Max(30) int soNgay,
        @NotNull @Pattern(regexp="VIETQR|TIEN_MAT") String phuongThucThanhToan) {}
