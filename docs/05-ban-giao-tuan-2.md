# Bàn giao tuần 2 quản lý nhà sách Nhóm 14

Tuần 2 triển khai REST API CRUD dựa trên CSDL tuần 1. Phân công lấy từ kế hoạch
5 tuần: thành viên 1 phụ trách sách; thành viên 2 phụ trách danh mục và nhà xuất bản;
thành viên 3 phụ trách khách hàng. Kế hoạch chưa có họ tên nên giữ số thứ tự thành viên.

## Thành viên 1 API quản lý sách

- Entity: `src/main/java/vn/nhom14/qlns/entity/Sach.java`.
- Repository: `src/main/java/vn/nhom14/qlns/repository/SachRepository.java`.
- Service: `src/main/java/vn/nhom14/qlns/service/SachService.java`.
- Controller: `src/main/java/vn/nhom14/qlns/controller/SachController.java`.
- DTO: `dto/SachRequest.java`, `dto/SachResponse.java` trong package `vn.nhom14.qlns`.
- Thêm, xem danh sách, xem chi tiết, sửa và xóa sách qua `/api/sach`.
- Quan hệ nhiều sách thuộc một danh mục và một NXB; kiểm tra bản ghi liên quan tồn tại.
- Mã sách bắt buộc, duy nhất; ISBN có thể bỏ trống, có giá trị thì duy nhất.
- Giá dùng BigDecimal, tối đa 13 chữ số nguyên và 2 chữ số thập phân, không âm.
- Tồn kho không âm; năm xuất bản nếu nhập phải từ 1000 đến 9999.
- Muốn ngừng bán: PUT đủ dữ liệu sách với `trangThai: false`.
- Chặn xóa sách có chi tiết đơn hàng bằng khóa ngoại; trả HTTP 409.

## Thành viên 2 API danh mục và nhà xuất bản

- Entity: `entity/DanhMuc.java`, `entity/NhaXuatBan.java`.
- Repository: `repository/DanhMucRepository.java`, `repository/NhaXuatBanRepository.java`.
- Service: `service/DanhMucService.java`, `service/NhaXuatBanService.java`.
- Controller: `controller/DanhMucController.java`, `controller/NhaXuatBanController.java`.
- DTO: các cặp `DanhMucRequest/Response`, `NhaXuatBanRequest/Response`.
- CRUD qua `/api/danh-muc` và `/api/nha-xuat-ban`.
- Tên bắt buộc và duy nhất; mô tả, địa chỉ có giới hạn độ dài theo CSDL.
- Kiểm tra email và số điện thoại NXB khi nhập.
- Chặn xóa danh mục hoặc NXB đang có sách bằng khóa ngoại; trả HTTP 409.

## Thành viên 3 API quản lý khách hàng

- Entity: `entity/KhachHang.java`.
- Repository: `repository/KhachHangRepository.java`.
- Service: `service/KhachHangService.java`.
- Controller: `controller/KhachHangController.java`.
- DTO: `dto/KhachHangRequest.java`, `dto/KhachHangResponse.java`.
- CRUD qua `/api/khach-hang`; họ tên bắt buộc; email và điện thoại tùy chọn nhưng phải đúng định dạng.
- Tạo hồ sơ khách mua tại quầy không bắt buộc tài khoản.
- `taiKhoanId` chỉ xuất trong response; API hồ sơ chưa tạo hoặc gán tài khoản.
  Sửa hồ sơ giữ nguyên liên kết tài khoản hiện có. Đăng nhập và phân quyền theo tuần 4.
- `ngayTao` do server tạo; chặn xóa khách hàng có đơn hàng, trả HTTP 409.

## Thành phần dùng chung và nguyên tắc

- `dto/PageResponse.java`: danh sách phân trang, sắp xếp id tăng dần.
- `exception/ApiExceptionHandler.java`: trả lỗi JSON 400, 404, 409, không đưa SQL ra response.
- DTO tách khỏi entity; response sách chỉ chứa id danh mục và NXB, không lộ proxy Hibernate.
- Service dùng transaction; cập nhật thất bại được rollback.
- POST/PUT yêu cầu đầy đủ các trường bắt buộc; PUT thay toàn bộ trường hồ sơ có thể sửa.
  Các trường tùy chọn không gửi trong PUT được đặt thành null.
- Các trường text được bỏ khoảng trắng đầu cuối; text tùy chọn rỗng được lưu null.
- Ràng buộc UNIQUE và khóa ngoại trong CSDL là lớp bảo vệ cuối cùng, kể cả truy cập đồng thời.
- Giữ nguyên schema, dữ liệu mẫu và tài liệu tuần 1 làm cơ sở đối chiếu.

## Chạy và nghiệm thu

Tại PowerShell trong `D:\QLNS_Nhom14`:

```powershell
.\mvnw.cmd clean package
.\mvnw.cmd spring-boot:run
```

Mặc định profile demo dùng H2 trong RAM, khởi động lại sẽ trả về dữ liệu mẫu.
Mở `http://localhost:8080` để xem trang giới thiệu; truy cập `/api/sach` để xem JSON.
Giao diện form quản lý thuộc tuần 3. Cấu hình MySQL giữ theo README.

Đọc `docs/06-huong-dan-api-tuan-2.md` để biết JSON, HTTP và thao tác thử.
`WeekTwoIntegrationTests` gửi HTTP thực vào server Spring Boot trên cổng ngẫu nhiên với H2,
không thay thế việc kiểm chứng MySQL thực tế.

## Minh chứng nên chụp khi trình bày

1. Terminal chạy `clean package` và dòng tổng số test cùng `BUILD SUCCESS`.
2. Thành viên 1: POST sách trả 201, GET danh sách, PUT đổi giá/tồn, dữ liệu âm trả 400.
3. Thành viên 2: CRUD danh mục và NXB, tên trùng trả 409, xóa danh mục đang có sách trả 409.
4. Thành viên 3: CRUD khách hàng, email/điện thoại sai trả 400, GET sau xóa trả 404.
5. Khi trình bày CSDL MySQL: chạy profile mysql và chụp dữ liệu sau thao tác API thực tế.
## Kết quả kiểm chứng ngày 16 tháng 10 năm 2026

Đã chạy `.\mvnw.cmd clean package` trực tiếp tại `D:\QLNS_Nhom14`:

| Bộ kiểm thử | Số ca | Thất bại | Lỗi | Bỏ qua |
|---|---:|---:|---:|---:|
| QlnsApplicationTests | 1 | 0 | 0 | 0 |
| WeekOneIntegrationTests | 4 | 0 | 0 | 0 |
| WeekTwoIntegrationTests | 13 | 0 | 0 | 0 |
| Tổng | 18 | 0 | 0 | 0 |

Kết quả Maven: `BUILD SUCCESS`. Đã tạo `target/qlns-0.0.1-SNAPSHOT.jar`.
Máy thực thi bằng JDK 25.0.3, project cấu hình mức Java 21 trong pom.xml.
13 ca tuần 2 gồm 4 luồng CRUD và phân trang, 4 nhóm lỗi request/id/phân trang,
trùng dữ liệu, giá/tồn/năm/quan hệ không hợp lệ, liên hệ sai định dạng,
khóa ngoại bảo vệ dữ liệu và rollback khi sửa sách thất bại.

Bằng chứng nằm trong `target/surefire-reports/`. Các kiểm thử chạy server HTTP thật
với H2 profile demo. Chưa kiểm chứng trên MySQL và chưa kiểm thử giao diện CRUD
vì giao diện quản lý nằm ở tuần 3.
