package vn.nhom14.qlns.dto;
import jakarta.validation.constraints.*;
public record StaffPasswordRequest(@NotBlank @Size(min=8,max=64) String password) {}
