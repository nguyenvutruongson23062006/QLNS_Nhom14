package vn.nhom14.qlns.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PaymentReconcileRequest(
        @NotBlank @Size(max=100) String maGiaoDichNhaCungCap,
        @Size(max=1000) String ghiChu) {
}
