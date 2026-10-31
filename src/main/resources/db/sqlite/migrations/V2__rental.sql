-- Giai đoạn 1: nền dữ liệu hội viên, thuê/trả, thanh toán và sách cũ.
-- File chỉ được SqliteConfig chạy một lần và ghi version=2 sau khi toàn bộ transaction thành công.
CREATE TABLE IF NOT EXISTS hoi_vien (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    khach_hang_id BIGINT NOT NULL UNIQUE,
    ma_hoi_vien VARCHAR(30) NOT NULL UNIQUE,
    ngay_bat_dau DATE,
    ngay_het_han DATE,
    trang_thai VARCHAR(30) NOT NULL DEFAULT 'CHO_THANH_TOAN',
    phi_thanh_vien DECIMAL(15,2) NOT NULL DEFAULT 50000,
    ngay_tao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ngay_cap_nhat TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_hoi_vien_khach_hang FOREIGN KEY (khach_hang_id) REFERENCES khach_hang(id),
    CONSTRAINT ck_hoi_vien_phi CHECK (phi_thanh_vien >= 0),
    CONSTRAINT ck_hoi_vien_ngay CHECK (ngay_het_han IS NULL OR ngay_bat_dau IS NULL OR ngay_het_han >= ngay_bat_dau),
    CONSTRAINT ck_hoi_vien_trang_thai CHECK (trang_thai IN ('CHO_THANH_TOAN','DANG_HOAT_DONG','HET_HAN','DA_HUY'))
);

CREATE TABLE IF NOT EXISTS the_thanh_vien (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    hoi_vien_id BIGINT NOT NULL UNIQUE,
    ma_the VARCHAR(40) NOT NULL UNIQUE,
    token_quet VARCHAR(100) NOT NULL UNIQUE,
    phuong_thuc_nhan VARCHAR(20) NOT NULL,
    trang_thai VARCHAR(30) NOT NULL DEFAULT 'CHO_PHAT_HANH',
    ngay_phat_hanh TIMESTAMP,
    ngay_kich_hoat TIMESTAMP,
    CONSTRAINT fk_the_hoi_vien FOREIGN KEY (hoi_vien_id) REFERENCES hoi_vien(id),
    CONSTRAINT ck_the_phuong_thuc CHECK (phuong_thuc_nhan IN ('TAI_QUAY','GIAO_HANG')),
    CONSTRAINT ck_the_trang_thai CHECK (trang_thai IN ('CHO_PHAT_HANH','CHO_NHAN','DANG_GIAO','DA_KICH_HOAT','DA_HUY'))
);

CREATE TABLE IF NOT EXISTS giao_the (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    the_thanh_vien_id BIGINT NOT NULL UNIQUE,
    ten_nguoi_nhan VARCHAR(150) NOT NULL,
    so_dien_thoai VARCHAR(20) NOT NULL,
    dia_chi VARCHAR(500) NOT NULL,
    phi_giao DECIMAL(15,2) NOT NULL DEFAULT 0,
    ma_van_don VARCHAR(100),
    trang_thai VARCHAR(30) NOT NULL DEFAULT 'CHO_GIAO',
    ngay_tao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ngay_giao TIMESTAMP,
    CONSTRAINT fk_giao_the_the FOREIGN KEY (the_thanh_vien_id) REFERENCES the_thanh_vien(id),
    CONSTRAINT ck_giao_the_phi CHECK (phi_giao >= 0),
    CONSTRAINT ck_giao_the_trang_thai CHECK (trang_thai IN ('CHO_GIAO','DANG_GIAO','DA_GIAO','THAT_BAI','DA_HUY'))
);

