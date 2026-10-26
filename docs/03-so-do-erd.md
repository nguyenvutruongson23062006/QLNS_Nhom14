# Sơ đồ quan hệ cơ sở dữ liệu

Mở file bằng trình xem Markdown hỗ trợ Mermaid để hiển thị sơ đồ.

```mermaid
erDiagram
    vai_tro ||--o{ tai_khoan : phan_quyen
    tai_khoan o|--o| khach_hang : ho_so
    danh_muc ||--o{ sach : phan_loai
    nha_xuat_ban ||--o{ sach : xuat_ban
    khach_hang ||--o{ don_hang : dat_hang
    tai_khoan o|--o{ don_hang : xu_ly
    don_hang ||--|{ chi_tiet_don_hang : bao_gom
    sach ||--o{ chi_tiet_don_hang : duoc_mua
    vai_tro {
        bigint id PK
        varchar ma_vai_tro UK
        varchar ten_vai_tro
    }
    tai_khoan {
        bigint id PK
        varchar ten_dang_nhap UK
        varchar mat_khau_hash
        varchar email UK
        bigint vai_tro_id FK
        boolean trang_thai
    }
    khach_hang {
        bigint id PK
        bigint tai_khoan_id FK,UK
        varchar ho_ten
        varchar so_dien_thoai
        varchar dia_chi
    }
    danh_muc {
        bigint id PK
        varchar ten_danh_muc UK
    }
    nha_xuat_ban {
        bigint id PK
        varchar ten_nha_xuat_ban UK
    }
    sach {
        bigint id PK
        varchar ma_sach UK
        varchar ten_sach
        bigint danh_muc_id FK
        bigint nha_xuat_ban_id FK
        decimal gia_ban
        int so_luong_ton
    }
    don_hang {
        bigint id PK
        varchar ma_don_hang UK
        bigint khach_hang_id FK
        bigint nhan_vien_id FK
        varchar trang_thai
        decimal tong_tien
    }
    chi_tiet_don_hang {
        bigint id PK
        bigint don_hang_id FK
        bigint sach_id FK
        int so_luong
        decimal don_gia
    }
```

Sơ đồ hiển thị các thuộc tính cốt lõi; SQL chứa đầy đủ trường dữ liệu.
Đơn hàng nghiệp vụ phải có ít nhất một chi tiết; service sẽ kiểm tra quy tắc này.
