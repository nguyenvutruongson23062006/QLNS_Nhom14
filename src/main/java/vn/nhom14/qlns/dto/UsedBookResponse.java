package vn.nhom14.qlns.dto;

import java.math.BigDecimal;

public record UsedBookResponse(long id,long banSaoId,String maBanSao,long sachId,String maSach,
        String tenSach,String tacGia,String hinhAnh,BigDecimal giaGoc,BigDecimal giaDeXuat,
        BigDecimal giaBan,String tinhTrang,String moTa,String trangThai) {}
