# BÁO CÁO KIỂM THỬ VÀ NGHIỆM THU HỆ THỐNG

**Đề tài:** Hệ thống quản lý nhà sách Nhóm 14  
**Ngày kiểm thử:** 29/10/2026  
**Phiên bản kiểm thử:** Mã nguồn hiện tại của `QLNS_Nhom14_GitHub`  
**Môi trường:** Windows, Java 21.0.11, Spring Boot 4.1.1, Maven Wrapper, H2 và SQLite 3.50.3

## 1. Mục tiêu kiểm thử

- Xác minh ứng dụng biên dịch, chạy test và đóng gói được.
- Kiểm tra các chức năng CRUD, hồ sơ, yêu thích, đăng nhập và phân quyền.
- Kiểm tra luồng giỏ hàng, đặt hàng COD, chống tạo đơn trùng và cập nhật tồn kho.
- Kiểm tra quản lý tài khoản nhân viên và giới hạn quyền theo vai trò.
- Kiểm tra các lỗi hồi quy liên quan đến SQLite và giới hạn mật khẩu BCrypt.
- Ghi rõ phần đã xác minh và phần chưa đủ điều kiện kết luận.

## 2. Phạm vi và phương pháp

| Nhóm | Phương pháp | Phạm vi |
|---|---|---|
| Kiểm thử đơn vị/tích hợp | Maven Surefire, Spring Boot Test | Service, controller, validation, transaction và CSDL |
| Kiểm thử HTTP | Server ngẫu nhiên trong test và runtime JAR | Đăng nhập, CSRF, đăng ký, đổi mật khẩu, CRUD và đơn hàng |
| Kiểm thử CSDL | H2 và SQLite | Ràng buộc, khóa ngoại, tồn kho, idempotency và rollback |
| Kiểm tra frontend | `node --check` và kiểm tra mã được phục vụ từ JAR | Cú pháp JavaScript, giỏ hàng theo tài khoản và phân trang |
| Kiểm tra đóng gói | Maven Package | Tạo JAR Spring Boot có thể khởi động |

Không dùng `maven.test.skip`; các test đều được biên dịch và thực thi thật.

## 3. Kết quả test tự động

Lệnh thực hiện:

```powershell
.\mvnw.cmd -B test
```

Kết quả tổng hợp: **52 test đạt, 0 failure, 0 error, 0 skipped**.

| Test suite | Số test | Failure | Error | Skipped |
|---|---:|---:|---:|---:|
| `HoSoIntegrationTests` | 2 | 0 | 0 | 0 |
| `OrderAndStaffIntegrationTests` | 6 | 0 | 0 | 0 |
| `QlnsApplicationTests` | 1 | 0 | 0 | 0 |
| `SqliteHoSoTests` | 2 | 0 | 0 | 0 |
| `SqliteIntegrationTests` | 16 | 0 | 0 | 0 |
| `SqliteYeuThichTests` | 4 | 0 | 0 | 0 |
| `WeekOneIntegrationTests` | 4 | 0 | 0 | 0 |
| `WeekTwoIntegrationTests` | 13 | 0 | 0 | 0 |
| `YeuThichIntegrationTests` | 4 | 0 | 0 | 0 |
| **Tổng** | **52** | **0** | **0** | **0** |

## 4. Các ca kiểm thử nghiệp vụ chính

| Mã | Ca kiểm thử | Kết quả mong đợi | Kết quả |
|---|---|---|---|
| TC-AUTH-01 | Đăng nhập đúng tài khoản | HTTP 200, trả đúng vai trò | Đạt |
| TC-AUTH-02 | Khách truy cập API quản trị | HTTP 403 | Đạt |
| TC-AUTH-03 | Nhân viên truy cập quản lý tài khoản nhân viên | HTTP 403 | Đạt |
| TC-AUTH-04 | Đổi mật khẩu trên SQLite | HTTP 200, mật khẩu cũ hết hiệu lực | Đạt |
| TC-AUTH-05 | Đăng ký bằng mật khẩu Unicode vượt 72 byte | HTTP 400, không tạo tài khoản | Đạt |
| TC-CRUD-01 | CRUD sách, danh mục, nhà xuất bản và khách hàng | Dữ liệu lưu đúng, validation hoạt động | Đạt |
| TC-DB-01 | Tồn kho âm hoặc khóa ngoại sai | CSDL từ chối thay đổi | Đạt |
| TC-ORDER-01 | Server tính giá từ CSDL | Không tin giá gửi từ trình duyệt | Đạt |
| TC-ORDER-02 | Gửi lại cùng `clientRequestId` | Không tạo đơn/trừ tồn lần hai | Đạt |
| TC-ORDER-03 | Hai khách mua cuốn cuối cùng đồng thời | Một thành công, một HTTP 409 | Đạt |
| TC-ORDER-04 | Hủy đơn nhiều lần | Chỉ hoàn tồn một lần | Đạt |
| TC-ORDER-05 | Khách đọc đơn của khách khác | HTTP 404 | Đạt |
| TC-STAFF-01 | Admin tạo nhân viên | Tài khoản có vai trò `NHAN_VIEN` | Đạt |
| TC-STAFF-02 | Nhân viên sửa sách nhưng không quản lý tài khoản | Quyền backend đúng thiết kế | Đạt |
| TC-UI-01 | Giỏ hàng của hai tài khoản trên cùng trình duyệt | Dùng khóa lưu trữ riêng theo `username` | Đạt qua kiểm tra mã/runtime |
| TC-UI-02 | Danh sách đơn và nhân viên vượt kích thước trang | Có nút Trước/Sau, gọi đúng `page` | Đạt qua kiểm tra mã/runtime |

