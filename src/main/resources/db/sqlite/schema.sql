-- MySQL 8.0.16+; H2 chạy cùng script trong chế độ tương thích MySQL.
-- Không DROP bảng. Chỉ chạy một lần trên database mới.
CREATE TABLE IF NOT EXISTS vai_tro (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    ma_vai_tro VARCHAR(20) NOT NULL UNIQUE,
    ten_vai_tro VARCHAR(100) NOT NULL,
    CONSTRAINT ck_vai_tro_ma CHECK (ma_vai_tro IN ('ADMIN', 'NHAN_VIEN', 'KHACH_HANG'))
);
CREATE TABLE IF NOT EXISTS tai_khoan (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    ten_dang_nhap VARCHAR(50) NOT NULL UNIQUE,
    mat_khau_hash VARCHAR(255) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    vai_tro_id BIGINT NOT NULL,
    trang_thai BOOLEAN NOT NULL DEFAULT TRUE,
    ngay_tao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_tai_khoan_vai_tro FOREIGN KEY (vai_tro_id) REFERENCES vai_tro(id)
);
CREATE TABLE IF NOT EXISTS khach_hang (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    tai_khoan_id BIGINT UNIQUE,
    ho_ten VARCHAR(150) NOT NULL,
    so_dien_thoai VARCHAR(20),
    email VARCHAR(150),
    dia_chi VARCHAR(500),
    ngay_tao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_khach_hang_tai_khoan FOREIGN KEY (tai_khoan_id) REFERENCES tai_khoan(id)
);
CREATE TABLE IF NOT EXISTS danh_muc (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    ten_danh_muc VARCHAR(100) NOT NULL UNIQUE,
    mo_ta VARCHAR(1000),
    trang_thai BOOLEAN NOT NULL DEFAULT TRUE
);
CREATE TABLE IF NOT EXISTS nha_xuat_ban (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    ten_nha_xuat_ban VARCHAR(150) NOT NULL UNIQUE,
    dia_chi VARCHAR(500),
    so_dien_thoai VARCHAR(20),
    email VARCHAR(150),
    trang_thai BOOLEAN NOT NULL DEFAULT TRUE
);
CREATE TABLE IF NOT EXISTS sach (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    ma_sach VARCHAR(30) NOT NULL UNIQUE,
    ten_sach VARCHAR(255) NOT NULL,
    tac_gia VARCHAR(150) NOT NULL,
    isbn VARCHAR(20) UNIQUE,
    danh_muc_id BIGINT NOT NULL,
    nha_xuat_ban_id BIGINT NOT NULL,
    nam_xuat_ban INT,
    gia_ban DECIMAL(15,2) NOT NULL,
    so_luong_ton INT NOT NULL DEFAULT 0,
    mo_ta TEXT,
    hinh_anh VARCHAR(500),
    trang_thai BOOLEAN NOT NULL DEFAULT TRUE,
    ngay_tao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_sach_danh_muc FOREIGN KEY (danh_muc_id) REFERENCES danh_muc(id),
    CONSTRAINT fk_sach_nxb FOREIGN KEY (nha_xuat_ban_id) REFERENCES nha_xuat_ban(id),
    CONSTRAINT ck_sach_gia CHECK (gia_ban >= 0),
    CONSTRAINT ck_sach_ton CHECK (so_luong_ton >= 0),
    CONSTRAINT ck_sach_nam CHECK (nam_xuat_ban IS NULL OR nam_xuat_ban BETWEEN 1000 AND 9999)
);
CREATE TABLE IF NOT EXISTS don_hang (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    ma_don_hang VARCHAR(30) NOT NULL UNIQUE,
    khach_hang_id BIGINT NOT NULL,
    nhan_vien_id BIGINT,
    ngay_dat TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    trang_thai VARCHAR(30) NOT NULL DEFAULT 'CHO_XAC_NHAN',
    ten_nguoi_nhan VARCHAR(150) NOT NULL,
    so_dien_thoai_nhan VARCHAR(20) NOT NULL,
    dia_chi_giao VARCHAR(500) NOT NULL,
    tong_tien DECIMAL(15,2) NOT NULL DEFAULT 0,
    ghi_chu VARCHAR(1000),
    CONSTRAINT fk_don_hang_khach FOREIGN KEY (khach_hang_id) REFERENCES khach_hang(id),
    CONSTRAINT fk_don_hang_nhan_vien FOREIGN KEY (nhan_vien_id) REFERENCES tai_khoan(id),
    CONSTRAINT ck_don_hang_tong CHECK (tong_tien >= 0),
    CONSTRAINT ck_don_hang_trang_thai CHECK (trang_thai IN
        ('CHO_XAC_NHAN','DA_XAC_NHAN','DANG_GIAO','HOAN_THANH','DA_HUY'))
);
CREATE TABLE IF NOT EXISTS chi_tiet_don_hang (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    don_hang_id BIGINT NOT NULL,
    sach_id BIGINT NOT NULL,
    so_luong INT NOT NULL,
    don_gia DECIMAL(15,2) NOT NULL,
    CONSTRAINT uq_chi_tiet_sach UNIQUE (don_hang_id, sach_id),
    CONSTRAINT fk_chi_tiet_don FOREIGN KEY (don_hang_id) REFERENCES don_hang(id),
    CONSTRAINT fk_chi_tiet_sach FOREIGN KEY (sach_id) REFERENCES sach(id),
    CONSTRAINT ck_chi_tiet_so_luong CHECK (so_luong > 0),
    CONSTRAINT ck_chi_tiet_gia CHECK (don_gia >= 0)
);
CREATE INDEX IF NOT EXISTS idx_sach_ten ON sach(ten_sach);
CREATE INDEX IF NOT EXISTS idx_don_hang_ngay ON don_hang(ngay_dat);
CREATE INDEX IF NOT EXISTS idx_don_hang_trang_thai ON don_hang(trang_thai);
-- Sách yêu thích theo tài khoản; không thêm dữ liệu giả.
CREATE TABLE IF NOT EXISTS sach_yeu_thich (
    tai_khoan_id BIGINT NOT NULL,
    sach_id BIGINT NOT NULL,
    ngay_luu TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (tai_khoan_id, sach_id),
    CONSTRAINT fk_yeu_thich_tai_khoan FOREIGN KEY (tai_khoan_id) REFERENCES tai_khoan(id) ON DELETE CASCADE,
    CONSTRAINT fk_yeu_thich_sach FOREIGN KEY (sach_id) REFERENCES sach(id) ON DELETE CASCADE
);
CREATE TABLE IF NOT EXISTS ho_so_ca_nhan (
 tai_khoan_id BIGINT PRIMARY KEY,
 anh_dai_dien VARCHAR(500),
 the_loai_yeu_thich VARCHAR(500),
 CONSTRAINT fk_ho_so_tai_khoan FOREIGN KEY (tai_khoan_id) REFERENCES tai_khoan(id) ON DELETE CASCADE
);

