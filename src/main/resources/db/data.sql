-- Dữ liệu khởi tạo dùng thông tin thư mục của các ấn bản có thật.
-- Metadata và ảnh bìa được đối chiếu theo ISBN qua Open Library ngày 2026-10-30.
-- Giá bán và tồn kho là dữ liệu vận hành của cửa hàng mẫu, không phải giá niêm yết của nhà xuất bản.
INSERT INTO vai_tro (id, ma_vai_tro, ten_vai_tro) VALUES
    (1, 'ADMIN', 'Quản trị viên'),
    (2, 'NHAN_VIEN', 'Nhân viên'),
    (3, 'KHACH_HANG', 'Khách hàng');

INSERT INTO danh_muc (id, ten_danh_muc, mo_ta) VALUES
    (1, 'Công nghệ thông tin', 'Lập trình, kiến trúc phần mềm và hệ thống dữ liệu'),
    (2, 'Văn học', 'Tiểu thuyết và tác phẩm văn học kinh điển, đương đại'),
    (3, 'Kinh tế và kinh doanh', 'Khởi nghiệp, quản trị và tư duy kinh doanh'),
    (4, 'Kỹ năng sống', 'Thói quen, giao tiếp và hiệu suất cá nhân'),
    (5, 'Thiếu nhi', 'Văn học và truyện đọc dành cho thiếu nhi'),
    (6, 'Ngoại ngữ', 'Ngữ pháp, từ vựng và từ điển tiếng Anh'),
    (7, 'Khoa học', 'Vũ trụ, sinh học và lịch sử khoa học'),
    (8, 'Lịch sử và địa lý', 'Lịch sử thế giới, văn minh và địa chính trị'),
    (9, 'Giáo trình và tham khảo', 'Giáo trình đại học và tài liệu tham khảo chuyên ngành'),
    (10, 'Sức khỏe và đời sống', 'Giấc ngủ, sức khỏe tinh thần, tuổi thọ và tổ chức cuộc sống');

INSERT INTO nha_xuat_ban (id, ten_nha_xuat_ban) VALUES
    (1, 'Prentice Hall'),
    (2, 'Addison-Wesley Professional'),
    (3, 'Pragmatic Bookshelf'),
    (4, 'O''Reilly Media'),
    (5, 'HMH Books for Young Readers'),
    (6, 'Signet Classics'),
    (7, 'Vintage International'),
    (8, 'Harper Perennial Modern Classics'),
    (9, 'Crown Business'),
    (10, 'Crown Publishing Group'),
    (11, 'Farrar, Straus and Giroux'),
    (12, 'HarperBusiness'),
    (13, 'Avery'),
    (14, 'Grand Central Publishing'),
    (15, 'Pocket Books'),
    (16, 'Simon & Schuster'),
    (17, 'Scholastic'),
    (18, 'Puffin'),
    (19, 'HarperCollins'),
    (20, 'Cambridge University Press'),
    (21, 'Oxford University Press'),
    (22, 'Bantam'),
    (23, 'Ballantine Books'),
    (24, 'Scribner'),
    (25, 'Harper'),
    (26, 'W. W. Norton & Company'),
    (27, 'MIT Press'),
    (28, 'Cengage Learning'),
    (29, 'Pearson'),
    (30, 'Springer'),
    (31, 'Penguin Books'),
    (32, 'Harmony'),
    (33, 'Ten Speed Press');

INSERT INTO sach
    (ma_sach, ten_sach, tac_gia, isbn, danh_muc_id, nha_xuat_ban_id, nam_xuat_ban, gia_ban, so_luong_ton, mo_ta, hinh_anh)
