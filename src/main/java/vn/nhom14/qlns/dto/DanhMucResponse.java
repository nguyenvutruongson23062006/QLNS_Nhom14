package vn.nhom14.qlns.dto;


public record DanhMucResponse(
        Long id,
        String tenDanhMuc,
        String moTa,
        Boolean trangThai
) {}