CREATE TABLE IF NOT EXISTS thanh_toan (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    ma_thanh_toan VARCHAR(40) NOT NULL UNIQUE,
    khach_hang_id BIGINT NOT NULL,
    muc_dich VARCHAR(30) NOT NULL,
    tham_chieu_loai VARCHAR(40) NOT NULL,
    tham_chieu_id BIGINT NOT NULL,
    phuong_thuc VARCHAR(20) NOT NULL,
    so_tien DECIMAL(15,2) NOT NULL,
    noi_dung_chuyen_khoan VARCHAR(100) NOT NULL UNIQUE,
    ma_giao_dich_nha_cung_cap VARCHAR(100),
    trang_thai VARCHAR(30) NOT NULL DEFAULT 'CHO_THANH_TOAN',
    xac_nhan_boi BIGINT,
    ngay_tao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ngay_xac_nhan TIMESTAMP,
    ghi_chu VARCHAR(1000),
    CONSTRAINT fk_thanh_toan_khach FOREIGN KEY (khach_hang_id) REFERENCES khach_hang(id),
    CONSTRAINT fk_thanh_toan_xac_nhan FOREIGN KEY (xac_nhan_boi) REFERENCES tai_khoan(id),
    CONSTRAINT ck_thanh_toan_tien CHECK (so_tien >= 0),
    CONSTRAINT ck_thanh_toan_muc_dich CHECK (muc_dich IN ('HOI_VIEN','THUE_SACH','TIEN_COC','PHI_PHAT','DON_HANG')),
    CONSTRAINT ck_thanh_toan_phuong_thuc CHECK (phuong_thuc IN ('VIETQR','TIEN_MAT')),
    CONSTRAINT ck_thanh_toan_trang_thai CHECK (trang_thai IN ('CHO_THANH_TOAN','CHO_DOI_SOAT','DA_THANH_TOAN','THAT_BAI','DA_HOAN_TIEN'))
);

CREATE TABLE IF NOT EXISTS cau_hinh_thue_sach (
    sach_id BIGINT PRIMARY KEY,
    cho_thue BOOLEAN NOT NULL DEFAULT TRUE,
    gia_thue_ngay DECIMAL(15,2) NOT NULL,
    tien_coc DECIMAL(15,2) NOT NULL DEFAULT 0,
    so_ngay_mac_dinh INT NOT NULL DEFAULT 14,
    so_lan_gia_han INT NOT NULL DEFAULT 1,
    phi_tre_ngay DECIMAL(15,2) NOT NULL DEFAULT 5000,
    ngay_cap_nhat TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_cau_hinh_thue_sach FOREIGN KEY (sach_id) REFERENCES sach(id),
    CONSTRAINT ck_cau_hinh_gia CHECK (gia_thue_ngay >= 0 AND tien_coc >= 0 AND phi_tre_ngay >= 0),
    CONSTRAINT ck_cau_hinh_ngay CHECK (so_ngay_mac_dinh BETWEEN 1 AND 90 AND so_lan_gia_han BETWEEN 0 AND 10)
);

CREATE TABLE IF NOT EXISTS ban_sao_sach (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    ma_ban_sao VARCHAR(40) NOT NULL UNIQUE,
    sach_id BIGINT NOT NULL,
    tinh_trang VARCHAR(20) NOT NULL DEFAULT 'MOI',
    trang_thai VARCHAR(30) NOT NULL DEFAULT 'SAN_SANG',
    ngay_nhap DATE NOT NULL DEFAULT CURRENT_DATE,
    ghi_chu VARCHAR(1000),
    hoat_dong BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_ban_sao_sach FOREIGN KEY (sach_id) REFERENCES sach(id),
    CONSTRAINT ck_ban_sao_tinh_trang CHECK (tinh_trang IN ('MOI','NHU_MOI','TOT','KHA','CU','HU_HONG')),
    CONSTRAINT ck_ban_sao_trang_thai CHECK (trang_thai IN ('SAN_SANG','DA_GIU','DANG_THUE','BAO_TRI','CHO_BAN_CU','DA_BAN','MAT'))
);

