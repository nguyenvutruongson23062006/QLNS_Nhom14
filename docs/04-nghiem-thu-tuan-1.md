# Nghiệm thu tuần 1

Ngày kiểm tra: 09/10/2026.

| Phân công | Sản phẩm đã hoàn thành | Trạng thái |
|---|---|---|
| Thành viên 1 | Phân tích chức năng, vai trò, use case, quy tắc và tiêu chí nghiệm thu | Hoàn thành |
| Thành viên 2 | Thiết kế 8 bảng, ERD, PK/FK/UNIQUE/CHECK, index, SQL và dữ liệu mẫu | Hoàn thành |
| Thành viên 3 | Project Java Spring Boot, Maven Wrapper, cấu hình MySQL/demo, API trạng thái, hướng dẫn chạy | Hoàn thành |

## Kết quả kiểm thử tự động

Lệnh `.\mvnw.cmd test`: BUILD SUCCESS, 5 tests, 0 failures, 0 errors, 0 skipped.

- Context Spring Boot khởi động thành công với profile demo.
- Truy vấn đủ 8 bảng; xác nhận 3 vai trò và 2 sách mẫu.
- CSDL từ chối cập nhật tồn kho âm và danh mục không tồn tại.
- HTTP GET `/api/system/status` trả 200, database CONNECTED, week 1.
- HTTP GET `/` trả 200 và trang tiếng Việt.

Lệnh `.\mvnw.cmd -DskipTests package` đóng gói thành công sau khi kiểm thử.
Đã chạy file `target/qlns-0.0.1-SNAPSHOT.jar` với profile mặc định demo:
API trả UP/CONNECTED và trang chủ trả HTTP 200. Tiến trình kiểm tra đã được tắt.

SQL đã chạy trên H2 chế độ MySQL. Chưa kiểm thử trên MySQL thật vì chưa có
thông tin kết nối máy chủ/tài khoản. Chạy các bước MySQL trong README và
`database/02-verify.sql` để xác nhận môi trường MySQL trước tuần 2.

## Bàn giao cho tuần 2

- Thành viên 1: Entity/Repository/Service/Controller và API quản lý sách.
- Thành viên 2: API danh mục và nhà xuất bản.
- Thành viên 3: API khách hàng.
- Dùng tên bảng và trường trong schema làm cơ sở ánh xạ JPA.
- Các quyền trong tài liệu là thiết kế dự kiến; xác thực/phân quyền triển khai tuần 3–4.

Phần tuần 1 chưa có chức năng CRUD hay đăng nhập; trang hiện tại dùng để xác nhận bộ khung hoạt động.
