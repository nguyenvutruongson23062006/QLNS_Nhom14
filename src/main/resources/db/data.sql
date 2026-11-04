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
    (33, 'Ten Speed Press'),
    (34, 'NXB Trẻ'),
    (35, 'NXB Kim Đồng'),
    (36, 'NXB Hội Nhà Văn'),
    (37, 'NXB Văn Học'),
    (38, 'NXB Lao Động'),
    (39, 'NXB Thế Giới'),
    (40, 'NXB Tổng Hợp TP.HCM');

INSERT INTO sach
    (ma_sach, ten_sach, tac_gia, isbn, danh_muc_id, nha_xuat_ban_id, nam_xuat_ban, gia_ban, so_luong_ton, mo_ta, hinh_anh)
VALUES
    ('S001', 'Clean Code', 'Robert C. Martin', '9780132350884', 1, 1, 2008, 329000, 18, 'Các nguyên tắc và ví dụ thực hành giúp lập trình viên viết mã rõ ràng, dễ bảo trì và dễ kiểm thử. Bạn đọc được hướng dẫn cách đặt tên, tổ chức hàm và lớp, xử lý lỗi, viết kiểm thử đơn vị và cải thiện mã nguồn thông qua việc tái cấu trúc. Phù hợp với sinh viên công nghệ thông tin và lập trình viên muốn xây dựng thói quen viết mã dễ đọc khi làm việc nhóm.', '/images/books/s001.jpg'),
    ('S002', 'Effective Java', 'Joshua Bloch', '9780134685991', 1, 2, 2017, 389000, 14, 'Tập hợp các thực hành thiết kế và lập trình Java hiệu quả, trình bày theo từng mục ngắn gọn. Nội dung đi sâu vào tạo và hủy đối tượng, phương thức, kiểu tổng quát, enum, ngoại lệ và lập trình đồng thời. Các tình huống thiết kế giúp người đọc nhận ra lựa chọn dễ gây lỗi và cân nhắc cách sử dụng Java phù hợp trong dự án thực tế.', '/images/books/s002.jpg'),
    ('S003', 'The Pragmatic Programmer', 'David Thomas; Andrew Hunt', '9780135957059', 1, 3, 2019, 419000, 12, 'Những kinh nghiệm nghề nghiệp cốt lõi để xây dựng phần mềm tốt và phát triển tư duy của một lập trình viên thực dụng. Cuốn sách bàn về trách nhiệm nghề nghiệp, giao tiếp trong nhóm, tự động hóa công việc và cách thích nghi khi yêu cầu thay đổi. Những gợi ý thực hành khuyến khích người đọc chủ động học hỏi, thử nghiệm và cải thiện chất lượng công việc mỗi ngày.', '/images/books/s003.jpg'),
    ('S004', 'Designing Data-Intensive Applications', 'Martin Kleppmann', '9781449373320', 1, 4, 2017, 529000, 9, 'Giải thích nền tảng của hệ thống dữ liệu tin cậy, có khả năng mở rộng và dễ bảo trì. Nội dung kết nối các chủ đề lưu trữ, mô hình dữ liệu, sao chép, phân vùng, giao dịch và xử lý dữ liệu phân tán. Phù hợp với người đã có nền tảng lập trình và muốn hiểu các đánh đổi khi lựa chọn kiến trúc cho hệ thống có nhiều dữ liệu.', '/images/books/s004.jpg'),
    ('S005', 'The Little Prince', 'Antoine de Saint-Exupéry', '9780156012195', 2, 5, 2000, 159000, 24, 'Câu chuyện giàu tính biểu tượng về tình bạn, trách nhiệm và cách người lớn nhìn thế giới. Qua cuộc gặp gỡ giữa một phi công và hoàng tử nhỏ đến từ hành tinh xa xôi, tác phẩm gợi mở những suy nghĩ về sự cô đơn, tình yêu và điều làm nên một mối gắn bó. Lối kể nhẹ nhàng cùng hình ảnh giàu liên tưởng phù hợp để đọc chậm và suy ngẫm ở nhiều lứa tuổi.', '/images/books/s005.jpg'),
    ('S006', '1984', 'George Orwell', '9780451524935', 2, 6, 1977, 179000, 20, 'Tiểu thuyết phản địa đàng về giám sát, tuyên truyền và quyền lực toàn trị. Nhân vật Winston Smith sống trong một xã hội nơi suy nghĩ, ngôn ngữ và ký ức đều chịu sự kiểm soát của quyền lực. Hành trình của anh đặt ra câu hỏi về tự do cá nhân, sự thật và khả năng giữ gìn bản sắc trước áp lực của tập thể.', '/images/books/s006.jpg'),
    ('S007', 'Norwegian Wood', 'Haruki Murakami', '9780375704024', 2, 7, 2000, 219000, 16, 'Tiểu thuyết trưởng thành xoay quanh ký ức, tình yêu và mất mát của những người trẻ tại Nhật Bản. Từ những hồi tưởng của Toru Watanabe, câu chuyện mở ra các mối quan hệ phức tạp và sự bối rối khi bước vào tuổi trưởng thành. Giọng văn trầm lắng hướng đến độc giả yêu thích tiểu thuyết tâm lý, với các chủ đề nhạy cảm về mất mát và sức khỏe tinh thần.', '/images/books/s007.jpg'),
    ('S008', 'To Kill a Mockingbird', 'Harper Lee', '9780061120084', 2, 8, 2006, 189000, 17, 'Tác phẩm văn học về công lý, định kiến và lòng trắc ẩn qua góc nhìn của một đứa trẻ. Qua lời kể của Scout và hình ảnh người cha Atticus Finch, câu chuyện đưa người đọc đến một cộng đồng miền Nam nước Mỹ với nhiều bất công và định kiến. Tác phẩm khơi gợi suy nghĩ về lòng can đảm, cách lắng nghe người khác và trách nhiệm bảo vệ điều đúng đắn.', '/images/books/s008.jpg'),
    ('S009', 'The Lean Startup', 'Eric Ries', '9780307887894', 3, 9, 2011, 249000, 15, 'Phương pháp xây dựng doanh nghiệp bằng thử nghiệm, học hỏi có kiểm chứng và cải tiến liên tục. Các khái niệm sản phẩm khả dụng tối thiểu, vòng lặp xây dựng – đo lường – học hỏi và điều chỉnh hướng đi được trình bày trong bối cảnh khởi nghiệp. Người đọc có thể tham khảo để kiểm tra giả định về khách hàng trước khi dành nhiều nguồn lực phát triển sản phẩm.', '/images/books/s009.jpg'),
    ('S010', 'Zero to One', 'Peter Thiel; Blake Masters', '9780804139298', 3, 10, 2014, 229000, 13, 'Các quan sát về đổi mới, lợi thế độc quyền và cách xây dựng doanh nghiệp tạo ra giá trị mới. Cuốn sách đặt vấn đề về việc tạo ra sản phẩm khác biệt thay vì chỉ lặp lại mô hình đã có trên thị trường. Những lập luận về công nghệ, thị trường ngách, đội ngũ và tầm nhìn dài hạn cung cấp góc nhìn để thảo luận khi phát triển ý tưởng kinh doanh.', '/images/books/s010.jpg'),
    ('S011', 'Thinking, Fast and Slow', 'Daniel Kahneman', '9780374533557', 3, 11, 2013, 279000, 11, 'Khám phá hai hệ thống tư duy và những thiên kiến ảnh hưởng đến quyết định của con người. Tác giả đối chiếu tư duy nhanh mang tính trực giác với tư duy chậm đòi hỏi sự tập trung và phân tích. Các ví dụ về phán đoán, rủi ro và cách diễn đạt thông tin giúp người đọc nhận diện giới hạn của trực giác trong đời sống và công việc.', '/images/books/s011.jpg'),
    ('S012', 'Good to Great', 'Jim Collins', '9780066620992', 3, 12, 2001, 259000, 10, 'Nghiên cứu các đặc điểm giúp một số công ty chuyển từ hoạt động tốt sang kết quả vượt trội. Các chủ đề gồm vai trò của lãnh đạo, lựa chọn con người, văn hóa kỷ luật và việc tập trung vào năng lực cốt lõi. Cuốn sách phù hợp để tham khảo và thảo luận về quản trị; các kết luận cần được cân nhắc theo hoàn cảnh của từng tổ chức.', '/images/books/s012.jpg'),
    ('S013', 'Atomic Habits', 'James Clear', '9780735211292', 4, 13, 2018, 239000, 28, 'Hướng dẫn xây dựng thói quen tốt và loại bỏ thói quen xấu thông qua những thay đổi nhỏ, có hệ thống. Nội dung tập trung vào mối quan hệ giữa tín hiệu, sự hấp dẫn, mức độ dễ thực hiện và phần thưởng của một hành vi. Người đọc có thể bắt đầu bằng việc thiết kế môi trường, giảm trở ngại và theo dõi những bước tiến nhỏ để duy trì việc học hoặc luyện tập.', '/images/books/s013.jpg'),
    ('S014', 'Deep Work', 'Cal Newport', '9781455586691', 4, 14, 2016, 229000, 18, 'Trình bày giá trị của khả năng tập trung sâu và cách tổ chức công việc trong môi trường nhiều xao nhãng. Tác giả phân tích sự khác nhau giữa công việc cần tập trung cao và những nhiệm vụ thường xuyên ngắt quãng sự chú ý. Các gợi ý về lịch làm việc, giới hạn xao nhãng và thời gian nghỉ giúp người đọc tự thiết kế một nhịp học tập hoặc làm việc phù hợp.', '/images/books/s014.jpg'),
    ('S015', 'How to Win Friends and Influence People', 'Dale Carnegie', '9780671027032', 4, 15, 1998, 199000, 21, 'Những nguyên tắc giao tiếp, hợp tác và xây dựng quan hệ tích cực trong đời sống và công việc. Nội dung nhấn mạnh việc quan tâm đến người đối diện, biết lắng nghe, ghi nhận đóng góp và góp ý một cách tôn trọng. Các tình huống giao tiếp có thể dùng làm điểm khởi đầu để rèn kỹ năng hợp tác trong lớp học, gia đình và môi trường nghề nghiệp.', '/images/books/s015.jpg'),
    ('S016', 'The 7 Habits of Highly Effective People', 'Stephen R. Covey', '9781982137274', 4, 16, 2020, 279000, 14, 'Khung phát triển cá nhân dựa trên tính chủ động, mục tiêu, ưu tiên và hợp tác hiệu quả. Cuốn sách dẫn dắt người đọc từ việc tự quản lý bản thân đến xây dựng quan hệ hợp tác và duy trì sự phát triển lâu dài. Các chủ đề như bắt đầu từ mục tiêu, ưu tiên việc quan trọng và tìm kiếm sự thấu hiểu phù hợp cho việc lập kế hoạch cá nhân.', '/images/books/s016.jpg'),
    ('S017', 'Harry Potter and the Sorcerer''s Stone', 'J. K. Rowling', '9780590353427', 5, 17, 1999, 209000, 26, 'Tập mở đầu hành trình của Harry Potter tại trường Hogwarts và thế giới phù thủy. Harry lần đầu biết về thân thế của mình, làm quen với Ron và Hermione, rồi khám phá những bí mật trong năm học đầu tiên ở Hogwarts. Câu chuyện kết hợp phép thuật, tình bạn và lòng can đảm, phù hợp với độc giả yêu thích các cuộc phiêu lưu kỳ ảo.', '/images/books/s017.jpg'),
    ('S018', 'Matilda', 'Roald Dahl', '9780142410370', 5, 18, 2013, 169000, 19, 'Câu chuyện về cô bé thông minh, ham đọc sách và hành trình tự bảo vệ mình trước những người lớn độc đoán. Với trí thông minh và niềm say mê đọc sách, Matilda tìm thấy sức mạnh để đối diện sự thờ ơ trong gia đình và những bất công ở trường học. Giọng kể dí dỏm cùng các tình huống kỳ ảo làm nổi bật giá trị của tri thức, lòng tốt và sự tự tin.', '/images/books/s018.jpg'),
    ('S019', 'Charlotte''s Web', 'E. B. White', '9780064400558', 5, 19, 1980, 149000, 22, 'Câu chuyện cảm động về tình bạn giữa chú heo Wilbur và cô nhện Charlotte. Trong khung cảnh một trang trại, Charlotte tìm cách giúp Wilbur thoát khỏi số phận mà chú lo sợ. Tác phẩm dùng lời kể ấm áp để nói về sự sẻ chia, lời hứa và những thay đổi tự nhiên của cuộc sống, thích hợp cho việc đọc cùng gia đình.', '/images/books/s019.jpg'),
    ('S020', 'The Gruffalo', 'Julia Donaldson', '9780142403877', 5, 18, 2006, 139000, 20, 'Truyện tranh vần điệu về một chú chuột nhỏ dùng trí thông minh để vượt qua các mối nguy trong rừng. Trên đường đi qua khu rừng, chú chuột liên tiếp gặp những con vật muốn ăn thịt mình và nghĩ ra câu chuyện về một quái vật đáng sợ. Nhịp kể lặp lại và các tình huống bất ngờ tạo cơ hội cho trẻ cùng đoán diễn biến và luyện kể chuyện.', '/images/books/s020.jpg'),
    ('S021', 'English Grammar in Use', 'Raymond Murphy', '9781108457651', 6, 20, 2019, 349000, 16, 'Giáo trình tự học ngữ pháp tiếng Anh trình độ trung cấp với bài giải thích và bài tập thực hành. Các bài học trình bày từng điểm ngữ pháp qua ví dụ và bài tập để người học tự kiểm tra mức độ hiểu bài. Phù hợp để ôn lại thì, cấu trúc câu và cách dùng từ trong quá trình học tiếng Anh trung cấp; nên kết hợp với luyện nghe, nói và viết.', '/images/books/s021.jpg'),
    ('S022', 'Essential Grammar in Use', 'Raymond Murphy', '9781107480551', 6, 20, 2015, 319000, 15, 'Tài liệu tự học ngữ pháp tiếng Anh dành cho người học ở trình độ sơ cấp. Nội dung giúp người mới học củng cố các mẫu câu cơ bản, động từ thông dụng và cách diễn đạt thường gặp trong giao tiếp. Có thể học theo từng chủ điểm hoặc chọn bài theo phần còn yếu để xây dựng nền tảng trước khi chuyển sang tài liệu trung cấp.', '/images/books/s022.jpg'),
    ('S023', 'Oxford Word Skills Intermediate', 'Ruth Gairns; Stuart Redman', '9780194620079', 6, 21, 2009, 289000, 13, 'Giáo trình phát triển vốn từ tiếng Anh trình độ trung cấp theo chủ đề và ngữ cảnh sử dụng. Từ vựng được đặt trong những tình huống quen thuộc như học tập, công việc, quan hệ xã hội và sinh hoạt hằng ngày. Các hoạt động thực hành giúp người học chú ý đến cách kết hợp từ và vận dụng từ mới vào câu thay vì chỉ ghi nhớ nghĩa riêng lẻ.', '/images/books/s023.jpg'),
    ('S024', 'Oxford Advanced Learner''s Dictionary', 'Oxford University Press', '9780194798488', 6, 21, 2020, 599000, 8, 'Từ điển tiếng Anh dành cho người học nâng cao với định nghĩa, ví dụ và hướng dẫn sử dụng từ. Người học có thể tra nghĩa, phát âm, từ loại và cách kết hợp từ để hiểu rõ hơn cách dùng trong ngữ cảnh. Đây là tài liệu tham khảo hữu ích khi đọc văn bản tiếng Anh, viết bài hoặc kiểm tra sự khác nhau giữa những từ gần nghĩa.', '/images/books/s024.jpg'),
    ('S025', 'A Brief History of Time', 'Stephen Hawking', '9780553380163', 7, 22, 1998, 219000, 18, 'Giới thiệu những câu hỏi lớn về vũ trụ, thời gian, hố đen và nguồn gốc của không gian. Cuốn sách đưa người đọc tiếp cận những ý tưởng về sự giãn nở của vũ trụ, thuyết tương đối và mối quan hệ giữa không gian với thời gian. Lối trình bày hướng đến độc giả phổ thông ham tìm hiểu vật lý, dù một số phần vẫn cần đọc chậm và suy ngẫm.', '/images/books/s025.jpg'),
    ('S026', 'Cosmos', 'Carl Sagan', '9780345539434', 7, 23, 2013, 249000, 14, 'Hành trình qua lịch sử khám phá vũ trụ và mối liên hệ giữa khoa học với nền văn minh nhân loại. Tác phẩm kết nối những câu chuyện về các vì sao, hành tinh và sự sống với hành trình tìm kiếm tri thức của con người. Tinh thần tò mò và tư duy khoa học xuyên suốt giúp cuốn sách phù hợp với độc giả muốn mở rộng góc nhìn về vị trí của chúng ta trong vũ trụ.', '/images/books/s026.jpg'),
    ('S027', 'The Selfish Gene', 'Richard Dawkins', '9780198788607', 7, 21, 2016, 289000, 12, 'Trình bày cách nhìn tiến hóa lấy gene làm trung tâm và những hệ quả đối với hành vi sinh vật. Tác giả sử dụng góc nhìn của gene để giải thích chọn lọc tự nhiên và thảo luận các hiện tượng như cạnh tranh, hợp tác và quan hệ họ hàng. Cuốn sách phù hợp với người quan tâm sinh học tiến hóa và muốn phân biệt cách giải thích khoa học với cách hiểu đạo đức thông thường.', '/images/books/s027.jpg'),
    ('S028', 'The Gene', 'Siddhartha Mukherjee', '9781476733524', 7, 24, 2017, 279000, 10, 'Lịch sử khoa học của di truyền học cùng các câu hỏi y học và đạo đức liên quan đến gene. Các câu chuyện về phát hiện khoa học được đan xen với suy ngẫm về di truyền, bản sắc và lựa chọn của con người. Người đọc có cơ hội tìm hiểu vì sao kiến thức về gene mở ra nhiều khả năng nhưng cũng đặt ra những câu hỏi khó về trách nhiệm xã hội.', '/images/books/s028.jpg'),
    ('S029', 'Sapiens', 'Yuval Noah Harari', '9780062316097', 8, 25, 2015, 279000, 23, 'Khái quát lịch sử loài người từ những cộng đồng săn bắt hái lượm đến xã hội hiện đại. Tác giả lần lượt bàn về những thay đổi trong nhận thức, nông nghiệp và khoa học đã tác động đến cách con người tổ chức cuộc sống. Đây là một góc nhìn tổng hợp để khơi gợi thảo luận về văn hóa, niềm tin và xã hội, phù hợp khi đọc cùng các nguồn lịch sử khác.', '/images/books/s029.jpg'),
    ('S030', 'Guns, Germs, and Steel', 'Jared Diamond', '9780393354324', 8, 26, 2017, 299000, 12, 'Phân tích ảnh hưởng của địa lý, cây trồng, vật nuôi và công nghệ đối với sự phát triển của các xã hội. Cuốn sách đặt câu hỏi vì sao các xã hội có những con đường phát triển khác nhau và tìm lời giải qua điều kiện môi trường. Những lập luận về lương thực, dịch bệnh và sự lan truyền kỹ thuật giúp mở rộng thảo luận về lịch sử ở quy mô lớn.', '/images/books/s030.jpg'),
    ('S031', 'The Silk Roads', 'Peter Frankopan', '9781101912379', 8, 7, 2017, 269000, 11, 'Kể lại lịch sử thế giới qua các tuyến giao thương và mạng lưới kết nối Á - Âu. Tác phẩm chuyển trọng tâm sang các vùng đất và tuyến đường nối liền phương Đông với phương Tây qua nhiều thời kỳ. Dòng chảy hàng hóa, tôn giáo, tư tưởng và quyền lực giúp người đọc hình dung lịch sử như một mạng lưới liên hệ giữa các cộng đồng.', '/images/books/s031.jpg'),
    ('S032', 'Prisoners of Geography', 'Tim Marshall', '9781501121470', 8, 24, 2016, 229000, 17, 'Giải thích cách địa hình và vị trí địa lý tác động đến chiến lược của các quốc gia. Qua các khu vực trên thế giới, tác giả bàn về vai trò của núi, sông, đồng bằng, biển và tài nguyên trong các lựa chọn chiến lược. Cuốn sách cung cấp một cách tiếp cận địa chính trị để tham khảo, cần kết hợp với bối cảnh lịch sử và xã hội khi đánh giá từng trường hợp.', '/images/books/s032.jpg'),
    ('S033', 'Introduction to Algorithms', 'Thomas H. Cormen; Charles E. Leiserson; Ronald L. Rivest; Clifford Stein', '9780262046305', 9, 27, 2022, 799000, 7, 'Giáo trình toàn diện về thiết kế, phân tích thuật toán và các cấu trúc dữ liệu nền tảng. Nội dung bao gồm độ phức tạp, sắp xếp, cấu trúc dữ liệu, đồ thị và các phương pháp thiết kế thuật toán như chia để trị, tham lam, quy hoạch động. Phù hợp với sinh viên đã có kiến thức lập trình cơ bản và muốn rèn cách chứng minh tính đúng đắn cũng như đánh giá hiệu quả thuật toán.', '/images/books/s033.jpg'),
    ('S034', 'Calculus', 'James Stewart', '9781285741550', 9, 28, 2016, 689000, 6, 'Giáo trình giải tích với lý thuyết, ví dụ và hệ thống bài tập dành cho bậc đại học. Người học được tiếp cận giới hạn, đạo hàm, tích phân và các ứng dụng qua cách trình bày gắn với bài toán cụ thể. Hệ thống ví dụ và bài tập phù hợp cho việc học theo môn, ôn kiến thức nền và luyện kỹ năng giải quyết vấn đề toán học.', '/images/books/s034.jpg'),
    ('S035', 'Campbell Biology', 'Lisa A. Urry và cộng sự', '9780134093413', 9, 29, 2017, 879000, 5, 'Giáo trình sinh học đại cương bao quát tế bào, di truyền, tiến hóa, đa dạng sinh học và sinh thái. Các chủ đề được kết nối từ cấp độ phân tử và tế bào đến cơ thể, quần thể và hệ sinh thái. Phù hợp với sinh viên khối khoa học sự sống hoặc người muốn xây dựng nền tảng sinh học có hệ thống trước khi đi sâu vào một chuyên ngành.', '/images/books/s035.jpg'),
    ('S036', 'The Elements of Statistical Learning', 'Trevor Hastie; Robert Tibshirani; Jerome Friedman', '9780387848570', 9, 30, 2009, 749000, 6, 'Tài liệu tham khảo chuyên sâu về mô hình thống kê, học máy và khai phá dữ liệu. Cuốn sách trình bày hồi quy, phân loại, lựa chọn mô hình và nhiều phương pháp học có giám sát hoặc không giám sát dưới góc nhìn thống kê. Phù hợp với người đã có nền tảng xác suất, đại số tuyến tính và muốn hiểu cơ sở lý thuyết phía sau các mô hình dữ liệu.', '/images/books/s036.jpg'),
    ('S037', 'Why We Sleep', 'Matthew Walker', '9781501144318', 10, 24, 2017, 239000, 18, 'Tổng hợp nghiên cứu về vai trò của giấc ngủ đối với trí nhớ, sức khỏe và hiệu suất. Tác giả thảo luận về các giai đoạn ngủ, nhịp sinh học và mối liên hệ giữa nghỉ ngơi với hoạt động hằng ngày. Cuốn sách dành cho độc giả quan tâm khoa học giấc ngủ; những nội dung về sức khỏe nên được đọc như thông tin tham khảo thay vì chỉ dẫn điều trị cá nhân.', '/images/books/s037.jpg'),
    ('S038', 'The Body Keeps the Score', 'Bessel van der Kolk', '9780143127741', 10, 31, 2015, 269000, 12, 'Khảo sát tác động của sang chấn lên cơ thể và tâm trí cùng các hướng tiếp cận phục hồi. Thông qua các câu chuyện lâm sàng và góc nhìn nghiên cứu, tác giả mô tả cách trải nghiệm sang chấn có thể ảnh hưởng đến cảm xúc, trí nhớ và phản ứng cơ thể. Tác phẩm có nội dung nhạy cảm, phù hợp để tìm hiểu và thảo luận về sức khỏe tinh thần, không thay thế hỗ trợ chuyên môn.', '/images/books/s038.jpg'),
    ('S039', 'Outlive', 'Peter Attia; Bill Gifford', '9780593236598', 10, 32, 2023, 319000, 15, 'Trình bày cách tiếp cận chủ động đối với sức khỏe lâu dài, phòng ngừa bệnh mạn tính và nâng cao chất lượng sống. Cuốn sách bàn về việc duy trì khả năng vận động, chú ý đến dinh dưỡng, giấc ngủ và sức khỏe cảm xúc khi tuổi tác tăng lên. Những phân tích về nguy cơ và phòng ngừa cung cấp chủ đề để người đọc trao đổi với chuyên gia y tế theo hoàn cảnh riêng.', '/images/books/s039.jpg'),
    ('S040', 'The Life-Changing Magic of Tidying Up', 'Marie Kondo', '9781607747307', 10, 33, 2014, 199000, 20, 'Phương pháp sắp xếp không gian sống dựa trên việc giữ lại những đồ vật thực sự có ý nghĩa. Tác giả đề xuất sắp xếp đồ đạc theo nhóm và cân nhắc cảm nhận của bản thân khi quyết định giữ lại hay cho đi. Những gợi ý về quần áo, sách vở và vật dụng cá nhân giúp người đọc từng bước tổ chức không gian sống theo nhu cầu của mình.', '/images/books/s040.jpg'),
    ('S041', 'Dế Mèn Phiêu Lưu Ký', 'Tô Hoài', '9786042183215', 5, 35, 2021, 65000, 45, 'Kiệt tác văn học thiếu nhi Việt Nam kể về hành trình chu du, trưởng thành và bài học về lòng nhân ái, dũng cảm của chú Dế Mèn qua nhiều vùng đất kỳ thú.', '/images/books/s041.jpg'),
    ('S042', 'Tuổi Thơ Dữ Dội', 'Phùng Quán', '9786049903112', 2, 37, 2020, 145000, 30, 'Bản trường ca bất hủ về những thiếu niên quả cảm của đội trinh sát Vệ Quốc Đoàn trong những ngày khói lửa tại chiến khu Thừa Thiên Huế.', '/images/books/s042.jpg'),
    ('S043', 'Số Đỏ', 'Vũ Trọng Phụng', '9786049903204', 2, 37, 2019, 85000, 35, 'Tác phẩm trào phúng đỉnh cao của văn học hiện thực phê phán Việt Nam thế kỷ 20, vạch trần xã hội thực dân nửa phong kiến qua nhân vật Xuân Tóc Đỏ.', '/images/books/s043.jpg'),
    ('S044', 'Cánh Đồng Bất Tận', 'Nguyễn Ngọc Tư', '9786041189423', 2, 34, 2021, 95000, 40, 'Tập truyện ngắn nổi tiếng khắc họa chân thực và sâu lắng số phận trôi dạt của con người trên miền sông nước Nam Bộ.', '/images/books/s044.jpg'),
    ('S045', 'Cho Tôi Xin Một Vé Đi Tuổi Thơ', 'Nguyễn Nhật Ánh', '9786041185340', 2, 34, 2022, 90000, 50, 'Cuốn vé thần kỳ đưa người đọc trở về những năm tháng ấu thơ trong trẻo, hồn nhiên với những câu chuyện dí dỏm và đầy triết lý nhân sinh.', '/images/books/s045.jpg'),
    ('S046', 'Tôi Thấy Hoa Vàng Trên Cỏ Xanh', 'Nguyễn Nhật Ánh', '9786041185357', 2, 34, 2022, 115000, 40, 'Bức tranh làng quê bình dị với tình anh em, tình bạn và những rung động đầu đời trong sáng của tuổi niên thiếu.', '/images/books/s046.jpg'),
    ('S047', 'Mắt Biếc', 'Nguyễn Nhật Ánh', '9786041185364', 2, 34, 2021, 110000, 35, 'Bản tình ca đượm buồn về mối tình đơn phương thủy chung của chàng thanh niên Ngạn dành cho cô bạn thời thơ ấu có đôi mắt biếc.', '/images/books/s047.jpg'),
    ('S048', 'Nỗi Buồn Chiến Tranh', 'Bảo Ninh', '9786049931207', 2, 36, 2020, 125000, 25, 'Kiệt tác văn học thời kỳ đổi mới phản ánh chân thực hiện thực tàn khốc của chiến tranh và chiều sâu tâm lý con người thời hậu chiến.', '/images/books/s048.jpg'),
    ('S049', 'Đất Rừng Phương Nam', 'Đoàn Giỏi', '9786042183222', 5, 35, 2022, 85000, 30, 'Cuộc phiêu lưu sống động của cậu bé An giữa thiên nhiên trù phú và con người hào sảng, bất khuất của miền Tây Nam Bộ.', '/images/books/s049.jpg'),
    ('S050', 'Vang Bóng Một Thời', 'Nguyễn Tuân', '9786049903303', 2, 37, 2019, 75000, 20, 'Tập tùy bút bậc thầy tái hiện vẻ đẹp văn hóa cổ truyền thanh nhã, tài hoa và khí phách của các bậc nho sĩ thời xưa.', '/images/books/s050.jpg'),
    ('S051', 'Chiếc Thuyền Ngoài Xa', 'Nguyễn Minh Châu', '9786049903402', 2, 37, 2020, 70000, 25, 'Tác phẩm văn học mang tính triết lý nhân sinh sâu sắc về góc nhìn đa chiều giữa cái đẹp nghệ thuật và hiện thực cuộc đời.', '/images/books/s051.jpg'),
    ('S052', 'Bỉ Vỏ', 'Nguyên Hồng', '9786049903501', 2, 37, 2021, 80000, 22, 'Bức tranh xót xa về số phận người phụ nữ nghèo khổ bị đẩy vào con đường tội lỗi nơi bến cảng Hải Phòng thời tiền chiến.', '/images/books/s052.jpg'),
    ('S053', 'Chí Phèo', 'Nam Cao', '9786049903600', 2, 37, 2022, 75000, 35, 'Tác phẩm kinh điển về bi kịch tha hóa và khát vọng lương thiện bị cự tuyệt của người nông dân trong xã hội cũ.', '/images/books/s053.jpg'),
    ('S054', 'Tắt Đèn', 'Ngô Tất Tố', '9786049903709', 2, 37, 2021, 70000, 30, 'Bức tranh ngột ngạt của nông thôn Việt Nam trước Cách mạng và hình tượng chị Dậu giàu đức hy sinh và tinh thần quật khởi.', '/images/books/s054.jpg'),
    ('S055', 'Gió Đầu Mùa', 'Thạch Lam', '9786049931306', 2, 36, 2020, 68000, 28, 'Những trang văn nhẹ nhàng, tinh tế và thấm đẫm lòng trắc ẩn về tình người giữa cuộc sống đời thường bình dị.', '/images/books/s055.jpg'),
    ('S056', 'Cà Phê Cùng Tony', 'Tony Buổi Sáng', '9786041186002', 4, 34, 2021, 90000, 50, 'Những bài viết truyền cảm hứng khởi nghiệp, rèn luyện văn minh, sự trung thực và tính tự lập cho các bạn trẻ hiện đại.', '/images/books/s056.jpg'),
    ('S057', 'Trên Đường Băng', 'Tony Buổi Sáng', '9786041186019', 4, 34, 2022, 95000, 60, 'Cuốn cẩm nang hành trang về thái độ sống, tính kỷ luật, tư duy hội nhập toàn cầu và tinh thần vươn xa của người trẻ.', '/images/books/s057.jpg'),
    ('S058', 'Hiểu Về Trái Tim', 'Minh Niệm', '9786045899201', 10, 40, 2022, 140000, 45, 'Những phân tích tâm lý sâu sắc về tình yêu, tha thứ, khổ đau và bình yên nội tâm, giúp nuôi dưỡng tâm hồn mỗi ngày.', '/images/books/s058.jpg'),
    ('S059', 'Muôn Kiếp Nhân Sinh', 'Nguyên Phong', '9786045899300', 2, 40, 2021, 168000, 40, 'Hành trình trải nghiệm luân hồi và quy luật nhân quả qua nhiều kiếp sống từ nền văn minh Atlantis đến Ai Cập cổ đại.', '/images/books/s059.jpg'),
    ('S060', 'Hành Trình Về Phương Đông', 'Nguyên Phong', '9786047781002', 2, 39, 2020, 110000, 35, 'Ghi chép về chuyến khảo sát tâm linh kỳ bí của đoàn khoa học Hoàng gia Anh tại dãy Himalaya và xứ sở Ấn Độ huyền bí.', '/images/books/s060.jpg'),
    ('S061', 'Đắc Nhân Tâm', 'Dale Carnegie', '9786045899409', 4, 40, 2022, 98000, 70, 'Bản dịch kinh điển của học giả Nguyễn Hiến Lê, nghệ thuật thu phục lòng người và xây dựng mối quan hệ giao tiếp bền vững.', '/images/books/s061.jpg'),
    ('S062', 'Quẳng Gánh Lo Đi Và Vui Sống', 'Dale Carnegie', '9786045899508', 4, 40, 2021, 92000, 50, 'Phương pháp vượt qua âu lo, làm chủ căng thẳng và tìm lại niềm vui trong công việc cũng như cuộc sống thường nhật.', '/images/books/s062.jpg'),
    ('S063', 'Nhà Giả Kim', 'Paulo Coelho', '9786049931405', 2, 36, 2022, 79000, 80, 'Câu chuyện ngụ ngôn giàu triết lý về hành trình theo đuổi vận mệnh và lắng nghe tiếng gọi trái tim của chàng chăn cừu Santiago.', '/images/books/s063.jpg'),
    ('S064', 'Cây Cam Ngọt Của Tôi', 'José Mauro de Vasconcelos', '9786049931504', 2, 36, 2022, 108000, 60, 'Tác phẩm chạm đến trái tim hàng triệu bạn đọc về lòng trắc ẩn, tình yêu thương và sự ấm áp giữa những cay đắng cuộc đời.', '/images/books/s064.jpg'),
    ('S065', 'Chiến Binh Cầu Vồng', 'Andrea Hirata', '9786049931603', 2, 36, 2021, 109000, 40, 'Câu chuyện có thật đầy xúc động về tình bạn và nghị lực vượt lên nghịch cảnh để học tập của 10 đứa trẻ nghèo ở đảo Belitong.', '/images/books/s065.jpg'),
    ('S066', 'Bước Chậm Lại Giữa Thế Gian Vội Vã', 'Hae Min', '9786047782009', 4, 39, 2022, 95000, 35, 'Những suy ngẫm sâu sắc và dịu dàng giúp tìm lại sự tĩnh lặng, an yên giữa nhịp sống hối hả của xã hội hiện đại.', '/images/books/s066.jpg'),
    ('S067', 'Lối Sống Tối Giản Của Người Nhật', 'Sasaki Fumio', '9786045999003', 4, 38, 2021, 88000, 30, 'Trải nghiệm thực tế về lối sống giản lược đồ đạc để tập trung vào những giá trị tinh thần và tự do đích thực.', '/images/books/s067.jpg'),
    ('S068', 'Tìm Lại Chính Mình', 'Thích Nhất Hạnh', '9786045899607', 10, 40, 2020, 85000, 35, 'Phương pháp thực tập chánh niệm đơn giản và sâu sắc giúp chuyển hóa những năng lượng tiêu cực thành bình an và tình thương.', '/images/books/s068.jpg'),
    ('S069', 'An Lạc Từng Bước Chân', 'Thích Nhất Hạnh', '9786045999102', 10, 38, 2021, 82000, 30, 'Nghệ thuật sống tỉnh thức trong từng hơi thở, bước chân và việc làm bình dị mỗi ngày.', '/images/books/s069.jpg'),
    ('S070', 'Tâm Lý Học Về Tiền', 'Morgan Housel', '9786041187009', 3, 34, 2022, 160000, 45, 'Những bài học vượt thời gian về sự giàu có, lòng tham và hạnh phúc qua lăng kính hành vi tâm lý con người.', '/images/books/s070.jpg'),
    ('S071', 'Khởi Nghiệp Tinh Gọn', 'Eric Ries', '9786045899706', 3, 40, 2021, 145000, 25, 'Mô hình xây dựng doanh nghiệp đổi mới sáng tạo, thử nghiệm nhanh và tiết kiệm tối đa nguồn lực.', '/images/books/s071.jpg'),
    ('S072', 'Lược Sử Loài Người', 'Yuval Noah Harari', '9786047783006', 8, 39, 2022, 220000, 35, 'Góc nhìn lịch sử vĩ mô khám phá cách loài vượn người không có gì nổi trội trở thành bá chủ hành tinh Trái Đất.', '/images/books/s072.jpg'),
    ('S073', 'Đại Việt Sử Ký Toàn Thư', 'Ngô Sĩ Liên', '9786049903808', 8, 37, 2020, 350000, 15, 'Bộ quốc sử đồ sộ ghi chép toàn diện lịch sử dựng nước và giữ nước oai hùng của dân tộc Việt Nam qua các triều đại.', '/images/books/s073.jpg'),
    ('S074', 'Việt Nam Sử Lược', 'Trần Trọng Kim', '9786049903907', 8, 37, 2021, 185000, 25, 'Cuốn sử thông lược đầu tiên viết bằng chữ quốc ngữ, trình bày khúc chiết và mạch lạc tiến trình lịch sử dân tộc.', '/images/books/s074.jpg'),
    ('S075', 'Hoàng Tử Bé', 'Antoine de Saint-Exupéry', '9786042183307', 5, 35, 2022, 75000, 60, 'Bản dịch tiếng Việt kinh điển của cuốn sách gối đầu giường về tình bạn, tình yêu và những bài học cuộc sống sâu sắc.', '/images/books/s075.jpg');


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
