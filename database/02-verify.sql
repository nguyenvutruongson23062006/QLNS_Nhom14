USE qlns_nhom14;
SHOW TABLES;
SELECT COUNT(*) AS so_vai_tro FROM vai_tro;
SELECT COUNT(*) AS so_sach_mau FROM sach;
SELECT s.ma_sach, s.ten_sach, d.ten_danh_muc, n.ten_nha_xuat_ban, s.gia_ban, s.so_luong_ton
FROM sach s JOIN danh_muc d ON s.danh_muc_id = d.id
JOIN nha_xuat_ban n ON s.nha_xuat_ban_id = n.id;
-- Kết quả trên DB mới: 8 bảng, 3 vai trò, 2 sách có danh mục/NXB hợp lệ.
