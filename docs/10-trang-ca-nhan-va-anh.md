# Trang cá nhân và tải ảnh

Mở http://localhost:8095/ca-nhan.html sau khi đăng nhập hoặc bấm tên tài khoản trên thanh điều hướng.

Người dùng cập nhật họ tên, email, số điện thoại, địa chỉ, ảnh đại diện và thể loại yêu thích tự nhập (tối đa 500 ký tự). Tên đăng nhập chỉ đọc. Backend lấy ID tài khoản từ phiên đăng nhập; không nhận ID do client gửi. Email được cập nhật đồng thời trong tai_khoan và khach_hang, rollback khi lỗi trùng email.

GET /api/ho-so đọc hồ sơ; PUT /api/ho-so cập nhật. Thể loại tự nhập lưu tại ho_so_ca_nhan.the_loai_yeu_thich, độc lập với thống kê đếm sách trong sach_yeu_thich.

Ảnh đại diện: chọn JPG/PNG ở trang cá nhân, xem trước rồi bấm Lưu thông tin.
`POST /api/anh/dai-dien` nhận multipart field `file`. Ảnh được lưu vào thư mục `uploads`
với tên băm ổn định theo tài khoản, nên mỗi lần đổi ảnh sẽ ghi đè tệp cũ của chính tài khoản đó.

Ảnh sách: admin/nhân viên vào Quản trị > Sách > Sửa; chọn ảnh dưới trường đường dẫn hình ảnh,
xem trước rồi bấm Lưu dữ liệu. `POST /api/anh/sach` chỉ cho `ADMIN` hoặc `NHAN_VIEN`.
Đường dẫn được lưu vào `sach.hinh_anh` để trang chủ, tủ sách và chi tiết hiển thị ảnh mới.

Ảnh đại diện ghi đè vào một tệp ổn định theo tài khoản, không tạo file rác sau mỗi lần đổi ảnh.
Kho upload có giới hạn tổng 500 MB; khi đạt ngưỡng hệ thống trả lỗi 507 và yêu cầu quản trị viên
dọn ảnh không còn được sử dụng. Nhân viên và admin có thể tải ảnh sách; khách hàng chỉ được tải ảnh đại diện.

Backend kiểm tra định dạng thực bằng bộ đọc ảnh, giới hạn 5 MB và 16 triệu điểm ảnh rồi chuyển
thành PNG. Ảnh sách dùng UUID; ảnh đại diện dùng băm tài khoản. Không sử dụng tên file người dùng
làm đường dẫn lưu. Chọn ảnh mới chưa đồng nghĩa lưu hồ sơ/sách; cần bấm nút Lưu.

MySQL có sẵn: chạy database/04-ho-so-ca-nhan.sql trên schema hiện có. Với database mới, schema.sql đã bao gồm bảng mới. Không chạy lại toàn bộ data.sql trên dữ liệu đã có. Chưa kiểm chứng MySQL thực tế.

Demo dùng H2 trong RAM nên dữ liệu hồ sơ mất khi khởi động lại. File ảnh trong `uploads` vẫn nằm
trên ổ đĩa. SQLite mặc định và MySQL đều lưu hồ sơ bền vững.

Integration test kiểm tra sửa/lưu/đọc lại hồ sơ, phân tách tài khoản, trùng email và rollback,
validation, CSRF, ảnh không hợp lệ, ảnh PNG hợp lệ và quyền upload ảnh sách. Kiểm tra trình duyệt
thật vẫn là bước nghiệm thu riêng, không được suy ra chỉ từ test HTTP.
