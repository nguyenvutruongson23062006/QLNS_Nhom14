package vn.nhom14.qlns.dto;

import java.util.List;
import jakarta.validation.constraints.*;

public record UsedBookOrderRequest(
        @NotBlank @Size(max=150) String tenNguoiNhan,
        @NotBlank @Size(max=20) @Pattern(regexp="\\+?[0-9][0-9 .-]{6,18}[0-9]") String soDienThoaiNhan,
        @NotBlank @Size(max=500) String diaChiGiao,
        @Size(max=1000) String ghiChu,
        @NotBlank @Pattern(regexp="[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}") String clientRequestId,
        @NotEmpty @Size(max=10) List<@NotNull @Positive Long> sachCuIds) {}
