-- Sách yêu thích theo tài khoản; không thêm dữ liệu giả.
CREATE TABLE IF NOT EXISTS sach_yeu_thich (
    tai_khoan_id BIGINT NOT NULL,
    sach_id BIGINT NOT NULL,
    ngay_luu TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (tai_khoan_id, sach_id),
    CONSTRAINT fk_yeu_thich_tai_khoan FOREIGN KEY (tai_khoan_id) REFERENCES tai_khoan(id) ON DELETE CASCADE,
    CONSTRAINT fk_yeu_thich_sach FOREIGN KEY (sach_id) REFERENCES sach(id) ON DELETE CASCADE
);
