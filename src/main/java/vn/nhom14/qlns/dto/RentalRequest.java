package vn.nhom14.qlns.dto;

import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

public record RentalRequest(
        @NotBlank @Pattern(regexp="[0-9a-fA-F-]{36}") String clientRequestId,
        @NotEmpty @Size(max=5) List<@Valid Item> items,
        @NotNull @Pattern(regexp="TAI_QUAY|GIAO_HANG") String phuongThucNhan,
        @NotNull @Pattern(regexp="VIETQR|TIEN_MAT") String phuongThucThanhToan,
        @Size(max=150) String tenNguoiNhan,
        @Pattern(regexp="^$|^[0-9+() .-]{8,20}$") String soDienThoaiNhan,
        @Size(max=500) String diaChiGiao,
        @Size(max=1000) String ghiChu) {
    public record Item(@NotNull @Positive Long sachId,@Min(1) @Max(5) int soLuong) {}
}
