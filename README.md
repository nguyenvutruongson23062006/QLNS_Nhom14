# Quản lý nhà sách Nhóm 14 — bản tích hợp dùng cho báo cáo

Ứng dụng có storefront, đăng nhập phiên máy chủ, phân quyền ADMIN/NHAN_VIEN/KHACH_HANG,
quản lý nhân viên, hồ sơ, yêu thích, giỏ hàng và đơn COD. Checkout lấy giá từ cơ sở dữ liệu,
khóa/kiểm tra tồn kho, chống gửi trùng và hoàn tồn đúng một lần khi hủy.

Đã tích hợp nội dung tuần 1 đến tuần 4 cho cả 3 thành viên theo kế hoạch 5 tuần.
Tuần 2: thành viên 1 làm API sách; thành viên 2 làm API danh mục và nhà xuất bản;
thành viên 3 làm API khách hàng. Tài liệu bàn giao: `docs/05-ban-giao-tuan-2.md`;
JSON và hướng dẫn thử: `docs/06-huong-dan-api-tuan-2.md`.
Java 21, Spring Boot 4.1.1, Maven Wrapper, Spring Web MVC, Data JPA,
Validation, MySQL Connector và H2 cho demo.

## Chạy nhanh trên Windows

Máy cần JDK 21 và Internet ở lần đầu để Maven Wrapper tải Maven/dependencies.
Mở PowerShell:

```powershell
cd D:\QLNS_Nhom14
.\mvnw.cmd test

# Lần chạy SQLite đầu tiên: tự đặt mật khẩu admin, không dùng mật khẩu cố định trong mã nguồn.
$env:QLNS_INITIAL_ADMIN_PASSWORD = Read-Host "Mật khẩu admin ban đầu"
.\mvnw.cmd spring-boot:run
```

Mở http://localhost:8080 để xem website quản lý nhà sách.
API http://localhost:8080/api/system/status kiểm tra ứng dụng và kết nối CSDL.
Mặc định dùng profile `sqlite`, lưu bền vững tại `database/qlns.sqlite`. Biến
`QLNS_INITIAL_ADMIN_PASSWORD` chỉ được dùng nếu cơ sở dữ liệu chưa có quản trị viên;
mật khẩu được băm BCrypt và không được ghi vào tệp. Các lần chạy sau không cần đặt lại biến.
Muốn chạy dữ liệu H2 dùng một lần, đặt `SPRING_PROFILES_ACTIVE=demo`.
Tắt ứng dụng bằng Ctrl+C. Nếu cổng 8080 đã dùng, đặt `$env:PORT = '8081'` trước khi chạy.

## Chạy với MySQL

1. Cài và khởi động MySQL 8.0.16 trở lên.
2. Trong MySQL Workbench mở và chạy `database/01-create-database.sql`.
3. Chọn schema `qlns_nhom14` (hoặc chạy `USE qlns_nhom14;`).
4. Mở và chạy `src/main/resources/db/schema.sql`, rồi `src/main/resources/db/data.sql`.
   Chạy một lần trên schema mới. Script không có lệnh DROP.
5. Tạo tài khoản ứng dụng bằng tài khoản quản trị MySQL, thay mật khẩu mẫu trước khi chạy:

```sql
CREATE USER 'qlns_app'@'localhost' IDENTIFIED BY 'THAY_BANG_MAT_KHAU_CUA_BAN';
GRANT SELECT, INSERT, UPDATE, DELETE ON qlns_nhom14.* TO 'qlns_app'@'localhost';
```

6. Trong PowerShell cấu hình rồi chạy:

```powershell
cd D:\QLNS_Nhom14
$env:SPRING_PROFILES_ACTIVE = 'mysql'
$env:DB_USERNAME = 'qlns_app'
$env:DB_PASSWORD = 'MAT_KHAU_BAN_DA_DAT'
$env:QLNS_INITIAL_ADMIN_PASSWORD = Read-Host "Mật khẩu admin ban đầu"
.\mvnw.cmd spring-boot:run
```

