-- Dữ liệu mẫu không chứa tài khoản hoặc mật khẩu giả.
INSERT INTO vai_tro (id, ma_vai_tro, ten_vai_tro) VALUES
    (1, 'ADMIN', 'Quản trị viên'), (2, 'NHAN_VIEN', 'Nhân viên'), (3, 'KHACH_HANG', 'Khách hàng');
INSERT INTO danh_muc (id, ten_danh_muc, mo_ta) VALUES
    (1, 'Công nghệ thông tin', 'Sách lập trình và công nghệ'),
    (2, 'Văn học', 'Tiểu thuyết và truyện');
INSERT INTO nha_xuat_ban (id, ten_nha_xuat_ban) VALUES
    (1, 'Nhà xuất bản Giáo dục Việt Nam'), (2, 'Nhà xuất bản Trẻ');
INSERT INTO sach (ma_sach, ten_sach, tac_gia, danh_muc_id, nha_xuat_ban_id, nam_xuat_ban, gia_ban, so_luong_ton, mo_ta) VALUES
    ('S001', 'Java căn bản', 'Tác giả minh họa', 1, 1, 2026, 120000, 20, 'Dữ liệu minh họa để phát triển phần mềm'),
    ('S002', 'Tuyển tập truyện minh họa', 'Tác giả minh họa', 2, 2, 2026, 85000, 15, 'Dữ liệu minh họa, không phải thông tin xuất bản thực tế');