VALUES
    ('S001', 'Clean Code', 'Robert C. Martin', '9780132350884', 1, 1, 2008, 329000, 18, 'Các nguyên tắc và ví dụ thực hành giúp lập trình viên viết mã rõ ràng, dễ bảo trì và dễ kiểm thử.', '/images/books/s001.jpg'),
    ('S002', 'Effective Java', 'Joshua Bloch', '9780134685991', 1, 2, 2017, 389000, 14, 'Tập hợp các thực hành thiết kế và lập trình Java hiệu quả, trình bày theo từng mục ngắn gọn.', '/images/books/s002.jpg'),
    ('S003', 'The Pragmatic Programmer', 'David Thomas; Andrew Hunt', '9780135957059', 1, 3, 2019, 419000, 12, 'Những kinh nghiệm nghề nghiệp cốt lõi để xây dựng phần mềm tốt và phát triển tư duy của một lập trình viên thực dụng.', '/images/books/s003.jpg'),
    ('S004', 'Designing Data-Intensive Applications', 'Martin Kleppmann', '9781449373320', 1, 4, 2017, 529000, 9, 'Giải thích nền tảng của hệ thống dữ liệu tin cậy, có khả năng mở rộng và dễ bảo trì.', '/images/books/s004.jpg'),
    ('S005', 'The Little Prince', 'Antoine de Saint-Exupéry', '9780156012195', 2, 5, 2000, 159000, 24, 'Câu chuyện giàu tính biểu tượng về tình bạn, trách nhiệm và cách người lớn nhìn thế giới.', '/images/books/s005.jpg'),
    ('S006', '1984', 'George Orwell', '9780451524935', 2, 6, 1977, 179000, 20, 'Tiểu thuyết phản địa đàng về giám sát, tuyên truyền và quyền lực toàn trị.', '/images/books/s006.jpg'),
    ('S007', 'Norwegian Wood', 'Haruki Murakami', '9780375704024', 2, 7, 2000, 219000, 16, 'Tiểu thuyết trưởng thành xoay quanh ký ức, tình yêu và mất mát của những người trẻ tại Nhật Bản.', '/images/books/s007.jpg'),
    ('S008', 'To Kill a Mockingbird', 'Harper Lee', '9780061120084', 2, 8, 2006, 189000, 17, 'Tác phẩm văn học về công lý, định kiến và lòng trắc ẩn qua góc nhìn của một đứa trẻ.', '/images/books/s008.jpg'),
    ('S009', 'The Lean Startup', 'Eric Ries', '9780307887894', 3, 9, 2011, 249000, 15, 'Phương pháp xây dựng doanh nghiệp bằng thử nghiệm, học hỏi có kiểm chứng và cải tiến liên tục.', '/images/books/s009.jpg'),
    ('S010', 'Zero to One', 'Peter Thiel; Blake Masters', '9780804139298', 3, 10, 2014, 229000, 13, 'Các quan sát về đổi mới, lợi thế độc quyền và cách xây dựng doanh nghiệp tạo ra giá trị mới.', '/images/books/s010.jpg'),
    ('S011', 'Thinking, Fast and Slow', 'Daniel Kahneman', '9780374533557', 3, 11, 2013, 279000, 11, 'Khám phá hai hệ thống tư duy và những thiên kiến ảnh hưởng đến quyết định của con người.', '/images/books/s011.jpg'),
    ('S012', 'Good to Great', 'Jim Collins', '9780066620992', 3, 12, 2001, 259000, 10, 'Nghiên cứu các đặc điểm giúp một số công ty chuyển từ hoạt động tốt sang kết quả vượt trội.', '/images/books/s012.jpg'),
    ('S013', 'Atomic Habits', 'James Clear', '9780735211292', 4, 13, 2018, 239000, 28, 'Hướng dẫn xây dựng thói quen tốt và loại bỏ thói quen xấu thông qua những thay đổi nhỏ, có hệ thống.', '/images/books/s013.jpg'),
    ('S014', 'Deep Work', 'Cal Newport', '9781455586691', 4, 14, 2016, 229000, 18, 'Trình bày giá trị của khả năng tập trung sâu và cách tổ chức công việc trong môi trường nhiều xao nhãng.', '/images/books/s014.jpg'),
    ('S015', 'How to Win Friends and Influence People', 'Dale Carnegie', '9780671027032', 4, 15, 1998, 199000, 21, 'Những nguyên tắc giao tiếp, hợp tác và xây dựng quan hệ tích cực trong đời sống và công việc.', '/images/books/s015.jpg'),
    ('S016', 'The 7 Habits of Highly Effective People', 'Stephen R. Covey', '9781982137274', 4, 16, 2020, 279000, 14, 'Khung phát triển cá nhân dựa trên tính chủ động, mục tiêu, ưu tiên và hợp tác hiệu quả.', '/images/books/s016.jpg'),
    ('S017', 'Harry Potter and the Sorcerer''s Stone', 'J. K. Rowling', '9780590353427', 5, 17, 1999, 209000, 26, 'Tập mở đầu hành trình của Harry Potter tại trường Hogwarts và thế giới phù thủy.', '/images/books/s017.jpg'),
    ('S018', 'Matilda', 'Roald Dahl', '9780142410370', 5, 18, 2013, 169000, 19, 'Câu chuyện về cô bé thông minh, ham đọc sách và hành trình tự bảo vệ mình trước những người lớn độc đoán.', '/images/books/s018.jpg'),
    ('S019', 'Charlotte''s Web', 'E. B. White', '9780064400558', 5, 19, 1980, 149000, 22, 'Câu chuyện cảm động về tình bạn giữa chú heo Wilbur và cô nhện Charlotte.', '/images/books/s019.jpg'),
    ('S020', 'The Gruffalo', 'Julia Donaldson', '9780142403877', 5, 18, 2006, 139000, 20, 'Truyện tranh vần điệu về một chú chuột nhỏ dùng trí thông minh để vượt qua các mối nguy trong rừng.', '/images/books/s020.jpg'),
    ('S021', 'English Grammar in Use', 'Raymond Murphy', '9781108457651', 6, 20, 2019, 349000, 16, 'Giáo trình tự học ngữ pháp tiếng Anh trình độ trung cấp với bài giải thích và bài tập thực hành.', '/images/books/s021.jpg'),
    ('S022', 'Essential Grammar in Use', 'Raymond Murphy', '9781107480551', 6, 20, 2015, 319000, 15, 'Tài liệu tự học ngữ pháp tiếng Anh dành cho người học ở trình độ sơ cấp.', '/images/books/s022.jpg'),
    ('S023', 'Oxford Word Skills Intermediate', 'Ruth Gairns; Stuart Redman', '9780194620079', 6, 21, 2009, 289000, 13, 'Giáo trình phát triển vốn từ tiếng Anh trình độ trung cấp theo chủ đề và ngữ cảnh sử dụng.', '/images/books/s023.jpg'),
    ('S024', 'Oxford Advanced Learner''s Dictionary', 'Oxford University Press', '9780194798488', 6, 21, 2020, 599000, 8, 'Từ điển tiếng Anh dành cho người học nâng cao với định nghĩa, ví dụ và hướng dẫn sử dụng từ.', '/images/books/s024.jpg'),
    ('S025', 'A Brief History of Time', 'Stephen Hawking', '9780553380163', 7, 22, 1998, 219000, 18, 'Giới thiệu những câu hỏi lớn về vũ trụ, thời gian, hố đen và nguồn gốc của không gian.', '/images/books/s025.jpg'),
    ('S026', 'Cosmos', 'Carl Sagan', '9780345539434', 7, 23, 2013, 249000, 14, 'Hành trình qua lịch sử khám phá vũ trụ và mối liên hệ giữa khoa học với nền văn minh nhân loại.', '/images/books/s026.jpg'),
    ('S027', 'The Selfish Gene', 'Richard Dawkins', '9780198788607', 7, 21, 2016, 289000, 12, 'Trình bày cách nhìn tiến hóa lấy gene làm trung tâm và những hệ quả đối với hành vi sinh vật.', '/images/books/s027.jpg'),
    ('S028', 'The Gene', 'Siddhartha Mukherjee', '9781476733524', 7, 24, 2017, 279000, 10, 'Lịch sử khoa học của di truyền học cùng các câu hỏi y học và đạo đức liên quan đến gene.', '/images/books/s028.jpg'),
    ('S029', 'Sapiens', 'Yuval Noah Harari', '9780062316097', 8, 25, 2015, 279000, 23, 'Khái quát lịch sử loài người từ những cộng đồng săn bắt hái lượm đến xã hội hiện đại.', '/images/books/s029.jpg'),
    ('S030', 'Guns, Germs, and Steel', 'Jared Diamond', '9780393354324', 8, 26, 2017, 299000, 12, 'Phân tích ảnh hưởng của địa lý, cây trồng, vật nuôi và công nghệ đối với sự phát triển của các xã hội.', '/images/books/s030.jpg'),
    ('S031', 'The Silk Roads', 'Peter Frankopan', '9781101912379', 8, 7, 2017, 269000, 11, 'Kể lại lịch sử thế giới qua các tuyến giao thương và mạng lưới kết nối Á - Âu.', '/images/books/s031.jpg'),
    ('S032', 'Prisoners of Geography', 'Tim Marshall', '9781501121470', 8, 24, 2016, 229000, 17, 'Giải thích cách địa hình và vị trí địa lý tác động đến chiến lược của các quốc gia.', '/images/books/s032.jpg'),
    ('S033', 'Introduction to Algorithms', 'Thomas H. Cormen; Charles E. Leiserson; Ronald L. Rivest; Clifford Stein', '9780262046305', 9, 27, 2022, 799000, 7, 'Giáo trình toàn diện về thiết kế, phân tích thuật toán và các cấu trúc dữ liệu nền tảng.', '/images/books/s033.jpg'),
    ('S034', 'Calculus', 'James Stewart', '9781285741550', 9, 28, 2016, 689000, 6, 'Giáo trình giải tích với lý thuyết, ví dụ và hệ thống bài tập dành cho bậc đại học.', '/images/books/s034.jpg'),
    ('S035', 'Campbell Biology', 'Lisa A. Urry và cộng sự', '9780134093413', 9, 29, 2017, 879000, 5, 'Giáo trình sinh học đại cương bao quát tế bào, di truyền, tiến hóa, đa dạng sinh học và sinh thái.', '/images/books/s035.jpg'),
    ('S036', 'The Elements of Statistical Learning', 'Trevor Hastie; Robert Tibshirani; Jerome Friedman', '9780387848570', 9, 30, 2009, 749000, 6, 'Tài liệu tham khảo chuyên sâu về mô hình thống kê, học máy và khai phá dữ liệu.', '/images/books/s036.jpg'),
    ('S037', 'Why We Sleep', 'Matthew Walker', '9781501144318', 10, 24, 2017, 239000, 18, 'Tổng hợp nghiên cứu về vai trò của giấc ngủ đối với trí nhớ, sức khỏe và hiệu suất.', '/images/books/s037.jpg'),
    ('S038', 'The Body Keeps the Score', 'Bessel van der Kolk', '9780143127741', 10, 31, 2015, 269000, 12, 'Khảo sát tác động của sang chấn lên cơ thể và tâm trí cùng các hướng tiếp cận phục hồi.', '/images/books/s038.jpg'),
    ('S039', 'Outlive', 'Peter Attia; Bill Gifford', '9780593236598', 10, 32, 2023, 319000, 15, 'Trình bày cách tiếp cận chủ động đối với sức khỏe lâu dài, phòng ngừa bệnh mạn tính và nâng cao chất lượng sống.', '/images/books/s039.jpg'),
    ('S040', 'The Life-Changing Magic of Tidying Up', 'Marie Kondo', '9781607747307', 10, 33, 2014, 199000, 20, 'Phương pháp sắp xếp không gian sống dựa trên việc giữ lại những đồ vật thực sự có ý nghĩa.', '/images/books/s040.jpg');


