package vn.nhom14.qlns.dto;

import jakarta.validation.constraints.*;

public record UsedBookCreateRequest(@Positive long banSaoId,
        @Size(max=2000) String moTa,@Size(max=500) String hinhAnh) {}