CREATE TABLE IF NOT EXISTS phieu_thue (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    ma_phieu_thue VARCHAR(40) NOT NULL UNIQUE,
    khach_hang_id BIGINT NOT NULL,
    nhan_vien_duyet_id BIGINT,
    nhan_vien_giao_id BIGINT,
    nhan_vien_nhan_id BIGINT,
    kenh_tao VARCHAR(20) NOT NULL,
    trang_thai VARCHAR(30) NOT NULL DEFAULT 'CHO_XAC_NHAN',
    client_request_id VARCHAR(100) NOT NULL,
    ngay_tao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ngay_duyet TIMESTAMP,
    ngay_giao TIMESTAMP,
    ngay_hoan_tat TIMESTAMP,
    tong_tien_thue DECIMAL(15,2) NOT NULL DEFAULT 0,
    tong_tien_coc DECIMAL(15,2) NOT NULL DEFAULT 0,
    tong_phi_phat DECIMAL(15,2) NOT NULL DEFAULT 0,
    phuong_thuc_nhan VARCHAR(20) NOT NULL DEFAULT 'TAI_QUAY',
    ten_nguoi_nhan VARCHAR(150),
    so_dien_thoai_nhan VARCHAR(20),
    dia_chi_giao VARCHAR(500),
    ghi_chu VARCHAR(1000),
    CONSTRAINT uq_phieu_thue_request UNIQUE (khach_hang_id, client_request_id),
    CONSTRAINT fk_phieu_thue_khach FOREIGN KEY (khach_hang_id) REFERENCES khach_hang(id),
    CONSTRAINT fk_phieu_thue_duyet FOREIGN KEY (nhan_vien_duyet_id) REFERENCES tai_khoan(id),
    CONSTRAINT fk_phieu_thue_giao FOREIGN KEY (nhan_vien_giao_id) REFERENCES tai_khoan(id),
    CONSTRAINT fk_phieu_thue_nhan FOREIGN KEY (nhan_vien_nhan_id) REFERENCES tai_khoan(id),
    CONSTRAINT ck_phieu_thue_kenh CHECK (kenh_tao IN ('ONLINE','TAI_QUAY')),
    CONSTRAINT ck_phieu_thue_trang_thai CHECK (trang_thai IN ('CHO_XAC_NHAN','DA_DUYET','CHO_THANH_TOAN','CHO_NHAN_SACH','DANG_THUE','HOAN_THANH','DA_HUY','MAT_SACH','TRANH_CHAP')),
    CONSTRAINT ck_phieu_thue_tien CHECK (tong_tien_thue >= 0 AND tong_tien_coc >= 0 AND tong_phi_phat >= 0),
    CONSTRAINT ck_phieu_thue_nhan CHECK (phuong_thuc_nhan IN ('TAI_QUAY','GIAO_HANG'))
);

CREATE TABLE IF NOT EXISTS chi_tiet_phieu_thue (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    phieu_thue_id BIGINT NOT NULL,
    ban_sao_id BIGINT NOT NULL,
    gia_thue_ngay DECIMAL(15,2) NOT NULL,
    tien_coc DECIMAL(15,2) NOT NULL,
    phi_tre_ngay DECIMAL(15,2) NOT NULL,
    ngay_hen_tra DATE NOT NULL,
    ngay_tra_thuc_te DATE,
    tinh_trang_truoc VARCHAR(20) NOT NULL,
    tinh_trang_sau VARCHAR(20),
    so_ngay_tre INT NOT NULL DEFAULT 0,
    phi_tre DECIMAL(15,2) NOT NULL DEFAULT 0,
    phi_hu_hong DECIMAL(15,2) NOT NULL DEFAULT 0,
    CONSTRAINT uq_chi_tiet_phieu_ban_sao UNIQUE (phieu_thue_id, ban_sao_id),
    CONSTRAINT fk_chi_tiet_phieu FOREIGN KEY (phieu_thue_id) REFERENCES phieu_thue(id),
    CONSTRAINT fk_chi_tiet_ban_sao FOREIGN KEY (ban_sao_id) REFERENCES ban_sao_sach(id),
    CONSTRAINT ck_chi_tiet_tien CHECK (gia_thue_ngay >= 0 AND tien_coc >= 0 AND phi_tre_ngay >= 0 AND phi_tre >= 0 AND phi_hu_hong >= 0),
    CONSTRAINT ck_chi_tiet_ngay_tre CHECK (so_ngay_tre >= 0),
    CONSTRAINT ck_chi_tiet_tinh_trang_truoc CHECK (tinh_trang_truoc IN ('MOI','NHU_MOI','TOT','KHA','CU','HU_HONG')),
    CONSTRAINT ck_chi_tiet_tinh_trang_sau CHECK (tinh_trang_sau IS NULL OR tinh_trang_sau IN ('MOI','NHU_MOI','TOT','KHA','CU','HU_HONG'))
);

