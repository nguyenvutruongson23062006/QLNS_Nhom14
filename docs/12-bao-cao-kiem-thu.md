# BÁO CÁO KIỂM THỬ VÀ NGHIỆM THU HỆ THỐNG

**Đề tài:** Hệ thống quản lý nhà sách Nhóm 14

**Ngày kiểm thử:** 03/11/2026

**Phiên bản:** nhánh `feature/rental-system`

**Môi trường:** Windows, Java 21.0.11, Spring Boot 4.1.1, H2 và SQLite 3.50.3

## 1. Mục tiêu và phạm vi

Lần nghiệm thu này kiểm tra cả nền tảng nhà sách và phần mở rộng rental: xác thực/phân quyền,
CRUD, hồ sơ/yêu thích, đơn COD, hội viên 50.000 đồng, VietQR chờ đối soát, thuê online/tại
quầy, bản sao vật lý, giao-trả-gia hạn, sách cũ và đóng gói JAR.

| Nhóm | Phương pháp |
|---|---|
| Java/API | Maven Surefire, Spring Boot Test, HTTP thật trên cổng ngẫu nhiên |
| CSDL | Cùng bộ nghiệp vụ trên H2 và SQLite, kiểm tra transaction/ràng buộc/idempotency |
| Frontend | `node --check`, chạy JAR demo và thao tác thật trong trình duyệt |
| Đóng gói | Maven Package và khởi động JAR |
| Dữ liệu bền vững | Migration có version, `integrity_check`, `foreign_key_check`, đếm dữ liệu trước/sau |

Không dùng `maven.test.skip`; các test đều được biên dịch và thực thi thật.

## 2. Kết quả tự động

```powershell
.\mvnw.cmd -B test
```

**72 test đạt, 0 failure, 0 error, 0 skipped.**

| Test suite | Test |
|---|---:|
| `HoSoIntegrationTests` / `SqliteHoSoTests` | 2 / 2 |
| `MembershipPaymentIntegrationTests` / `SqliteMembershipPaymentTests` | 4 / 4 |
| `RentalIntegrationTests` / `SqliteRentalTests` | 4 / 4 |
| `UsedBookIntegrationTests` / `SqliteUsedBookTests` | 1 / 1 |
| `OrderAndStaffIntegrationTests` | 6 |
| `SqliteIntegrationTests` | 17 |
| `YeuThichIntegrationTests` / `SqliteYeuThichTests` | 4 / 4 |
| `WeekOneIntegrationTests` / `WeekTwoIntegrationTests` | 4 / 14 |
| `QlnsApplicationTests` | 1 |
| **Tổng** | **72** |

## 3. Ca nghiệp vụ quan trọng

| Mã | Ca kiểm thử | Kết quả |
|---|---|---|
| AUTH-01 | Khách/nhân viên gọi API vượt quyền | 403; quyền được chặn tại backend |
| ORDER-01 | Server tự tính giá, khóa tồn, gửi lại cùng mã | Không tin giá client, không trừ tồn hai lần |
| ORDER-02 | Hai khách mua cuốn cuối | Một thành công, một 409 |
| MEMBER-01 | Đăng ký một thẻ 50.000 đồng, nhận tại quầy/giao hàng | Đúng phí, không tạo hồ sơ thứ hai |
| MEMBER-02 | Khách tự đối soát hoặc phát hành thẻ trước thanh toán | Bị từ chối |
| PAY-01 | Hai khoản dùng cùng mã giao dịch | Khoản thứ hai bị 409 |
| RENT-01 | Chưa có thẻ hoạt động/kích hoạt nhưng thuê | Bị từ chối |
| RENT-02 | Phân bổ bản sao và giới hạn 5 bản đang giữ | Transaction giữ đúng bản, vượt giới hạn bị từ chối |
| RENT-03 | Gửi lại mã yêu cầu cùng/khác nội dung | Cùng nội dung không tạo trùng; khác nội dung bị 409 |
| RENT-04 | Giao trước duyệt/thanh toán | Bị từ chối |
| RENT-05 | Gia hạn VietQR | Chỉ cộng ngày sau đối soát |
| RENT-06 | Trả trễ/hư hỏng | Tính phí theo server, cập nhật tình trạng từng bản |
| USED-01 | Đưa bản chưa từng hoàn trả sang sách cũ | Bị từ chối |
| USED-02 | Giá sách cũ bằng/cao hơn giá mới | Bị từ chối |
| USED-03 | Hai khách đặt cùng độc bản | Chỉ một đơn thành công |
| USED-04 | Hủy/hoàn thành đơn sách cũ | Hủy mở bán lại; hoàn thành chuyển bản sang `DA_BAN` |

