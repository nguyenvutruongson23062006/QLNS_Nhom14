# Bàn giao tuần 3 cho cả 3 thành viên

Phạm vi theo kế hoạch 5 tuần: giao diện website và kết nối API. Mã nguồn được bổ sung trực tiếp vào dự án hiện có; tiếp tục sử dụng các API tuần 2.

| Thành viên | Phần việc | File chính |
|---|---|---|
| 1 | Trang chủ, giao diện sách, form thêm/sửa/xóa, phân trang | src/main/resources/static/index.html, sach.html |
| 2 | Trang quản trị với các module sách, danh mục, nhà xuất bản, khách hàng | src/main/resources/static/quan-tri.html |
| 3 | Giao diện đăng nhập minh họa, quản lý khách hàng và validation | src/main/resources/static/dang-nhap.html, khach-hang.html |

Tài nguyên dùng chung: `src/main/resources/static/css/style.css` và `src/main/resources/static/js/app.js`. JavaScript dùng cấu hình trường cho từng module để các trang cùng xử lý lỗi API, trạng thái rỗng, phân trang và biểu mẫu nhất quán. CSS thuần, không cần Node/npm hoặc CDN để chạy ứng dụng.

## Chạy và trình bày

```powershell
cd D:\QLNS_Nhom14
$env:SPRING_PROFILES_ACTIVE = 'demo'
$env:PORT = '8093'
.\mvnw.cmd spring-boot:run
```

Mở http://localhost:8093/ bằng trình duyệt, không mở HTML bằng file://.

1. Thành viên 1: trình bày trang chủ (số liệu và sách lấy từ API); mở `/sach.html`, thêm sách, chọn danh mục/NXB, sửa giá và tồn kho. Để ISBN trống nếu chưa có. Thử mã sách trùng để minh họa lỗi API.
2. Thành viên 2: mở `/quan-tri.html`, chuyển giữa các module; thêm danh mục và nhà xuất bản rồi kiểm tra chúng xuất hiện trong form sách. Thử xóa danh mục đang được sách sử dụng để xem lỗi khóa ngoại.
3. Thành viên 3: mở `/khach-hang.html`, thêm/sửa/xóa khách hàng; thử email sai và trường họ tên trống. Mở `/dang-nhap.html`, nhập dữ liệu minh họa và gửi biểu mẫu để xem thông báo.

Trang đăng nhập chỉ minh họa biểu mẫu và chọn vai trò. Chưa có API xác thực, phiên đăng nhập hay bảo vệ trang quản trị; không lưu/gửi mật khẩu, không dùng mật khẩu thật. Xác thực và phân quyền thuộc tuần 4 theo kế hoạch. Không có tài khoản đăng nhập mẫu.

## Dữ liệu và kiểm chứng

Profile demo dùng H2 trong RAM, khởi tạo lại khi tắt/chạy ứng dụng. Để lưu dữ liệu lâu dài, cấu hình profile MySQL theo README hiện có. Không cần sửa schema cho phần giao diện tuần 3.

Kiểm chứng ngày 22/10/2026:

- `node --check src/main/resources/static/js/app.js`: thành công.
- `mvnw.cmd test`: BUILD SUCCESS, 18 tests, 0 failures, 0 errors, 0 skipped; kiểm chứng backend trên H2.
- Đã mở trình duyệt thực tế: trang chủ hiển thị số liệu từ API; trang sách hiển thị 2 sách mẫu; form sửa sách tải đúng danh mục/NXB và lưu thành công; trang khách hàng thêm thành công bản ghi minh họa; trang quản trị tải danh mục.
- Chưa kiểm chứng trực tiếp toàn bộ tổ hợp thao tác UI, phân trang nhiều trang, giao diện điện thoại và MySQL. Dùng các bước demo trên để nghiệm thu tiếp.
- `/api/system/status` giữ trường `week: 2` là mốc API hiện có; giao diện tuần 3 được phục vụ bằng static resources.
