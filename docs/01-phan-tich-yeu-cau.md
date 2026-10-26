# Phân tích yêu cầu quản lý nhà sách

## Mục tiêu và phạm vi tuần 1

Xây dựng website quản lý nhà sách của Nhóm 14 bằng Java Spring Boot, REST API,
Spring Data JPA, MySQL và giao diện HTML/CSS/JavaScript/Bootstrap.
Tuần 1 bàn giao phân tích yêu cầu, thiết kế CSDL và project khởi tạo.
Các yêu cầu nghiệp vụ dưới đây là cơ sở triển khai tuần 2 đến tuần 5;
chưa phải chức năng đã hoàn thành trong tuần 1.

## Vai trò và quyền sử dụng dự kiến

| Chức năng | Quản trị viên | Nhân viên | Khách hàng |
|---|---|---|---|
| Xem, tìm sách đang bán | Có | Có | Có |
| Quản lý sách, kiểm tra tồn kho | Có | Có | Không |
| Quản lý danh mục, nhà xuất bản | Có | Không | Không |
| Quản lý khách hàng | Có | Tra cứu | Chỉ hồ sơ của mình |
| Đặt và theo dõi đơn | Có | Xử lý đơn | Chỉ đơn của mình |
| Quản lý tài khoản và vai trò | Có | Không | Không |
| Xem thống kê kinh doanh | Có | Không | Không |

Người chưa đăng nhập được xem và tìm sách, đăng ký, đăng nhập.
Các quyền chi tiết trong bảng là đề xuất thiết kế từ phân công trong kế hoạch,
cần thống nhất khi triển khai phân quyền ở tuần 4.

## Yêu cầu chức năng

| Mã | Yêu cầu | Điều kiện nghiệm thu khi triển khai | Tuần |
|---|---|---|---|
| FR01 | Quản lý sách | Thêm, xem, sửa, ngừng bán sách; mã sách duy nhất; giá và tồn kho không âm | 2 |
| FR02 | Quản lý danh mục và NXB | CRUD; tên duy nhất; không xóa bản ghi đang được sách sử dụng | 2 |
| FR03 | Quản lý khách hàng | CRUD hồ sơ; tên bắt buộc; kiểm tra định dạng liên hệ | 2–3 |
| FR04 | Đăng ký, đăng nhập | Tên đăng nhập/email duy nhất; lưu mật khẩu băm; đăng ký mặc định vai trò khách hàng | 3–4 |
| FR05 | Đặt hàng | Kiểm tra tồn kho; lấy giá từ server; lưu giá tại thời điểm mua | 4 |
| FR06 | Xử lý đơn | Chuyển trạng thái theo luồng; khách chỉ xem đơn của mình | 4 |
| FR07 | Tìm kiếm | Tìm theo tên/tác giả, lọc danh mục/NXB; phân trang | 4 |
| FR08 | Thống kê | Doanh thu theo thời gian, sách bán chạy, sách tồn thấp; chỉ tính đơn hoàn thành | 4 |
| FR09 | Quản lý tài khoản | Admin tạo/khóa tài khoản, gán vai trò; tài khoản bị khóa không đăng nhập được | 4 |

## Quy tắc nghiệp vụ

1. Một sách thuộc một danh mục và một nhà xuất bản. Mỗi tài khoản có một vai trò.
2. Khách mua tại quầy có thể có hồ sơ mà chưa có tài khoản; khách mua online phải có tài khoản liên kết.
3. Mỗi đơn thuộc một khách hàng; một đầu sách xuất hiện tối đa một dòng trong đơn.
4. Số lượng mua là số nguyên dương; đơn giá và tồn kho không âm.
5. Tổng tiền bằng tổng `so_luong * don_gia` của các dòng. Server tính tổng,
   không tin số tiền do trình duyệt gửi. Tuần 1 chưa thiết kế thuế, phí vận chuyển hay giảm giá.
6. Đơn giá trong chi tiết đơn là giá đã chốt; sửa giá sách không thay đổi lịch sử đơn.
7. Luồng đơn: CHO_XAC_NHAN → DA_XAC_NHAN → DANG_GIAO → HOAN_THANH.
   Cho phép hủy từ CHO_XAC_NHAN hoặc DA_XAC_NHAN. Không hủy đơn đã hoàn thành.
8. Đề xuất trừ tồn ngay khi tạo đơn, hoàn tồn một lần khi hủy. Toàn bộ thao tác đơn,
   chi tiết, tồn kho và tổng tiền phải cùng transaction; khóa dòng/kiểm soát đồng thời để tránh bán quá tồn.
9. Không xóa sách/khách/tài khoản đã có lịch sử đơn; dùng ngừng bán hoặc khóa tài khoản.
10. Quyền nhân viên xử lý đơn và tài khoản khách hàng phải được service kiểm tra;
    khóa ngoại chỉ đảm bảo bản ghi tồn tại, không kiểm tra vai trò.

## Yêu cầu phi chức năng

- Dữ liệu và giao diện hỗ trợ tiếng Việt UTF-8; tiền dùng DECIMAL/BigDecimal, không dùng float/double.
- Mô hình controller → service → repository → entity; dùng DTO khi làm API nghiệp vụ.
- REST dùng mã HTTP phù hợp: 200/201/204, 400 khi dữ liệu sai, 401/403 khi thiếu quyền, 404 khi không tồn tại.
- Không lưu mật khẩu rõ, không commit mật khẩu MySQL. Dùng biến môi trường.
- Tuần 4 bổ sung Spring Security và cơ chế băm mật khẩu; tuần 1 chưa có đăng nhập/phân quyền.
- Các chức năng tìm kiếm/danh sách có phân trang; dữ liệu lỗi trả thông báo dễ hiểu.

## Use case tiêu biểu

**UC01 Quản lý sách:** Nhân viên/admin nhập dữ liệu → kiểm tra danh mục/NXB,
mã sách, giá, tồn → lưu → trả kết quả. Dữ liệu sai hoặc mã trùng không được lưu.

**UC02 Đặt hàng:** Khách đăng nhập → chọn sách/số lượng → nhập thông tin nhận →
server kiểm tra tồn và tính tổng → lưu đơn/chi tiết và cập nhật tồn cùng transaction.
Nếu thiếu tồn hoặc bước lưu lỗi, rollback toàn bộ.

**UC03 Xử lý đơn:** Nhân viên/admin mở đơn → kiểm tra trạng thái hiện tại →
chuyển trạng thái hợp lệ. Hủy đơn hoàn tồn đúng một lần.

## Phân công tuần 1

| Người phụ trách trong kế hoạch | Sản phẩm |
|---|---|
| Thành viên 1 | Tài liệu này: vai trò, yêu cầu, use case, quy tắc nghiệp vụ |
| Thành viên 2 | `02-thiet-ke-csdl.md`, sơ đồ ERD, SQL tạo 8 bảng và dữ liệu mẫu |
| Thành viên 3 | Spring Boot, Maven Wrapper, cấu hình MySQL/demo, API kiểm tra và hướng dẫn chạy |

Kế hoạch chưa nêu tên 3 thành viên nên giữ nhãn Thành viên 1/2/3.
