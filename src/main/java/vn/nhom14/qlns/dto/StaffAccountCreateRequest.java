package vn.nhom14.qlns.dto;
import jakarta.validation.constraints.*;
public record StaffAccountCreateRequest(
        @NotBlank @Pattern(regexp="[a-zA-Z0-9_]{3,50}") String username,
        @NotBlank @Size(min=8,max=64) String password,
        @NotBlank @Email @Size(max=150) String email) {}
