package vn.nhom14.qlns.dto;

import java.math.BigDecimal;
import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

public record RentalReturnRequest(@NotEmpty List<@Valid Item> items) {
    public record Item(@Positive long banSaoId,
            @NotNull @Pattern(regexp="NHU_MOI|TOT|KHA|CU|HU_HONG") String tinhTrangSau,
            @NotNull @DecimalMin("0.00") @Digits(integer=13,fraction=2) BigDecimal phiHuHong) {}
}