-- Dữ liệu mở rộng minh họa để thực hành, không phải thông tin xuất bản thực tế.
-- Tổng cộng 10 danh mục, 8 nhà xuất bản, 40 sách. Chạy một lần trên schema mới.
INSERT INTO danh_muc (id,ten_danh_muc,mo_ta) VALUES (3,'Kinh tế và kinh doanh','Sách thuộc nhóm Kinh tế và kinh doanh');
INSERT INTO danh_muc (id,ten_danh_muc,mo_ta) VALUES (4,'Kỹ năng sống','Sách thuộc nhóm Kỹ năng sống');
INSERT INTO danh_muc (id,ten_danh_muc,mo_ta) VALUES (5,'Thiếu nhi','Sách thuộc nhóm Thiếu nhi');
INSERT INTO danh_muc (id,ten_danh_muc,mo_ta) VALUES (6,'Ngoại ngữ','Sách thuộc nhóm Ngoại ngữ');
INSERT INTO danh_muc (id,ten_danh_muc,mo_ta) VALUES (7,'Khoa học','Sách thuộc nhóm Khoa học');
INSERT INTO danh_muc (id,ten_danh_muc,mo_ta) VALUES (8,'Lịch sử và địa lý','Sách thuộc nhóm Lịch sử và địa lý');
INSERT INTO danh_muc (id,ten_danh_muc,mo_ta) VALUES (9,'Giáo trình và tham khảo','Sách thuộc nhóm Giáo trình và tham khảo');
INSERT INTO danh_muc (id,ten_danh_muc,mo_ta) VALUES (10,'Sức khỏe và đời sống','Sách thuộc nhóm Sức khỏe và đời sống');
INSERT INTO nha_xuat_ban (id,ten_nha_xuat_ban) VALUES (3,'Nhà xuất bản Kim Đồng');
INSERT INTO nha_xuat_ban (id,ten_nha_xuat_ban) VALUES (4,'Nhà xuất bản Tổng hợp Thành phố Hồ Chí Minh');
INSERT INTO nha_xuat_ban (id,ten_nha_xuat_ban) VALUES (5,'Nhà xuất bản Lao Động');
INSERT INTO nha_xuat_ban (id,ten_nha_xuat_ban) VALUES (6,'Nhà xuất bản Đại học Quốc gia Hà Nội');
INSERT INTO nha_xuat_ban (id,ten_nha_xuat_ban) VALUES (7,'Nhà xuất bản Khoa học và Kỹ thuật');
INSERT INTO nha_xuat_ban (id,ten_nha_xuat_ban) VALUES (8,'Nhà xuất bản Văn học');
INSERT INTO sach (ma_sach,ten_sach,tac_gia,danh_muc_id,nha_xuat_ban_id,nam_xuat_ban,gia_ban,so_luong_ton,mo_ta) VALUES
('S003','Lập trình hướng đối tượng Java','Nhóm biên soạn minh họa 1',1,3,2026,106000,31,'Dữ liệu học tập; tên sách, tác giả, giá và liên kết NXB chỉ để minh họa'),
('S004','Cơ sở dữ liệu thực hành','Nhóm biên soạn minh họa 1',1,4,2023,123000,38,'Dữ liệu học tập; tên sách, tác giả, giá và liên kết NXB chỉ để minh họa'),
('S005','Phát triển website cơ bản','Nhóm biên soạn minh họa 1',1,5,2024,140000,45,'Dữ liệu học tập; tên sách, tác giả, giá và liên kết NXB chỉ để minh họa'),
('S006','Những câu chuyện bên hiên nhà','Nhóm biên soạn minh họa 2',2,6,2025,157000,52,'Dữ liệu học tập; tên sách, tác giả, giá và liên kết NXB chỉ để minh họa'),
('S007','Hành trình qua mùa hạ','Nhóm biên soạn minh họa 2',2,7,2026,174000,59,'Dữ liệu học tập; tên sách, tác giả, giá và liên kết NXB chỉ để minh họa'),
('S008','Góc phố và những người bạn','Nhóm biên soạn minh họa 2',2,8,2023,191000,66,'Dữ liệu học tập; tên sách, tác giả, giá và liên kết NXB chỉ để minh họa'),
('S009','Nhập môn quản trị kinh doanh','Nhóm biên soạn minh họa 3',3,1,2024,208000,73,'Dữ liệu học tập; tên sách, tác giả, giá và liên kết NXB chỉ để minh họa'),
('S010','Tài chính cá nhân căn bản','Nhóm biên soạn minh họa 3',3,2,2025,65000,0,'Dữ liệu học tập; tên sách, tác giả, giá và liên kết NXB chỉ để minh họa'),
('S011','Khởi nghiệp từ ý tưởng nhỏ','Nhóm biên soạn minh họa 3',3,3,2026,82000,17,'Dữ liệu học tập; tên sách, tác giả, giá và liên kết NXB chỉ để minh họa'),
('S012','Marketing cho cửa hàng','Nhóm biên soạn minh họa 3',3,4,2023,99000,24,'Dữ liệu học tập; tên sách, tác giả, giá và liên kết NXB chỉ để minh họa'),
('S013','Kỹ năng quản lý thời gian','Nhóm biên soạn minh họa 4',4,5,2024,116000,31,'Dữ liệu học tập; tên sách, tác giả, giá và liên kết NXB chỉ để minh họa'),
('S014','Giao tiếp trong công việc','Nhóm biên soạn minh họa 4',4,6,2025,133000,38,'Dữ liệu học tập; tên sách, tác giả, giá và liên kết NXB chỉ để minh họa'),
('S015','Xây dựng thói quen học tập','Nhóm biên soạn minh họa 4',4,7,2026,150000,45,'Dữ liệu học tập; tên sách, tác giả, giá và liên kết NXB chỉ để minh họa'),
('S016','Làm việc nhóm hiệu quả','Nhóm biên soạn minh họa 4',4,8,2023,167000,52,'Dữ liệu học tập; tên sách, tác giả, giá và liên kết NXB chỉ để minh họa'),
('S017','Khám phá khu vườn nhỏ','Nhóm biên soạn minh họa 5',5,1,2024,184000,59,'Dữ liệu học tập; tên sách, tác giả, giá và liên kết NXB chỉ để minh họa'),
('S018','Những người bạn trong rừng','Nhóm biên soạn minh họa 5',5,2,2025,201000,66,'Dữ liệu học tập; tên sách, tác giả, giá và liên kết NXB chỉ để minh họa'),
('S019','Bé học các con số','Nhóm biên soạn minh họa 5',5,3,2026,58000,73,'Dữ liệu học tập; tên sách, tác giả, giá và liên kết NXB chỉ để minh họa'),
('S020','Câu chuyện trước giờ ngủ','Nhóm biên soạn minh họa 5',5,4,2023,75000,10,'Dữ liệu học tập; tên sách, tác giả, giá và liên kết NXB chỉ để minh họa'),
('S021','Tiếng Anh giao tiếp cơ bản','Nhóm biên soạn minh họa 6',6,5,2024,92000,17,'Dữ liệu học tập; tên sách, tác giả, giá và liên kết NXB chỉ để minh họa'),
('S022','Ngữ pháp tiếng Anh thực hành','Nhóm biên soạn minh họa 6',6,6,2025,109000,24,'Dữ liệu học tập; tên sách, tác giả, giá và liên kết NXB chỉ để minh họa'),
('S023','Từ vựng tiếng Anh theo chủ đề','Nhóm biên soạn minh họa 6',6,7,2026,126000,31,'Dữ liệu học tập; tên sách, tác giả, giá và liên kết NXB chỉ để minh họa'),
('S024','Luyện đọc tiếng Anh mỗi ngày','Nhóm biên soạn minh họa 6',6,8,2023,143000,38,'Dữ liệu học tập; tên sách, tác giả, giá và liên kết NXB chỉ để minh họa'),
('S025','Khám phá hệ Mặt Trời','Nhóm biên soạn minh họa 7',7,1,2024,160000,45,'Dữ liệu học tập; tên sách, tác giả, giá và liên kết NXB chỉ để minh họa'),
('S026','Thế giới động vật quanh ta','Nhóm biên soạn minh họa 7',7,2,2025,177000,52,'Dữ liệu học tập; tên sách, tác giả, giá và liên kết NXB chỉ để minh họa'),
('S027','Vật lý trong cuộc sống','Nhóm biên soạn minh họa 7',7,3,2026,194000,59,'Dữ liệu học tập; tên sách, tác giả, giá và liên kết NXB chỉ để minh họa'),
('S028','Hóa học qua thí nghiệm nhỏ','Nhóm biên soạn minh họa 7',7,4,2023,211000,66,'Dữ liệu học tập; tên sách, tác giả, giá và liên kết NXB chỉ để minh họa'),
('S029','Dòng chảy lịch sử Việt Nam','Nhóm biên soạn minh họa 8',8,5,2024,68000,73,'Dữ liệu học tập; tên sách, tác giả, giá và liên kết NXB chỉ để minh họa'),
('S030','Địa lý Việt Nam nhập môn','Nhóm biên soạn minh họa 8',8,6,2025,85000,10,'Dữ liệu học tập; tên sách, tác giả, giá và liên kết NXB chỉ để minh họa'),
('S031','Khám phá các nền văn minh','Nhóm biên soạn minh họa 8',8,7,2026,102000,17,'Dữ liệu học tập; tên sách, tác giả, giá và liên kết NXB chỉ để minh họa'),
('S032','Bản đồ và hành trình khám phá','Nhóm biên soạn minh họa 8',8,8,2023,119000,24,'Dữ liệu học tập; tên sách, tác giả, giá và liên kết NXB chỉ để minh họa'),
('S033','Toán học cơ sở','Nhóm biên soạn minh họa 9',9,1,2024,136000,31,'Dữ liệu học tập; tên sách, tác giả, giá và liên kết NXB chỉ để minh họa'),
('S034','Nhập môn xác suất thống kê','Nhóm biên soạn minh họa 9',9,2,2025,153000,38,'Dữ liệu học tập; tên sách, tác giả, giá và liên kết NXB chỉ để minh họa'),
('S035','Phương pháp nghiên cứu khoa học','Nhóm biên soạn minh họa 9',9,3,2026,170000,45,'Dữ liệu học tập; tên sách, tác giả, giá và liên kết NXB chỉ để minh họa'),
('S036','Tin học văn phòng thực hành','Nhóm biên soạn minh họa 9',9,4,2023,187000,52,'Dữ liệu học tập; tên sách, tác giả, giá và liên kết NXB chỉ để minh họa'),
('S037','Dinh dưỡng trong bữa ăn hằng ngày','Nhóm biên soạn minh họa 10',10,5,2024,204000,59,'Dữ liệu học tập; tên sách, tác giả, giá và liên kết NXB chỉ để minh họa'),
('S038','Vận động và cuộc sống','Nhóm biên soạn minh họa 10',10,6,2025,61000,66,'Dữ liệu học tập; tên sách, tác giả, giá và liên kết NXB chỉ để minh họa'),
('S039','Chăm sóc giấc ngủ','Nhóm biên soạn minh họa 10',10,7,2026,78000,73,'Dữ liệu học tập; tên sách, tác giả, giá và liên kết NXB chỉ để minh họa'),
('S040','Tổ chức không gian sống','Nhóm biên soạn minh họa 10',10,8,2023,95000,10,'Dữ liệu học tập; tên sách, tác giả, giá và liên kết NXB chỉ để minh họa');

