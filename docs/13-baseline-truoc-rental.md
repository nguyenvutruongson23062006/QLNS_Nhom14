# Baseline trước khi phát triển hệ thống rental

Thời điểm xác minh: 31/10/2026, múi giờ Asia/Saigon.

## Phạm vi checkpoint

Checkpoint này ghi nhận phiên bản nhà sách trước khi bổ sung hội viên, VietQR, cho thuê sách và bán sách cũ. Phiên bản đã có catalog thật, ảnh bìa cục bộ, phân quyền khách hàng/nhân viên/admin, hồ sơ, yêu thích, giỏ hàng, đơn COD và quản lý tài khoản nhân viên.

## Kết quả kiểm thử

- `mvnw.cmd -B test`: 52 test, 0 lỗi, 0 thất bại, 0 bỏ qua.
- `mvnw.cmd -B -DskipTests package`: thành công.
- `node --check src/main/resources/static/js/app.js`: thành công.
- `git diff --check`: không có lỗi whitespace; Git chỉ cảnh báo một số file LF sẽ được đổi sang CRLF trên Windows.
- JAR SHA-256: `B7E1B40A49470906764CC6D4D52B4120FFFECC27F14537EB27E86092FC4E0ED2`.

## Cơ sở dữ liệu SQLite persistent

- File chạy: `database/qlns.sqlite`.
- `PRAGMA integrity_check`: `ok`.
- `PRAGMA foreign_key_check`: không có vi phạm.
- 40 sách, 40 ISBN khác nhau, 40 bìa cục bộ và 33 nhà xuất bản.
- 0 tài khoản, 0 khách hàng, 0 đơn hàng, 0 dòng đơn hàng và 0 sách yêu thích.
- SHA-256: `D8248F7EB6D2D0CE0FFC7D077FFBEA2DC8D379A2F6A37CB1C3B0BC5B551B1903`.
- Bản sao trước rental: `C:\Users\mikul\OneDrive\Documents\QLNS_Nhom14\backups\QLNS_Nhom14_GitHub-pre-rental-20261031.sqlite`.
- Bản sao có cùng SHA-256 và `integrity_check = ok`.

## Kiểm tra runtime packaged

JAR được chạy với profile `sqlite` và DB persistent trên cổng 8114:

- `/api/system/status`: ứng dụng `UP`, database `CONNECTED`, profile `sqlite`.
- `/api/sach?page=0&size=1`: tổng cộng 40 sách.
- `/`: HTTP 200 và trả đúng trang nhà sách.
- Tài khoản demo cố định bị từ chối với HTTP 401 trong runtime persistent.

DB persistent chưa có quản trị viên. Đây là trạng thái an toàn theo thiết kế: cần cung cấp biến môi trường `QLNS_INITIAL_ADMIN_PASSWORD` trong lần khởi động tạo admin đầu tiên; không đưa mật khẩu thật vào source hoặc tài liệu.

## Cổng vào giai đoạn rental

Chỉ bắt đầu migration rental trên nhánh `feature/rental-system`. Mọi migration phải bảo toàn các số liệu và checksum logic ở trên, giữ toàn bộ test cũ xanh và có đường khôi phục từ bản sao SQLite.
