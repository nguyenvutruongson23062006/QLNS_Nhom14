package vn.nhom14.qlns.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CardStatusRequest(@NotBlank String trangThai, @Size(max=100) String maVanDon) {
}
