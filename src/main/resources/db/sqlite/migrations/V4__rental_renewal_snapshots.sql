-- Snapshot thời hạn tại lúc thuê để thay đổi cấu hình sau này không sửa hợp đồng cũ.
ALTER TABLE chi_tiet_phieu_thue ADD COLUMN so_ngay_thue INT NOT NULL DEFAULT 14
    CHECK (so_ngay_thue BETWEEN 1 AND 90);
ALTER TABLE chi_tiet_phieu_thue ADD COLUMN so_lan_gia_han INT NOT NULL DEFAULT 0
    CHECK (so_lan_gia_han BETWEEN 0 AND 10);
ALTER TABLE chi_tiet_phieu_thue ADD COLUMN so_lan_gia_han_toi_da INT NOT NULL DEFAULT 1
    CHECK (so_lan_gia_han_toi_da BETWEEN 0 AND 10);