-- Cấu hình cho thuê 10 đầu sách (gia_thue_ngay, tien_coc, so_ngay_mac_dinh, so_lan_gia_han, phi_tre_ngay)
INSERT INTO cau_hinh_thue_sach (sach_id, cho_thue, gia_thue_ngay, tien_coc, so_ngay_mac_dinh, so_lan_gia_han, phi_tre_ngay)
SELECT id, TRUE,  5000, 30000, 14, 2, 3000 FROM sach WHERE ma_sach = 'S001'; -- Clean Code
INSERT INTO cau_hinh_thue_sach (sach_id, cho_thue, gia_thue_ngay, tien_coc, so_ngay_mac_dinh, so_lan_gia_han, phi_tre_ngay)
SELECT id, TRUE,  5000, 30000, 14, 2, 3000 FROM sach WHERE ma_sach = 'S002'; -- Effective Java
INSERT INTO cau_hinh_thue_sach (sach_id, cho_thue, gia_thue_ngay, tien_coc, so_ngay_mac_dinh, so_lan_gia_han, phi_tre_ngay)
SELECT id, TRUE,  3000, 20000, 14, 2, 2000 FROM sach WHERE ma_sach = 'S005'; -- The Little Prince
INSERT INTO cau_hinh_thue_sach (sach_id, cho_thue, gia_thue_ngay, tien_coc, so_ngay_mac_dinh, so_lan_gia_han, phi_tre_ngay)
SELECT id, TRUE,  3000, 20000, 14, 2, 2000 FROM sach WHERE ma_sach = 'S006'; -- 1984
INSERT INTO cau_hinh_thue_sach (sach_id, cho_thue, gia_thue_ngay, tien_coc, so_ngay_mac_dinh, so_lan_gia_han, phi_tre_ngay)
SELECT id, TRUE,  3500, 20000, 14, 2, 2000 FROM sach WHERE ma_sach = 'S007'; -- Norwegian Wood
INSERT INTO cau_hinh_thue_sach (sach_id, cho_thue, gia_thue_ngay, tien_coc, so_ngay_mac_dinh, so_lan_gia_han, phi_tre_ngay)
SELECT id, TRUE,  4000, 25000, 14, 2, 3000 FROM sach WHERE ma_sach = 'S009'; -- The Lean Startup
INSERT INTO cau_hinh_thue_sach (sach_id, cho_thue, gia_thue_ngay, tien_coc, so_ngay_mac_dinh, so_lan_gia_han, phi_tre_ngay)
SELECT id, TRUE,  4000, 25000, 14, 2, 3000 FROM sach WHERE ma_sach = 'S013'; -- Atomic Habits
INSERT INTO cau_hinh_thue_sach (sach_id, cho_thue, gia_thue_ngay, tien_coc, so_ngay_mac_dinh, so_lan_gia_han, phi_tre_ngay)
SELECT id, TRUE,  3500, 20000, 14, 2, 2000 FROM sach WHERE ma_sach = 'S017'; -- Harry Potter
INSERT INTO cau_hinh_thue_sach (sach_id, cho_thue, gia_thue_ngay, tien_coc, so_ngay_mac_dinh, so_lan_gia_han, phi_tre_ngay)
SELECT id, TRUE,  4000, 25000, 14, 2, 3000 FROM sach WHERE ma_sach = 'S025'; -- A Brief History of Time
INSERT INTO cau_hinh_thue_sach (sach_id, cho_thue, gia_thue_ngay, tien_coc, so_ngay_mac_dinh, so_lan_gia_han, phi_tre_ngay)
SELECT id, TRUE,  4000, 25000, 14, 2, 3000 FROM sach WHERE ma_sach = 'S029'; -- Sapiens

