# Thiết kế cơ sở dữ liệu

Database: `qlns_nhom14`; MySQL từ 8.0.16 để thực thi CHECK.
Charset `utf8mb4`, collation `utf8mb4_unicode_ci`.
Nguồn SQL chính nằm tại `src/main/resources/db/schema.sql` và `data.sql`;
demo H2 cũng dùng chính hai file này để tránh lệch thiết kế.

## Danh sách bảng và dữ liệu

Tất cả khóa chính `id` là BIGINT AUTO_INCREMENT.
Các trường NOT NULL, UNIQUE, DEFAULT và CHECK được khai báo đầy đủ trong SQL.

| Bảng | Trường chính | Ý nghĩa |
|---|---|---|
| vai_tro | ma_vai_tro, ten_vai_tro | Ba vai trò ADMIN, NHAN_VIEN, KHACH_HANG |
| tai_khoan | ten_dang_nhap, mat_khau_hash, email, vai_tro_id, trang_thai, ngay_tao | Tài khoản; username/email duy nhất; mật khẩu đã băm |
| khach_hang | tai_khoan_id, ho_ten, so_dien_thoai, email, dia_chi, ngay_tao | Hồ sơ; liên kết tài khoản tùy chọn, duy nhất |
| danh_muc | ten_danh_muc, mo_ta, trang_thai | Phân loại sách, tên duy nhất |
| nha_xuat_ban | ten_nha_xuat_ban, dia_chi, so_dien_thoai, email, trang_thai | Nhà xuất bản, tên duy nhất |
| sach | ma_sach, ten_sach, tac_gia, isbn, danh_muc_id, nha_xuat_ban_id, nam_xuat_ban, gia_ban, so_luong_ton, mo_ta, hinh_anh, trang_thai, ngay_tao | Sách; mã duy nhất; ISBN tùy chọn và duy nhất |
| don_hang | ma_don_hang, khach_hang_id, nhan_vien_id, ngay_dat, trang_thai, ten_nguoi_nhan, so_dien_thoai_nhan, dia_chi_giao, tong_tien, ghi_chu | Đơn; lưu thông tin giao hàng tại thời điểm đặt |
| chi_tiet_don_hang | don_hang_id, sach_id, so_luong, don_gia | Dòng đơn; giữ giá lịch sử; cặp đơn/sách duy nhất |

`VARCHAR` lưu điện thoại và ISBN để giữ số 0 đầu và ký tự phân cách.
Tiền là `DECIMAL(15,2)`, số lượng là `INT`, trạng thái hoạt động là `BOOLEAN`.
Thời gian tạo dùng `TIMESTAMP DEFAULT CURRENT_TIMESTAMP`.

## Quan hệ

- vai_tro 1–N tai_khoan.
- tai_khoan 1–0..1 khach_hang; hồ sơ khách có thể chưa có tài khoản.
- danh_muc 1–N sach; nha_xuat_ban 1–N sach.
- khach_hang 1–N don_hang.
- tai_khoan 1–N don_hang qua nhan_vien_id, có thể NULL nếu chưa phân công.
- don_hang 1–N chi_tiet_don_hang; sach 1–N chi_tiet_don_hang.

Khóa ngoại không cascade delete, giúp bảo vệ lịch sử.
Không lưu thành tiền dòng vì có thể tính `so_luong * don_gia`.
`tong_tien` lưu trên đơn để tra cứu/thống kê, service chịu trách nhiệm đồng bộ với chi tiết.
Chỉ mục bổ sung cho tên sách, ngày đặt và trạng thái đơn; MySQL tự tạo chỉ mục cho khóa ngoại.
Tìm chứa từ với LIKE `%từ khóa%` có thể không tận dụng chỉ mục tên; cân nhắc FULLTEXT khi dữ liệu tăng.

## Các ràng buộc cần triển khai ở service trong tuần sau

CHECK/khóa ngoại chưa đảm bảo đơn có ít nhất một dòng, tổng đơn khớp chi tiết,
nhân viên được gán đúng vai trò, chuyển trạng thái hợp lệ hoặc khách chỉ đọc đơn của mình.
Các quy tắc này phải được kiểm tra trong Java với transaction.
Tên đăng nhập/email cần chuẩn hóa nhất quán trước khi lưu để phù hợp collation MySQL.

## Dữ liệu khởi tạo

3 vai trò, 2 danh mục, 2 NXB, 2 sách minh họa. Không tạo tài khoản đăng nhập
vì chức năng xác thực và bộ mã hóa mật khẩu thuộc tuần 3–4.
Chỉ chạy schema và dữ liệu mẫu một lần trên database mới; SQL không xóa dữ liệu cũ.
H2 demo không lưu dữ liệu sau khi tắt và không thay thế kiểm thử MySQL thật.
