# HỆ THỐNG RENTAL, HỘI VIÊN VÀ SÁCH CŨ

**Phiên bản tài liệu:** 03/11/2026

**Phạm vi:** ứng dụng Java Spring Boot `QLNS_Nhom14_GitHub`

## 1. Mục tiêu

Phân hệ rental cho phép một đầu sách có nhiều bản sao vật lý. Khách phải có thẻ hội viên
đang hoạt động và đã kích hoạt mới được thuê. Yêu cầu có thể tạo online hoặc bởi nhân viên
tại quầy. Mỗi bản sao được giữ chỗ, giao, nhận trả và ghi nhận tình trạng riêng. Bản đã trả
có thể được đưa sang quầy sách cũ sau khi nhân viên mô tả và admin duyệt giá.

Hệ thống không tự xác nhận thanh toán từ ảnh chụp QR. VietQR chỉ tạo nội dung/ảnh thanh
toán; nhân viên phải đối chiếu giao dịch thật trước khi nhập mã đối soát.

## 2. Quy tắc nghiệp vụ chính

- Phí hội viên cố định `50.000 đồng/12 tháng`; một khách chỉ có một hồ sơ hội viên.
- Thẻ nhận tại quầy hoặc giao tận nơi. Thẻ chỉ được phát hành sau khi phí đã thanh toán và
  chỉ cho phép thuê khi ở trạng thái `DA_KICH_HOAT`.
- Một hội viên giữ tối đa 5 bản đang nằm trong các phiếu chưa kết thúc.
- Server tự chọn bản sao `SAN_SANG` trong transaction; client không được tự chọn ID bản sao.
- Giá thuê, tiền cọc, số ngày và phí trễ lấy từ cấu hình server tại thời điểm tạo phiếu.
- Cùng `clientRequestId` và cùng nội dung trả lại cùng kết quả; dùng lại mã cho nội dung khác
  bị từ chối để tránh gửi trùng.
- VietQR và tiền mặt đều phải có mã giao dịch/phiếu thu duy nhất khi đối soát thủ công.
- Khi trả, nhân viên phải chọn tình trạng cho từng bản và nhập phí hư hỏng nếu có.
- Chỉ bản đã từng hoàn trả, còn tình trạng bán được và chưa có danh mục sách cũ mới là ứng viên.
- Giá gợi ý theo tình trạng: như mới 70%, tốt 55%, khá 40%, cũ 25% giá sách mới. Admin quyết
  định giá cuối và giá sách cũ bắt buộc thấp hơn giá sách mới.
- Mỗi mục sách cũ đại diện đúng một bản vật lý. Đặt hàng sẽ khóa mục; hủy đơn trả mục về quầy;
  hoàn thành đơn chuyển bản vật lý sang `DA_BAN`.

## 3. Luồng trạng thái

### Hội viên và thẻ

`CHO_THANH_TOAN -> DANG_HOAT_DONG -> HET_HAN`

`CHO_PHAT_HANH -> CHO_NHAN | DANG_GIAO -> DA_KICH_HOAT`

### Phiếu thuê

`CHO_XAC_NHAN -> CHO_THANH_TOAN -> CHO_NHAN_SACH -> DANG_THUE -> HOAN_THANH`

Phiếu có thể chuyển `DA_HUY` trước khi giao. Khi đối soát đã hoàn tất trước lúc duyệt, bước
`CHO_THANH_TOAN` được bỏ qua. Gia hạn chỉ áp dụng ngày mới sau khi khoản gia hạn đã được đối
soát, tránh cộng ngày khi khách chưa thanh toán.

### Sách cũ

`UNG_VIEN -> CHO_DUYET -> DANG_BAN -> DA_BAN`

Mục `CHO_DUYET` hoặc `DANG_BAN` có thể chuyển `NGUNG_BAN` và trả bản sao về kho rental.

## 4. Giao diện

