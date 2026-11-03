package vn.nhom14.qlns.dto;

import java.math.BigDecimal;

public record RentalCatalogResponse(long sachId,String maSach,String tenSach,String tacGia,String hinhAnh,
        boolean choThue,BigDecimal giaThueNgay,BigDecimal tienCoc,int soNgayMacDinh,
        int soLanGiaHan,BigDecimal phiTreNgay,int soBanSaoSanSang,int tongBanSao) {}