## 5. Kiểm thử runtime SQLite sau đóng gói

Ứng dụng được đóng gói bằng:

```powershell
.\mvnw.cmd -B -DskipTests package
```

JAR `target/qlns-0.0.1-SNAPSHOT.jar` khởi động thành công với profile `sqlite,test`.

| Kiểm tra runtime | Kết quả |
|---|---|
| Đăng nhập bạn đọc mẫu | HTTP 200 |
| Đổi mật khẩu | HTTP 200 |
| Đăng nhập lại bằng mật khẩu mới | HTTP 200 |
| Đăng ký mật khẩu Unicode 40 ký tự `đ` | HTTP 400 do vượt 72 byte UTF-8 |
| API phân trang đơn hàng | Trả cấu trúc trang hợp lệ |
| API phân trang tài khoản nhân viên | Trả cấu trúc trang hợp lệ |
| JavaScript mới được phục vụ từ JAR | Có mã giỏ hàng `v2` và phân trang mới |

Tiến trình runtime kiểm thử đã được dừng và cổng kiểm thử đã được giải phóng.

## 6. Các lỗi phát hiện và trạng thái xử lý

| Lỗi | Mức độ | Nguyên nhân | Trạng thái |
|---|---|---|---|
| Đổi mật khẩu trả 500 trên SQLite | Cao | SQLite không hỗ trợ `SELECT ... FOR UPDATE` | Đã sửa và có test hồi quy |
| Đăng ký mật khẩu Unicode dài trả 500 | Cao | Bean Validation đếm ký tự, BCrypt giới hạn theo byte | Đã sửa và có test hồi quy |
| Giỏ hàng dùng chung giữa nhiều tài khoản | Trung bình | Một khóa `localStorage` dùng cho mọi người dùng | Đã tách theo tài khoản |
| Đơn hàng/nhân viên chỉ hiển thị 100 bản ghi đầu | Trung bình | Frontend cố định `page=0&size=100` | Đã bổ sung phân trang |

## 7. Giới hạn kiểm thử còn lại

- Chưa chạy runtime MySQL vì máy kiểm thử chưa có dịch vụ và database MySQL của bài.
- Chưa lặp lại kiểm thử click-by-click bằng trình duyệt sau thay đổi cuối; frontend hiện mới được
  xác minh bằng cú pháp JavaScript, nội dung JAR và API phân trang.
- Chưa kiểm thử tải lớn nhiều người dùng hoặc upload ảnh đồng thời trong thời gian dài.
- Chưa kiểm thử trên thiết bị di động và các trình duyệt khác nhau.

Các giới hạn trên không được ghi là “đã đạt” vì chưa có bằng chứng thực thi tương ứng.

## 8. Kết luận nghiệm thu

Bản hiện tại **đạt kiểm thử tự động và runtime SQLite** trong phạm vi đã nêu: 52/52 test đạt,
đóng gói JAR thành công, các lỗi mật khẩu gây HTTP 500 đã được sửa và có test chống tái phát.
Các chức năng chính về CRUD, phân quyền, hồ sơ, yêu thích, đơn hàng, tồn kho và nhân viên có
bằng chứng kiểm thử.

Trước khi triển khai thật trên MySQL hoặc đưa lên Internet, cần hoàn thành kiểm thử runtime
MySQL, kiểm thử trình duyệt cuối cùng, kiểm thử tải và bổ sung cơ chế migration/sao lưu CSDL.
