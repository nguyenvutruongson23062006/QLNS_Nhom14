# Bàn giao đơn hàng, nhân viên và bảo mật

Cập nhật ngày 29/10/2026 cho bản dùng trong báo cáo thật.

## Phạm vi đã triển khai

- Ba vai trò backend: `ADMIN`, `NHAN_VIEN`, `KHACH_HANG`; giao diện chỉ phản ánh quyền,
  quyết định cuối cùng nằm tại Spring Security.
- Admin tạo, khóa/mở khóa và đặt lại mật khẩu nhân viên. Khóa, đổi quyền hoặc đổi mật khẩu
  làm phiên cũ hết hiệu lực ở request kế tiếp.
- Khách lưu giỏ hàng trên trình duyệt và gửi mã UUID chống tạo đơn trùng. Server tự đọc giá,
  kiểm tra sách/danh mục/NXB còn hoạt động và cập nhật tồn trong transaction.
- Khách chỉ thấy đơn của mình và chỉ hủy khi còn `CHO_XAC_NHAN`. Nhân viên/admin chuyển
  `CHO_XAC_NHAN → DA_XAC_NHAN → DANG_GIAO → HOAN_THANH`; hủy từ hai trạng thái đầu.
- Hủy đơn hoàn tồn đúng một lần. Cập nhật tồn dùng điều kiện `so_luong_ton >= số lượng`
  để không bán quá tồn khi có request đồng thời.
- Ảnh được xác thực nội dung, giới hạn 5 MB/16 triệu điểm ảnh, chuyển thành PNG. Ảnh đại diện
  ghi đè theo tài khoản và kho upload có giới hạn tổng 500 MB.

## Khởi tạo an toàn

SQLite là profile mặc định nhưng không chứa mật khẩu admin cố định. Trước lần chạy đầu:

```powershell
$env:QLNS_INITIAL_ADMIN_PASSWORD = Read-Host "Mật khẩu admin ban đầu"
.\mvnw.cmd spring-boot:run
```

Mật khẩu cần 12-64 ký tự và tối đa 72 byte UTF-8. Sau khi admin đầu tiên được tạo, biến này
không còn được sử dụng. Profile `demo` dùng tài khoản mẫu chỉ để kiểm thử dữ liệu H2 tạm thời.

## Điểm kiểm thử bắt buộc

- Biên dịch test phải thành công; không được bỏ qua test bằng `maven.test.skip`.
- Kiểm tra đăng nhập và quyền của cả ba vai trò.
- Kiểm tra gửi lại cùng UUID không tạo đơn hoặc trừ tồn lần hai.
- Kiểm tra hai khách cùng mua cuốn cuối chỉ một request thành công.
- Kiểm tra hủy lặp không hoàn tồn hai lần và khách không xem được đơn của người khác.
- Chạy các luồng checkout/hủy trên SQLite, không chỉ H2.

## Kết quả xác minh bản bàn giao

- `mvnw.cmd test`: 52 test, 0 lỗi, 0 thất bại, 0 bỏ qua trên H2 và SQLite.
- Đã có test hồi quy cho đổi mật khẩu trên SQLite và giới hạn 72 byte UTF-8 của BCrypt;
  dữ liệu không hợp lệ trả HTTP 400 thay vì làm API lỗi 500.
- `node --check src/main/resources/static/js/app.js`: đạt.
- Đóng gói được `target/qlns-0.0.1-SNAPSHOT.jar` và khởi động thật với SQLite.
- Tài khoản mẫu cố định bị từ chối trên SQLite; admin đầu tiên tạo qua biến môi trường đăng nhập
  được với đúng vai trò `ADMIN`.
- Smoke test trình duyệt đã đi qua đăng nhập bạn đọc, giỏ hàng, đặt COD, danh sách đơn, đăng nhập
  admin và màn hình xử lý đơn. Nút thao tác hiển thị theo hành động: `Xác nhận đơn`, `Hủy đơn`.
- Chưa kiểm chứng runtime MySQL vì máy kiểm tra không chạy dịch vụ/cơ sở dữ liệu MySQL của bài.

Báo cáo chi tiết theo từng nhóm kiểm thử: [Báo cáo kiểm thử và nghiệm thu](12-bao-cao-kiem-thu.md).