## 4. Kiểm thử trình duyệt trên JAR demo

JAR chạy tại `http://localhost:8114` với profile `demo`. Các bước sau đã được thao tác qua
giao diện, không thay dữ liệu SQLite chính:

1. Admin cấu hình Clean Code, giá thuê 3.000 đồng/ngày, cọc 50.000 đồng và thêm 2 bản sao.
2. Bạn đọc thấy nút **Cần kích hoạt thẻ hội viên**, đăng ký thẻ và nhận khoản 50.000 đồng
   ở trạng thái **Chờ đối soát**.
3. Admin đối soát, phát hành và kích hoạt; bạn đọc thấy đúng hiệu lực một năm.
4. Bạn đọc thuê online; admin duyệt, đối soát 92.000 đồng, giao và nhận trả với tình trạng thật.
5. Bản trả xuất hiện trong ứng viên sách cũ, được tạo mục, admin duyệt giá 180.950 đồng.
6. Bạn đọc đặt mua độc bản COD; admin chuyển đơn qua xác nhận, giao và hoàn thành.
7. Admin tạo thêm phiếu thuê tại quầy, nhập mã phiếu thu, giao và nhận trả thành công.
8. Console trình duyệt ở các màn hình kết thúc không ghi nhận lỗi JavaScript.

Trong lúc kiểm thử đã phát hiện và sửa các lỗi UI: người đã đăng nhập nhưng chưa kích hoạt thẻ
bị nhắc “Đăng nhập”; giao dịch thất bại bị mô tả như đã đối soát; một số trạng thái chưa Việt
hóa; đơn sách cũ tạo mã gửi mới ở mọi lần thử lại dù response có thể bị mất.

## 5. SQLite bền vững và migration

- Có bản sao lưu pre-rental ở ngoài repository trước khi chạy migration.
- Migration version 2, 3 và 4 chỉ chạy một lần; khởi động lại không tạo bảng/dữ liệu lặp.
- Dữ liệu nền được giữ nguyên: 40 sách và 33 nhà xuất bản.
- `PRAGMA integrity_check` trả `ok`; `PRAGMA foreign_key_check` không có dòng lỗi.
- Database runtime và file sao lưu không được đưa vào commit.

## 6. Các lỗi đã xử lý đáng chú ý

| Lỗi | Mức độ | Cách xử lý |
|---|---|---|
| Checkout/thuê có thể bị gửi lại sau mất mạng | Cao | `clientRequestId`, so sánh toàn bộ nội dung và transaction |
| Một bản sách cũ có thể bị hai người tranh mua | Cao | Khóa/trạng thái nguyên tử, 409 cho người đến sau |
| VietQR có thể bị hiểu là đã thanh toán chỉ vì có QR | Cao | Luôn chờ đối soát, mã giao dịch duy nhất, quyền staff/admin |
| Bản sao có thể sai vòng đời khi hủy/hoàn tất | Cao | Trạng thái riêng, hoàn lại khi hủy, `DA_BAN` khi hoàn thành |
| Retry sách cũ trên UI sinh mã mới | Trung bình | Lưu mã theo tài khoản+nội dung đến khi nhận response thành công |
| Nhãn thanh toán/thẻ gây hiểu sai | Trung bình | Hiển thị trạng thái theo từng trường hợp và Việt hóa |

## 7. Giới hạn chưa được chứng minh

- Chưa chạy runtime MySQL vì máy nghiệm thu không có database MySQL dành cho bài. SQL đã được
  bổ sung nhưng không được ghi là đã kiểm thử runtime.
- VietQR chưa có webhook ngân hàng; đối soát là thủ công.
- Chưa tích hợp hãng vận chuyển, cổng hoàn tiền cọc hoặc thông báo email/SMS.
- Chưa kiểm thử tải lớn, mất điện giữa transaction hoặc nhiều trình duyệt/OS tự động.

## 8. Kết luận

Bản hiện tại đạt kiểm thử tự động H2/SQLite, đóng gói và luồng trình duyệt chính trong phạm vi
nêu trên. Các chức năng rental và sách cũ có kiểm soát quyền, transaction và kiểm thử hồi quy.
Không nên tuyên bố sẵn sàng vận hành thương mại cho đến khi kiểm thử MySQL, webhook thanh toán,
sao lưu/khôi phục và tải đồng thời được thực hiện trong môi trường triển khai thật.
