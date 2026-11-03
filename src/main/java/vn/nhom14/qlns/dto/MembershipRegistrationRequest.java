package vn.nhom14.qlns.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record MembershipRegistrationRequest(
        @NotNull @Pattern(regexp="TAI_QUAY|GIAO_HANG",message="Cách nhận thẻ không hợp lệ") String phuongThucNhan,
        @NotNull @Pattern(regexp="VIETQR|TIEN_MAT",message="Phương thức thanh toán không hợp lệ") String phuongThucThanhToan,
        @Size(max=150) String tenNguoiNhan,
        @Pattern(regexp="^$|^[0-9+() .-]{8,20}$",message="Số điện thoại không hợp lệ") String soDienThoai,
        @Size(max=500) String diaChi) {
}