Nếu dùng host/port khác, đặt `DB_URL` theo mẫu trong `application-mysql.yml`.
Profile MySQL không tự tạo hoặc sửa bảng. Hibernate `ddl-auto=none`; schema được quản lý bằng SQL tuần 1 và không tự sửa khi chạy JPA.
API trạng thái chỉ kiểm tra kết nối; kiểm tra đủ bảng bằng script kiểm chứng. Tài khoản
nhân viên chỉ do admin tạo trong mục **Nhân viên** và không thể tự nâng quyền.

Để quay lại demo trong cùng cửa sổ:

```powershell
$env:SPRING_PROFILES_ACTIVE = 'demo'
.\mvnw.cmd spring-boot:run
```

## Đóng gói và mở IDE

```powershell
.\mvnw.cmd clean package
java -jar target\qlns-0.0.1-SNAPSHOT.jar
```

Trong IntelliJ IDEA, Eclipse hoặc NetBeans: mở/import thư mục này như Maven project,
chọn JDK 21 rồi chạy `vn.nhom14.qlns.QlnsApplication`.
Không cần cài Maven riêng. Linux/macOS dùng `./mvnw` thay `mvnw.cmd`.

## Cấu trúc và bàn giao

| Đường dẫn | Nội dung |
|---|---|
| docs/01-phan-tich-yeu-cau.md | Thành viên 1: yêu cầu và use case |
| docs/02-thiet-ke-csdl.md | Thành viên 2: thiết kế và quy tắc CSDL |
| docs/03-so-do-erd.md | Sơ đồ quan hệ 8 bảng |
| database/ | Tạo database MySQL và script kiểm chứng |
| src/main/resources/db/ | SQL tạo bảng và dữ liệu mẫu dùng chung |
| src/main/java/vn/nhom14/qlns/ | 4 module CRUD, DTO và xử lý lỗi; ứng dụng và API trạng thái |
| src/main/resources/ | Cấu hình demo/MySQL, trang khởi tạo |
| src/test/java/ | Kiểm thử tuần 1 và HTTP CRUD tuần 2, validation, rollback, khóa ngoại |
| docs/04-nghiem-thu-tuan-1.md | Checklist bàn giao và kết quả kiểm chứng |
| docs/12-bao-cao-kiem-thu.md | Báo cáo kiểm thử tổng hợp H2, SQLite, API, bảo mật và bản đóng gói |

Tuần 2 đã có `entity`, `repository`, `service`, `dto`, `exception` và controller nghiệp vụ.
Các endpoint CRUD: `/api/sach`, `/api/danh-muc`, `/api/nha-xuat-ban`, `/api/khach-hang`.
Danh sách có phân trang `?page=0&size=20`. Các API trả JSON; tuần 3 đã bổ sung giao diện form,
đăng nhập/phân quyền và đơn hàng ở tuần 4. Các tài liệu tuần 1 được giữ để đối chiếu.

Tham khảo môi trường Spring Boot: https://docs.spring.io/spring-boot/system-requirements.html

## Giao diện và phân quyền

Trang chủ: `/`; sách: `/sach.html`; giỏ hàng: `/gio-hang.html`; đơn hàng: `/don-hang.html`;
hồ sơ: `/ca-nhan.html`; quản trị: `/quan-tri.html`; đăng nhập: `/dang-nhap.html`.

- `ADMIN`: toàn bộ khu quản trị, tài khoản nhân viên, danh mục, NXB, sách, khách hàng và đơn hàng.
- `NHAN_VIEN`: sách, khách hàng và xử lý đơn; không được tạo nhân viên hoặc sửa danh mục/NXB.
- `KHACH_HANG`: hồ sơ, ảnh đại diện, yêu thích, giỏ hàng, đặt/xem/hủy đơn của chính mình.

Profile `demo` mới có tài khoản mẫu `admin` và `bandoc`; SQLite/MySQL thật không tự tạo
mật khẩu công khai. Ảnh đại diện dùng một tệp ổn định cho mỗi tài khoản; toàn bộ kho ảnh
có giới hạn 500 MB để tránh tải tệp làm đầy ổ đĩa.

Phân công, cách demo và kết quả kiểm chứng: [Bàn giao tuần 3](docs/07-ban-giao-tuan-3.md). Đăng nhập hiện là giao diện minh họa; xác thực và phân quyền triển khai tuần 4.


