# Hướng dẫn API tuần 2

Base URL mặc định: `http://localhost:8080`. Request tạo và sửa gửi `Content-Type: application/json`.

## Đường dẫn và mã HTTP

Áp dụng cho bốn tài nguyên: `sach`, `danh-muc`, `nha-xuat-ban`, `khach-hang`.

| Method | Đường dẫn | Kết quả |
|---|---|---|
| GET | `/api/{tai-nguyen}?page=0&size=20` | 200, danh sách phân trang |
| GET | `/api/{tai-nguyen}/{id}` | 200, một bản ghi; 404 nếu không có |
| POST | `/api/{tai-nguyen}` | 201, bản ghi mới và header Location |
| PUT | `/api/{tai-nguyen}/{id}` | 200, bản ghi đã sửa; 404 nếu không có |
| DELETE | `/api/{tai-nguyen}/{id}` | 204, không có body; 404 nếu không có |

`page` bắt đầu từ 0; `size` từ 1 đến 100; sắp xếp theo id tăng dần.

```json
{
  "content": [],
  "page": 0,
  "size": 20,
  "totalElements": 0,
  "totalPages": 0
}
```

Sai JSON, sai kiểu hoặc vi phạm validation trả 400; trùng UNIQUE hoặc xóa dữ liệu đang được
tham chiếu trả 409. Lỗi có `status`, `message`, `errors`; `errors` chứa tên trường khi validation lỗi.

```json
{
  "status": 400,
  "message": "Dữ liệu không hợp lệ",
  "errors": {"hoTen": "must not be blank"}
}
```

## JSON danh mục

```json
{
  "tenDanhMuc": "Khoa học",
  "moTa": "Sách khoa học phổ thông",
  "trangThai": true
}
```

## JSON nhà xuất bản

```json
{
  "tenNhaXuatBan": "Nhà xuất bản minh họa",
  "diaChi": "Hà Nội",
  "soDienThoai": "0901234567",
  "email": "nxb@example.com",
  "trangThai": true
}
```

## JSON sách

```json
{
  "maSach": "S003",
  "tenSach": "Lập trình Java thực hành",
  "tacGia": "Tác giả minh họa",
  "isbn": null,
  "danhMucId": 1,
  "nhaXuatBanId": 1,
  "namXuatBan": 2026,
  "giaBan": 150000.00,
  "soLuongTon": 10,
  "moTa": "Dữ liệu minh họa",
  "hinhAnh": null,
  "trangThai": true
}
```

`maSach`, `tenSach`, `tacGia`, `danhMucId`, `nhaXuatBanId`, `giaBan`, `soLuongTon`,
`trangThai` bắt buộc. Hai id liên quan phải tồn tại, giá và tồn kho không âm.
Sách mẫu S001 và S002 thuộc dữ liệu demo; dùng mã mới khi thử POST.

## JSON khách hàng

```json
{
  "hoTen": "Nguyễn Văn An",
  "soDienThoai": "0901234567",
  "email": "an@example.com",
  "diaChi": "Thành phố Hồ Chí Minh"
}
```

Chỉ `hoTen` bắt buộc. Không gửi `id`, `ngayTao`, `taiKhoanId` để sửa liên kết tài khoản;
các trường này do server quản lý. Chưa có chức năng tạo tài khoản trong tuần 2.

## Thử CRUD bằng PowerShell

Chạy ứng dụng ở terminal thứ nhất, rồi mở terminal thứ hai:

```powershell
$baseUrl = 'http://localhost:8080'
$data = @{
    hoTen = 'Nguyễn Văn An'
    soDienThoai = '0901234567'
    email = 'an@example.com'
    diaChi = 'Hà Nội'
} | ConvertTo-Json
$created = Invoke-RestMethod -Method Post -Uri "$baseUrl/api/khach-hang" `
    -ContentType 'application/json; charset=utf-8' -Body ([Text.Encoding]::UTF8.GetBytes($data))
$created
Invoke-RestMethod "$baseUrl/api/khach-hang?page=0&size=20"
Invoke-RestMethod "$baseUrl/api/khach-hang/$($created.id)"

$data = @{
    hoTen = 'Nguyễn Văn An đã sửa'
    soDienThoai = '0901234567'
    email = 'an@example.com'
    diaChi = 'Đà Nẵng'
} | ConvertTo-Json
Invoke-RestMethod -Method Put -Uri "$baseUrl/api/khach-hang/$($created.id)" `
    -ContentType 'application/json; charset=utf-8' -Body ([Text.Encoding]::UTF8.GetBytes($data))
Invoke-RestMethod -Method Delete -Uri "$baseUrl/api/khach-hang/$($created.id)"
```

Sau DELETE, GET cùng id trả 404; PowerShell sẽ hiện lỗi HTTP tương ứng.
Với Postman, chọn method và URL theo bảng, Body → raw → JSON, dán mẫu tương ứng.
POST trả id mới; dùng id đó cho GET/PUT/DELETE, không giả định id sau khi chạy test.

API tuần 2 chưa có đăng nhập và phân quyền, dùng trong môi trường phát triển theo kế hoạch.