-- Bản sao vật lý cho mỗi đầu sách (2-3 bản/cuốn)
INSERT INTO ban_sao_sach (ma_ban_sao, sach_id, tinh_trang, trang_thai) SELECT 'BS-S001-01', id, 'MOI',     'SAN_SANG' FROM sach WHERE ma_sach='S001';
INSERT INTO ban_sao_sach (ma_ban_sao, sach_id, tinh_trang, trang_thai) SELECT 'BS-S001-02', id, 'NHU_MOI', 'SAN_SANG' FROM sach WHERE ma_sach='S001';
INSERT INTO ban_sao_sach (ma_ban_sao, sach_id, tinh_trang, trang_thai) SELECT 'BS-S002-01', id, 'MOI',     'SAN_SANG' FROM sach WHERE ma_sach='S002';
INSERT INTO ban_sao_sach (ma_ban_sao, sach_id, tinh_trang, trang_thai) SELECT 'BS-S002-02', id, 'TOT',     'SAN_SANG' FROM sach WHERE ma_sach='S002';
INSERT INTO ban_sao_sach (ma_ban_sao, sach_id, tinh_trang, trang_thai) SELECT 'BS-S005-01', id, 'MOI',     'SAN_SANG' FROM sach WHERE ma_sach='S005';
INSERT INTO ban_sao_sach (ma_ban_sao, sach_id, tinh_trang, trang_thai) SELECT 'BS-S005-02', id, 'NHU_MOI', 'SAN_SANG' FROM sach WHERE ma_sach='S005';
INSERT INTO ban_sao_sach (ma_ban_sao, sach_id, tinh_trang, trang_thai) SELECT 'BS-S005-03', id, 'TOT',     'SAN_SANG' FROM sach WHERE ma_sach='S005';
INSERT INTO ban_sao_sach (ma_ban_sao, sach_id, tinh_trang, trang_thai) SELECT 'BS-S006-01', id, 'MOI',     'SAN_SANG' FROM sach WHERE ma_sach='S006';
INSERT INTO ban_sao_sach (ma_ban_sao, sach_id, tinh_trang, trang_thai) SELECT 'BS-S006-02', id, 'TOT',     'SAN_SANG' FROM sach WHERE ma_sach='S006';
INSERT INTO ban_sao_sach (ma_ban_sao, sach_id, tinh_trang, trang_thai) SELECT 'BS-S007-01', id, 'MOI',     'SAN_SANG' FROM sach WHERE ma_sach='S007';
INSERT INTO ban_sao_sach (ma_ban_sao, sach_id, tinh_trang, trang_thai) SELECT 'BS-S007-02', id, 'NHU_MOI', 'SAN_SANG' FROM sach WHERE ma_sach='S007';
INSERT INTO ban_sao_sach (ma_ban_sao, sach_id, tinh_trang, trang_thai) SELECT 'BS-S009-01', id, 'MOI',     'SAN_SANG' FROM sach WHERE ma_sach='S009';
INSERT INTO ban_sao_sach (ma_ban_sao, sach_id, tinh_trang, trang_thai) SELECT 'BS-S009-02', id, 'TOT',     'SAN_SANG' FROM sach WHERE ma_sach='S009';
INSERT INTO ban_sao_sach (ma_ban_sao, sach_id, tinh_trang, trang_thai) SELECT 'BS-S013-01', id, 'MOI',     'SAN_SANG' FROM sach WHERE ma_sach='S013';
INSERT INTO ban_sao_sach (ma_ban_sao, sach_id, tinh_trang, trang_thai) SELECT 'BS-S013-02', id, 'NHU_MOI', 'SAN_SANG' FROM sach WHERE ma_sach='S013';
INSERT INTO ban_sao_sach (ma_ban_sao, sach_id, tinh_trang, trang_thai) SELECT 'BS-S013-03', id, 'MOI',     'SAN_SANG' FROM sach WHERE ma_sach='S013';
INSERT INTO ban_sao_sach (ma_ban_sao, sach_id, tinh_trang, trang_thai) SELECT 'BS-S017-01', id, 'MOI',     'SAN_SANG' FROM sach WHERE ma_sach='S017';
INSERT INTO ban_sao_sach (ma_ban_sao, sach_id, tinh_trang, trang_thai) SELECT 'BS-S017-02', id, 'NHU_MOI', 'SAN_SANG' FROM sach WHERE ma_sach='S017';
INSERT INTO ban_sao_sach (ma_ban_sao, sach_id, tinh_trang, trang_thai) SELECT 'BS-S017-03', id, 'TOT',     'SAN_SANG' FROM sach WHERE ma_sach='S017';
INSERT INTO ban_sao_sach (ma_ban_sao, sach_id, tinh_trang, trang_thai) SELECT 'BS-S025-01', id, 'MOI',     'SAN_SANG' FROM sach WHERE ma_sach='S025';
INSERT INTO ban_sao_sach (ma_ban_sao, sach_id, tinh_trang, trang_thai) SELECT 'BS-S025-02', id, 'TOT',     'SAN_SANG' FROM sach WHERE ma_sach='S025';
INSERT INTO ban_sao_sach (ma_ban_sao, sach_id, tinh_trang, trang_thai) SELECT 'BS-S029-01', id, 'MOI',     'SAN_SANG' FROM sach WHERE ma_sach='S029';
INSERT INTO ban_sao_sach (ma_ban_sao, sach_id, tinh_trang, trang_thai) SELECT 'BS-S029-02', id, 'NHU_MOI', 'SAN_SANG' FROM sach WHERE ma_sach='S029';
INSERT INTO ban_sao_sach (ma_ban_sao, sach_id, tinh_trang, trang_thai) SELECT 'BS-S029-03', id, 'TOT',     'SAN_SANG' FROM sach WHERE ma_sach='S029';


