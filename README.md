# Cập nhật đăng nhập và chi tiết sách

Đã bổ sung đăng nhập thật, phân quyền admin, đăng ký bạn đọc và chi tiết sách. Xem [Hướng dẫn bản mới](docs/08-dang-nhap-va-chi-tiet-sach.md) để lấy tài khoản và chạy bản cập nhật. Các ghi chú đăng nhập minh họa bên dưới là tài liệu của phiên bản cũ.

# Quản lý nhà sách Nhóm 14

Đã triển khai tuần 1, tuần 2 và giao diện tuần 3 cho cả 3 thành viên theo kế hoạch 5 tuần.
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
.\mvnw.cmd spring-boot:run
```

Mở http://localhost:8080 để xem website quản lý nhà sách.
API http://localhost:8080/api/system/status kiểm tra ứng dụng và kết nối CSDL.
Mặc định dùng profile `demo` với H2 trong bộ nhớ, tự tạo 8 bảng và dữ liệu mẫu.
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
.\mvnw.cmd spring-boot:run
```

Nếu dùng host/port khác, đặt `DB_URL` theo mẫu trong `application-mysql.yml`.
Profile MySQL không tự tạo hoặc sửa bảng. Hibernate `ddl-auto=none`; schema được quản lý bằng SQL tuần 1 và không tự sửa khi chạy JPA.
API trạng thái chỉ kiểm tra kết nối; kiểm tra đủ bảng bằng script kiểm chứng dưới đây.

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

Tuần 2 đã có `entity`, `repository`, `service`, `dto`, `exception` và controller nghiệp vụ.
Các endpoint CRUD: `/api/sach`, `/api/danh-muc`, `/api/nha-xuat-ban`, `/api/khach-hang`.
Danh sách có phân trang `?page=0&size=20`. Các API trả JSON; tuần 3 đã bổ sung giao diện form,
đăng nhập/phân quyền và đơn hàng ở tuần 4. Các tài liệu tuần 1 được giữ để đối chiếu.

Tham khảo môi trường Spring Boot: https://docs.spring.io/spring-boot/system-requirements.html

## Giao diện tuần 3

Trang chủ: /; sách: /sach.html; quản trị: /quan-tri.html; khách hàng: /khach-hang.html; đăng nhập minh họa: /dang-nhap.html.

Phân công, cách demo và kết quả kiểm chứng: [Bàn giao tuần 3](docs/07-ban-giao-tuan-3.md). Đăng nhập hiện là giao diện minh họa; xác thực và phân quyền triển khai tuần 4.


