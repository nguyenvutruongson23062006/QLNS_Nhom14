package vn.nhom14.qlns.dto;

import java.math.BigDecimal;
import jakarta.validation.constraints.*;

public record UsedBookApproveRequest(@NotNull @DecimalMin(value="0.01")
        @Digits(integer=13,fraction=2) BigDecimal giaBan) {}