| Người dùng | Trang/chức năng |
|---|---|
| Khách | `/thue-sach.html`: đăng ký thẻ, xem khoản chờ đối soát, thuê, hủy và gia hạn |
| Khách | `/sach-cu.html`: xem tình trạng thật và đặt mua độc bản COD |
| Nhân viên/admin | `quan-tri.html?module=rental`: cấu hình, thêm bản sao, tạo tại quầy, duyệt/giao/trả |
| Nhân viên/admin | `?module=memberships`: đăng ký tại quầy, phát hành/giao/kích hoạt thẻ |
| Nhân viên/admin | `?module=payments`: nhập mã giao dịch hoặc phiếu thu để đối soát |
| Nhân viên/admin | `?module=used-books`: tạo danh mục, ngừng bán; riêng admin được duyệt giá |

## 5. API chính

- `GET /api/rental-catalog`; `PUT /api/rental-catalog/{sachId}`; thêm bản sao tại
  `POST /api/rental-catalog/{sachId}/copies`.
- `POST /api/memberships` hoặc `/api/memberships/customers/{customerId}`; đổi trạng thái thẻ
  tại `PATCH /api/memberships/{id}/card`.
- `PATCH /api/payments/{id}/reconcile` dành cho nhân viên/admin.
- `POST /api/rentals` hoặc `/api/rentals/customers/{customerId}`; các bước `approve`,
  `handover`, `cancel`, `renew`, `return` nằm dưới `/api/rentals/{id}`.
- `GET /api/used-books` là công khai; tạo/quản lý dành cho nhân viên; `approve` chỉ dành cho
  admin; khách đặt mua tại `POST /api/used-books/orders`.

CSRF được bật cho mọi request thay đổi dữ liệu. Quyền được kiểm tra ở backend, không dựa vào
việc ẩn nút trên giao diện.

## 6. Cơ sở dữ liệu và triển khai

- SQLite mặc định dùng migration có version và lưu tại `database/qlns.sqlite`.
- H2 demo tự nạp `db/schema.sql`, `db/rental-schema.sql`, `db/data.sql`.
- MySQL không tự tạo bảng. Trên schema mới phải chạy lần lượt `schema.sql`,
  `rental-schema.sql`, rồi `data.sql`.
- Trước migration SQLite đã có bản sao lưu ở ngoài repository. Không commit database runtime.

VietQR đọc cấu hình từ `QLNS_VIETQR_BANK_BIN`, `QLNS_VIETQR_ACCOUNT_NUMBER` và
`QLNS_VIETQR_ACCOUNT_NAME`. Không có cấu hình thì khoản thanh toán vẫn tồn tại ở trạng thái
chờ nhưng ảnh QR không được tạo.

## 7. Kịch bản demo báo cáo

1. Admin cấu hình một đầu sách, thêm hai bản sao.
2. Bạn đọc đăng ký thẻ 50.000 đồng; trang hiển thị khoản chờ đối soát và chưa cho thuê.
3. Nhân viên đối soát, phát hành và kích hoạt thẻ.
4. Bạn đọc thuê online; admin duyệt, đối soát tiền thuê/cọc và giao sách.
5. Admin nhận trả, chọn tình trạng thật; bản quay về kho.
6. Nhân viên tạo mục sách cũ; admin duyệt giá; bạn đọc đặt COD và admin hoàn thành đơn.
7. Tạo thêm một phiếu tại quầy, nhập mã phiếu thu, giao và nhận trả để minh họa kênh thứ hai.

## 8. Giới hạn trung thực

- Đối soát hiện là thủ công, chưa tích hợp webhook ngân hàng và chưa tự phát hiện chuyển khoản.
- Giao thẻ/giao sách chỉ lưu thông tin nhận và mã vận đơn; chưa tích hợp hãng vận chuyển.
- Hoàn cọc được phản ánh trong nghiệp vụ trả sách nhưng chưa có cổng hoàn tiền tự động.
- Chưa chạy runtime MySQL trong lần nghiệm thu này; chỉ có schema và cấu hình MySQL.
- Chưa có kiểm thử tải lớn hoặc kiểm thử đa trình duyệt tự động.
