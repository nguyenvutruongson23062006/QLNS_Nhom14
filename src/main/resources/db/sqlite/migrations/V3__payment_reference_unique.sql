-- Một giao dịch ngân hàng/biên nhận chỉ được dùng để xác nhận một khoản thanh toán.
CREATE UNIQUE INDEX IF NOT EXISTS uq_thanh_toan_ma_giao_dich
ON thanh_toan(ma_giao_dich_nha_cung_cap);
