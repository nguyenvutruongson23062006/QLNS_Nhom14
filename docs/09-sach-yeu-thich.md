# Sách yêu thích và thể loại yêu thích

Đã bổ sung backend và giao diện theo tài khoản đăng nhập. Không lấy tai_khoan_id từ client; sử dụng Authentication.getName() để tìm tài khoản trên server.

## Sử dụng

Bản kiểm tra chạy ở http://localhost:8095/. Đăng nhập bandoc / BanDoc@123456, nhấn ♡ trên thẻ sách hoặc chi tiết sách. Mở /yeu-thich.html để xem các sách đã lưu và bỏ lưu bằng ♥. Trang chủ, tủ sách và trang yêu thích có góc thể loại yêu thích dành cho tài khoản đăng nhập.

Thống kê tính bằng COUNT(*) trên sach_yeu_thich JOIN sach JOIN danh_muc, lọc theo tài khoản. Mỗi cặp tài khoản/sách là duy nhất. Sắp xếp theo số sách giảm dần; đồng hạng được hiển thị cùng nhau. Chưa lưu sách sẽ hiển thị lời hướng dẫn thay vì tự đặt thể loại yêu thích. Tỷ lệ thanh hiển thị = số sách trong thể loại / tổng số sách yêu thích. Thống kê phản ánh thao tác thật của tài khoản, còn danh mục sách mẫu trong dự án vẫn là dữ liệu thực hành.

## Backend

- GET /api/yeu-thich?page=0&size=20: danh sách sách đã lưu, phân trang.
- GET /api/yeu-thich/ids: ID sách đã lưu để đánh dấu trái tim.
- GET /api/yeu-thich/the-loai: tongSoSach và theLoai gồm id, tenDanhMuc, soSach.
- PUT /api/yeu-thich/{sachId}: lưu sách; gọi lặp không tăng số lượng.
- DELETE /api/yeu-thich/{sachId}: bỏ lưu trong tài khoản hiện tại.

Tất cả endpoint yêu cầu đăng nhập. PUT/DELETE cần CSRF token theo cơ chế đang có. Khóa ngoại tự xóa liên kết yêu thích khi sách/tài khoản bị xóa.

Mã chính: controller/YeuThichController.java; service/YeuThichService.java; static/yeu-thich.html; static/js/app.js; static/css/style.css.

## Cơ sở dữ liệu

Schema mới đã bao gồm bảng sach_yeu_thich trong src/main/resources/db/schema.sql. Với MySQL hiện có, chỉ chạy database/03-sach-yeu-thich.sql trên schema qlns_nhom14; không chạy lại toàn bộ schema.sql hoặc data.sql. Script bổ sung CREATE TABLE IF NOT EXISTS không xóa dữ liệu hiện có. Chưa thực thi thay đổi này trên MySQL thực tế.

Demo H2 trong RAM ghi dữ liệu thật trong phiên chạy và giữ khi tải lại trang hoặc đăng nhập lại; dữ liệu mất khi khởi động lại tiến trình. Muốn lưu bền vững dùng profile mysql và tài khoản trong database đó.

## Kiểm chứng

mvnw.cmd test: BUILD SUCCESS, 22 tests, 0 failures/errors/skipped. Bốn test mới kiểm tra: khách chưa đăng nhập; lưu trùng, phân trang và thống kê cập nhật sau bỏ lưu; cách ly hai tài khoản và CSRF; sách không tồn tại và tham số trang sai. Đã kiểm tra giao diện lưu 3 sách (2 Công nghệ thông tin và 1 Văn học), mở trang yêu thích và thấy số liệu đúng từ backend. Không thêm seed sở thích hoặc hardcode số lượng.
