package vn.nhom14.qlns.dto;
import jakarta.validation.constraints.*;
public record StaffAccountUpdateRequest(@NotBlank @Email @Size(max=150) String email,
                                        @NotNull Boolean enabled) {}
