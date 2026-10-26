# Đăng nhập thật và phân quyền

Cập nhật ngày 23/10/2026 theo yêu cầu mở rộng: thay đăng nhập minh họa bằng Spring Security, phiên đăng nhập trên server, mật khẩu BCrypt và CSRF. Tài khoản được đọc từ tai_khoan JOIN vai_tro. Đăng ký luôn tạo vai trò KHACH_HANG; client không tự chọn quyền admin.

## Chạy bản mới

```powershell
cd D:\QLNS_Nhom14
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=demo" "-Dspring-boot.run.arguments=--server.port=8094"
```

Phiên kiểm tra hiện dùng cổng 8094. Khi dùng cổng 8080 cần dừng phiên cũ trước rồi thay 8094 thành 8080.

Demo tự tạo hai tài khoản nếu chưa tồn tại:

| Loại | Tài khoản | Mật khẩu |
|---|---|---|
| Admin | admin | Admin@123456 |
| Bạn đọc | bandoc | BanDoc@123456 |

Tài khoản mẫu chỉ khởi tạo ở profile demo. MySQL sử dụng dữ liệu tài khoản trong bảng tai_khoan với mật khẩu BCrypt; không tự tạo admin mẫu trong MySQL. Đăng ký bạn đọc dùng /dang-ky.html.

- /dang-nhap.html: bạn đọc đăng nhập rồi chuyển tới tủ sách.
- /admin-login.html: đăng nhập admin, tài khoản bạn đọc không thể dùng luồng này để vào quản trị.
- /quan-tri.html: admin quản lý sách, danh mục, NXB, khách hàng; bố cục sidebar tối tham khảo resources/views/admin/partial/sidebar.blade.php của dự án Laravel người dùng chỉ định.
- / và /sach.html: danh sách xem công khai, không có nút CRUD. Đọc chi tiết yêu cầu đăng nhập.
- /chi-tiet.html?id=1: bìa, tên, tác giả, thể loại, NXB, giá, tồn kho, giới thiệu và đọc thử. Sách chưa có URL ảnh dùng bìa SVG mặc định. Nội dung đọc thử hiện là đoạn minh họa biên soạn cho bài tập, chưa phải trích đoạn riêng từ sách thực tế.

Backend chặn API ghi sách/danh mục/NXB và toàn bộ API khách hàng nếu không có quyền ADMIN. Ghi dữ liệu và đăng xuất cần CSRF token; /api/auth/csrf cấp token cho client. /api/auth/me trả người dùng hiện tại, /api/auth/login nhận form, /api/auth/register nhận JSON.

Kiểm chứng: mvnw.cmd test đạt 18 test hiện có sau khi cập nhật test CRUD đăng nhập admin. scripts/security-smoke.ps1 kiểm tra HTTP thực tế tại 8094: sai mật khẩu, phiên bạn đọc, quyền admin, chặn khách hàng và ghi sách trái phép, đăng xuất và CSRF. Mã JavaScript đã kiểm tra cú pháp bằng node --check. Đã kiểm tra trên trình duyệt luồng bạn đọc đăng nhập và xem chi tiết sách. MySQL chưa được kiểm chứng trực tiếp.

Các tài liệu và báo cáo tuần 3 trước ngày cập nhật ghi đăng nhập minh họa là trạng thái lịch sử, không còn phản ánh bản mở rộng này. Đơn hàng và thanh toán chưa triển khai.