-- ==========================================
-- DỮ LIỆU SÁCH CŨ (USED BOOKS)
-- Tình trạng ngẫu nhiên đa dạng: NHU_MOI, TOT, KHA, CU
-- ==========================================

-- 1. Thêm các bản sao sách chuyển sang bán sách cũ
INSERT INTO ban_sao_sach (ma_ban_sao, sach_id, tinh_trang, trang_thai) SELECT 'BS-CU-S003', id, 'NHU_MOI', 'CHO_BAN_CU' FROM sach WHERE ma_sach = 'S003';
INSERT INTO ban_sao_sach (ma_ban_sao, sach_id, tinh_trang, trang_thai) SELECT 'BS-CU-S004', id, 'TOT',     'CHO_BAN_CU' FROM sach WHERE ma_sach = 'S004';
INSERT INTO ban_sao_sach (ma_ban_sao, sach_id, tinh_trang, trang_thai) SELECT 'BS-CU-S007', id, 'KHA',     'CHO_BAN_CU' FROM sach WHERE ma_sach = 'S007';
INSERT INTO ban_sao_sach (ma_ban_sao, sach_id, tinh_trang, trang_thai) SELECT 'BS-CU-S008', id, 'CU',      'CHO_BAN_CU' FROM sach WHERE ma_sach = 'S008';
INSERT INTO ban_sao_sach (ma_ban_sao, sach_id, tinh_trang, trang_thai) SELECT 'BS-CU-S010', id, 'TOT',     'CHO_BAN_CU' FROM sach WHERE ma_sach = 'S010';
INSERT INTO ban_sao_sach (ma_ban_sao, sach_id, tinh_trang, trang_thai) SELECT 'BS-CU-S011', id, 'KHA',     'CHO_BAN_CU' FROM sach WHERE ma_sach = 'S011';
INSERT INTO ban_sao_sach (ma_ban_sao, sach_id, tinh_trang, trang_thai) SELECT 'BS-CU-S012', id, 'NHU_MOI', 'CHO_BAN_CU' FROM sach WHERE ma_sach = 'S012';
INSERT INTO ban_sao_sach (ma_ban_sao, sach_id, tinh_trang, trang_thai) SELECT 'BS-CU-S014', id, 'TOT',     'CHO_BAN_CU' FROM sach WHERE ma_sach = 'S014';
INSERT INTO ban_sao_sach (ma_ban_sao, sach_id, tinh_trang, trang_thai) SELECT 'BS-CU-S015', id, 'CU',      'CHO_BAN_CU' FROM sach WHERE ma_sach = 'S015';
INSERT INTO ban_sao_sach (ma_ban_sao, sach_id, tinh_trang, trang_thai) SELECT 'BS-CU-S016', id, 'NHU_MOI', 'CHO_BAN_CU' FROM sach WHERE ma_sach = 'S016';
INSERT INTO ban_sao_sach (ma_ban_sao, sach_id, tinh_trang, trang_thai) SELECT 'BS-CU-S018', id, 'TOT',     'CHO_BAN_CU' FROM sach WHERE ma_sach = 'S018';
INSERT INTO ban_sao_sach (ma_ban_sao, sach_id, tinh_trang, trang_thai) SELECT 'BS-CU-S019', id, 'KHA',     'CHO_BAN_CU' FROM sach WHERE ma_sach = 'S019';
INSERT INTO ban_sao_sach (ma_ban_sao, sach_id, tinh_trang, trang_thai) SELECT 'BS-CU-S026', id, 'NHU_MOI', 'CHO_BAN_CU' FROM sach WHERE ma_sach = 'S026';
INSERT INTO ban_sao_sach (ma_ban_sao, sach_id, tinh_trang, trang_thai) SELECT 'BS-CU-S030', id, 'TOT',     'CHO_BAN_CU' FROM sach WHERE ma_sach = 'S030';
INSERT INTO ban_sao_sach (ma_ban_sao, sach_id, tinh_trang, trang_thai) SELECT 'BS-CU-S031', id, 'KHA',     'CHO_BAN_CU' FROM sach WHERE ma_sach = 'S031';
INSERT INTO ban_sao_sach (ma_ban_sao, sach_id, tinh_trang, trang_thai) SELECT 'BS-CU-S033', id, 'TOT',     'CHO_BAN_CU' FROM sach WHERE ma_sach = 'S033';
INSERT INTO ban_sao_sach (ma_ban_sao, sach_id, tinh_trang, trang_thai) SELECT 'BS-CU-S037', id, 'CU',      'CHO_BAN_CU' FROM sach WHERE ma_sach = 'S037';
INSERT INTO ban_sao_sach (ma_ban_sao, sach_id, tinh_trang, trang_thai) SELECT 'BS-CU-S039', id, 'NHU_MOI', 'CHO_BAN_CU' FROM sach WHERE ma_sach = 'S039';

