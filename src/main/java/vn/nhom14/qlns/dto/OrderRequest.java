package vn.nhom14.qlns.dto;
import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
/** Prices are always read from the database; the browser sends only ids and quantities. */
public record OrderRequest(
    @NotBlank @Size(max=150) String tenNguoiNhan,
    @NotBlank @Size(max=20) @Pattern(regexp="\\+?[0-9][0-9 .-]{6,18}[0-9]") String soDienThoaiNhan,
    @NotBlank @Size(max=500) String diaChiGiao,
    @Size(max=1000) String ghiChu,
    @NotBlank @Pattern(regexp="[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}") String clientRequestId,
    @NotEmpty @Size(max=50) List<@NotNull @Valid Item> items) {
    public record Item(@NotNull @Positive Long sachId,@NotNull @Min(1) @Max(99) Integer soLuong) {}
}
