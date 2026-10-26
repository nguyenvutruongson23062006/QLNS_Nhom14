CREATE TABLE IF NOT EXISTS ho_so_ca_nhan (
 tai_khoan_id BIGINT PRIMARY KEY,
 anh_dai_dien VARCHAR(500),
 the_loai_yeu_thich VARCHAR(500),
 CONSTRAINT fk_ho_so_tai_khoan FOREIGN KEY (tai_khoan_id) REFERENCES tai_khoan(id) ON DELETE CASCADE
);
