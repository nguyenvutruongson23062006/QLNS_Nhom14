package vn.nhom14.qlns.dto;

import java.math.BigDecimal;
import jakarta.validation.constraints.*;

public record RentalConfigRequest(@NotNull Boolean choThue,
        @NotNull @DecimalMin("0.00") @Digits(integer=13,fraction=2) BigDecimal giaThueNgay,
        @NotNull @DecimalMin("0.00") @Digits(integer=13,fraction=2) BigDecimal tienCoc,
        @Min(1) @Max(90) int soNgayMacDinh,
        @Min(0) @Max(10) int soLanGiaHan,
        @NotNull @DecimalMin("0.00") @Digits(integer=13,fraction=2) BigDecimal phiTreNgay) {}
