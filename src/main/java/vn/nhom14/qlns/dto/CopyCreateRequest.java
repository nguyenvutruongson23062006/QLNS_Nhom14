package vn.nhom14.qlns.dto;

import jakarta.validation.constraints.*;

public record CopyCreateRequest(@Min(1) @Max(100) int soLuong,
        @NotNull @Pattern(regexp="MOI|NHU_MOI|TOT|KHA|CU") String tinhTrang,
        @Size(max=1000) String ghiChu) {}