-- 2 đầu sách cũ chờ duyệt
INSERT INTO ban_sao_sach (ma_ban_sao, sach_id, tinh_trang, trang_thai) SELECT 'BS-CU-S001', id, 'TOT',     'CHO_BAN_CU' FROM sach WHERE ma_sach = 'S001';
INSERT INTO ban_sao_sach (ma_ban_sao, sach_id, tinh_trang, trang_thai) SELECT 'BS-CU-S002', id, 'KHA',     'CHO_BAN_CU' FROM sach WHERE ma_sach = 'S002';

-- 2. Thêm vào bảng sach_cu (18 cuốn đang mở bán + 2 cuốn chờ duyệt)
-- S003 - The Pragmatic Programmer
INSERT INTO sach_cu (ban_sao_id, gia_de_xuat, gia_ban, tinh_trang, mo_ta, trang_thai, duyet_boi, ngay_duyet)
SELECT b.id, 293000, 270000, 'NHU_MOI', 'Bản sao lưu hành nội bộ, giấy đẹp nguyên vẹn, không nếp gấp hay quăn góc.', 'DANG_BAN', NULL, CURRENT_TIMESTAMP
FROM ban_sao_sach b WHERE b.ma_ban_sao = 'BS-CU-S003';

-- S004 - Designing Data-Intensive Applications
INSERT INTO sach_cu (ban_sao_id, gia_de_xuat, gia_ban, tinh_trang, mo_ta, trang_thai, duyet_boi, ngay_duyet)
SELECT b.id, 290000, 280000, 'TOT', 'Bìa còn bóng, gáy sách chắc chắn, ruột sách sạch đẹp không ghi chú.', 'DANG_BAN', NULL, CURRENT_TIMESTAMP
FROM ban_sao_sach b WHERE b.ma_ban_sao = 'BS-CU-S004';