CREATE TABLE IF NOT EXISTS nhat_ky_thue (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    phieu_thue_id BIGINT NOT NULL,
    trang_thai_cu VARCHAR(30),
    trang_thai_moi VARCHAR(30) NOT NULL,
    nguoi_thuc_hien_id BIGINT,
    thoi_gian TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ghi_chu VARCHAR(1000),
    CONSTRAINT fk_nhat_ky_phieu FOREIGN KEY (phieu_thue_id) REFERENCES phieu_thue(id),
    CONSTRAINT fk_nhat_ky_nguoi FOREIGN KEY (nguoi_thuc_hien_id) REFERENCES tai_khoan(id)
);

CREATE TABLE IF NOT EXISTS sach_cu (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    ban_sao_id BIGINT NOT NULL UNIQUE,
    gia_de_xuat DECIMAL(15,2) NOT NULL,
    gia_ban DECIMAL(15,2),
    tinh_trang VARCHAR(20) NOT NULL,
    mo_ta VARCHAR(2000),
    hinh_anh VARCHAR(500),
    trang_thai VARCHAR(30) NOT NULL DEFAULT 'CHO_DUYET',
    duyet_boi BIGINT,
    ngay_tao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ngay_duyet TIMESTAMP,
    CONSTRAINT fk_sach_cu_ban_sao FOREIGN KEY (ban_sao_id) REFERENCES ban_sao_sach(id),
    CONSTRAINT fk_sach_cu_duyet FOREIGN KEY (duyet_boi) REFERENCES tai_khoan(id),
    CONSTRAINT ck_sach_cu_gia CHECK (gia_de_xuat >= 0 AND (gia_ban IS NULL OR gia_ban >= 0)),
    CONSTRAINT ck_sach_cu_tinh_trang CHECK (tinh_trang IN ('NHU_MOI','TOT','KHA','CU')),
    CONSTRAINT ck_sach_cu_trang_thai CHECK (trang_thai IN ('CHO_DUYET','DANG_BAN','DA_BAN','NGUNG_BAN'))
);

CREATE TABLE IF NOT EXISTS chi_tiet_don_hang_sach_cu (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    don_hang_id BIGINT NOT NULL,
    sach_cu_id BIGINT NOT NULL,
    don_gia DECIMAL(15,2) NOT NULL,
    CONSTRAINT uq_don_hang_sach_cu UNIQUE (don_hang_id, sach_cu_id),
    CONSTRAINT fk_ct_don_sach_cu_don FOREIGN KEY (don_hang_id) REFERENCES don_hang(id),
    CONSTRAINT fk_ct_don_sach_cu_sach FOREIGN KEY (sach_cu_id) REFERENCES sach_cu(id),
    CONSTRAINT ck_ct_don_sach_cu_gia CHECK (don_gia >= 0)
);

CREATE INDEX IF NOT EXISTS idx_hoi_vien_trang_thai ON hoi_vien(trang_thai);
CREATE INDEX IF NOT EXISTS idx_thanh_toan_tham_chieu ON thanh_toan(tham_chieu_loai, tham_chieu_id);
CREATE INDEX IF NOT EXISTS idx_thanh_toan_trang_thai ON thanh_toan(trang_thai);
CREATE INDEX IF NOT EXISTS idx_ban_sao_sach_trang_thai ON ban_sao_sach(sach_id, trang_thai);
CREATE INDEX IF NOT EXISTS idx_phieu_thue_khach ON phieu_thue(khach_hang_id, ngay_tao);
CREATE INDEX IF NOT EXISTS idx_phieu_thue_trang_thai ON phieu_thue(trang_thai);
CREATE INDEX IF NOT EXISTS idx_chi_tiet_phieu_hen_tra ON chi_tiet_phieu_thue(ngay_hen_tra, ngay_tra_thuc_te);
CREATE INDEX IF NOT EXISTS idx_nhat_ky_phieu ON nhat_ky_thue(phieu_thue_id, thoi_gian);
CREATE INDEX IF NOT EXISTS idx_sach_cu_trang_thai ON sach_cu(trang_thai);