-- S007 - Norwegian Wood
INSERT INTO sach_cu (ban_sao_id, gia_de_xuat, gia_ban, tinh_trang, mo_ta, trang_thai, duyet_boi, ngay_duyet)
SELECT b.id, 88000, 95000, 'KHA', 'Giấy ngả màu thời gian nhẹ theo phong cách hoài niệm, chữ in sắc nét rõ ràng.', 'DANG_BAN', NULL, CURRENT_TIMESTAMP
FROM ban_sao_sach b WHERE b.ma_ban_sao = 'BS-CU-S007';

-- S008 - To Kill a Mockingbird
INSERT INTO sach_cu (ban_sao_id, gia_de_xuat, gia_ban, tinh_trang, mo_ta, trang_thai, duyet_boi, ngay_duyet)
SELECT b.id, 47000, 60000, 'CU', 'Bìa sờn nhẹ ở góc, trang ruột đầy đủ không rách, giá rất tốt để đọc lại.', 'DANG_BAN', NULL, CURRENT_TIMESTAMP
FROM ban_sao_sach b WHERE b.ma_ban_sao = 'BS-CU-S008';

-- S010 - Zero to One
INSERT INTO sach_cu (ban_sao_id, gia_de_xuat, gia_ban, tinh_trang, mo_ta, trang_thai, duyet_boi, ngay_duyet)
SELECT b.id, 126000, 125000, 'TOT', 'Sách được bảo quản tốt trong tủ kính, mép giấy thẳng, chỉ đọc qua 1 lần.', 'DANG_BAN', NULL, CURRENT_TIMESTAMP
FROM ban_sao_sach b WHERE b.ma_ban_sao = 'BS-CU-S010';

-- S011 - Thinking, Fast and Slow
INSERT INTO sach_cu (ban_sao_id, gia_de_xuat, gia_ban, tinh_trang, mo_ta, trang_thai, duyet_boi, ngay_duyet)
SELECT b.id, 112000, 120000, 'KHA', 'Có vài vết đánh dấu bút chì ở phần mở đầu, các chương sau sạch đẹp.', 'DANG_BAN', NULL, CURRENT_TIMESTAMP
FROM ban_sao_sach b WHERE b.ma_ban_sao = 'BS-CU-S011';

-- S012 - Good to Great
INSERT INTO sach_cu (ban_sao_id, gia_de_xuat, gia_ban, tinh_trang, mo_ta, trang_thai, duyet_boi, ngay_duyet)
SELECT b.id, 181000, 175000, 'NHU_MOI', 'Hình thức như mới 98%, gáy sách nguyên vẹn không cấn móp, mép giấy phẳng.', 'DANG_BAN', NULL, CURRENT_TIMESTAMP
FROM ban_sao_sach b WHERE b.ma_ban_sao = 'BS-CU-S012';

-- S014 - Deep Work
INSERT INTO sach_cu (ban_sao_id, gia_de_xuat, gia_ban, tinh_trang, mo_ta, trang_thai, duyet_boi, ngay_duyet)
SELECT b.id, 126000, 129000, 'TOT', 'Bìa đẹp, không quăn góc, chữ in rõ nét, giấy thơm không ẩm mốc.', 'DANG_BAN', NULL, CURRENT_TIMESTAMP
FROM ban_sao_sach b WHERE b.ma_ban_sao = 'BS-CU-S014';

-- S015 - How to Win Friends and Influence People
INSERT INTO sach_cu (ban_sao_id, gia_de_xuat, gia_ban, tinh_trang, mo_ta, trang_thai, duyet_boi, ngay_duyet)
SELECT b.id, 50000, 65000, 'CU', 'Ấn bản sưu tầm, giấy ngả vàng tự nhiên, gáy chắc chắn không rách trang.', 'DANG_BAN', NULL, CURRENT_TIMESTAMP
FROM ban_sao_sach b WHERE b.ma_ban_sao = 'BS-CU-S015';

-- S016 - The 7 Habits of Highly Effective People
INSERT INTO sach_cu (ban_sao_id, gia_de_xuat, gia_ban, tinh_trang, mo_ta, trang_thai, duyet_boi, ngay_duyet)
SELECT b.id, 195000, 190000, 'NHU_MOI', 'Tình trạng xuất sắc như mới, mép giấy thẳng tinh tươm, chưa từng viết vẽ.', 'DANG_BAN', NULL, CURRENT_TIMESTAMP
FROM ban_sao_sach b WHERE b.ma_ban_sao = 'BS-CU-S016';

-- S018 - Matilda
INSERT INTO sach_cu (ban_sao_id, gia_de_xuat, gia_ban, tinh_trang, mo_ta, trang_thai, duyet_boi, ngay_duyet)
SELECT b.id, 93000, 90000, 'TOT', 'Truyện bìa mềm đẹp, tranh minh họa sắc nét, các trang ruột sạch sẽ.', 'DANG_BAN', NULL, CURRENT_TIMESTAMP
FROM ban_sao_sach b WHERE b.ma_ban_sao = 'BS-CU-S018';

-- S019 - Charlotte's Web
INSERT INTO sach_cu (ban_sao_id, gia_de_xuat, gia_ban, tinh_trang, mo_ta, trang_thai, duyet_boi, ngay_duyet)
SELECT b.id, 60000, 65000, 'KHA', 'Gáy sách hơi mềm nhẹ do đã qua sử dụng, tranh minh họa cổ điển đầy đủ.', 'DANG_BAN', NULL, CURRENT_TIMESTAMP
FROM ban_sao_sach b WHERE b.ma_ban_sao = 'BS-CU-S019';

-- S026 - Cosmos
INSERT INTO sach_cu (ban_sao_id, gia_de_xuat, gia_ban, tinh_trang, mo_ta, trang_thai, duyet_boi, ngay_duyet)
SELECT b.id, 174000, 165000, 'NHU_MOI', 'Bản in đẹp, chất lượng giấy cao cấp, gáy vuông vức gần như nguyên bản.', 'DANG_BAN', NULL, CURRENT_TIMESTAMP
FROM ban_sao_sach b WHERE b.ma_ban_sao = 'BS-CU-S026';

-- S030 - Guns, Germs, and Steel
INSERT INTO sach_cu (ban_sao_id, gia_de_xuat, gia_ban, tinh_trang, mo_ta, trang_thai, duyet_boi, ngay_duyet)
SELECT b.id, 164000, 160000, 'TOT', 'Bìa nguyên vẹn, các bảng biểu và phụ lục địa lý rõ ràng, sạch đẹp.', 'DANG_BAN', NULL, CURRENT_TIMESTAMP
FROM ban_sao_sach b WHERE b.ma_ban_sao = 'BS-CU-S030';

-- S031 - The Silk Roads
INSERT INTO sach_cu (ban_sao_id, gia_de_xuat, gia_ban, tinh_trang, mo_ta, trang_thai, duyet_boi, ngay_duyet)
SELECT b.id, 108000, 115000, 'KHA', 'Có nếp gấp nhẹ ở góc bìa sau, bên trong hoàn hảo không vết bẩn.', 'DANG_BAN', NULL, CURRENT_TIMESTAMP
FROM ban_sao_sach b WHERE b.ma_ban_sao = 'BS-CU-S031';

-- S033 - Introduction to Algorithms
INSERT INTO sach_cu (ban_sao_id, gia_de_xuat, gia_ban, tinh_trang, mo_ta, trang_thai, duyet_boi, ngay_duyet)
SELECT b.id, 439000, 450000, 'TOT', 'Giáo trình chuẩn, gáy sách được giữ gìn rất tốt, tiết kiệm so với mua mới.', 'DANG_BAN', NULL, CURRENT_TIMESTAMP
FROM ban_sao_sach b WHERE b.ma_ban_sao = 'BS-CU-S033';

-- S037 - Why We Sleep
INSERT INTO sach_cu (ban_sao_id, gia_de_xuat, gia_ban, tinh_trang, mo_ta, trang_thai, duyet_boi, ngay_duyet)
SELECT b.id, 60000, 75000, 'CU', 'Dấu ấn thời gian nhẹ ở mép sách, các trang chữ đọc rõ ràng không rách.', 'DANG_BAN', NULL, CURRENT_TIMESTAMP
FROM ban_sao_sach b WHERE b.ma_ban_sao = 'BS-CU-S037';

-- S039 - Outlive
INSERT INTO sach_cu (ban_sao_id, gia_de_xuat, gia_ban, tinh_trang, mo_ta, trang_thai, duyet_boi, ngay_duyet)
SELECT b.id, 223000, 215000, 'NHU_MOI', 'Ấn bản gần đây, còn nguyên vẹn mùi sách mới, không một vết tì.', 'DANG_BAN', NULL, CURRENT_TIMESTAMP
FROM ban_sao_sach b WHERE b.ma_ban_sao = 'BS-CU-S039';

-- 2 cuốn chờ duyệt giá trong trang quản trị:
-- S001 - Clean Code
INSERT INTO sach_cu (ban_sao_id, gia_de_xuat, gia_ban, tinh_trang, mo_ta, trang_thai)
SELECT b.id, 181000, NULL, 'TOT', 'Bản sao hoàn trả sau thuê, hình thức tốt, chờ quản trị viên duyệt giá mở bán.', 'CHO_DUYET'
FROM ban_sao_sach b WHERE b.ma_ban_sao = 'BS-CU-S001';

-- S002 - Effective Java
INSERT INTO sach_cu (ban_sao_id, gia_de_xuat, gia_ban, tinh_trang, mo_ta, trang_thai)
SELECT b.id, 156000, NULL, 'KHA', 'Bản sao hoàn trả kỳ trước, ruột sạch, chờ quản trị viên phê duyệt mở bán.', 'CHO_DUYET'
FROM ban_sao_sach b WHERE b.ma_ban_sao = 'BS-CU-S002';
