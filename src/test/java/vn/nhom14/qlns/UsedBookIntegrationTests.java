package vn.nhom14.qlns;

import java.net.*;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Pattern;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("demo")
class UsedBookIntegrationTests {
    @Value("${local.server.port}") int port;
    @Autowired JdbcTemplate jdbc;
    final List<String> accounts=new ArrayList<>();final List<Long> books=new ArrayList<>();

    class Session {
        final HttpClient client=HttpClient.newBuilder().cookieHandler(new CookieManager(null,CookiePolicy.ACCEPT_ALL)).build();
        HttpResponse<String> json(String method,String path,String body)throws Exception {var builder=HttpRequest.newBuilder(URI.create("http://localhost:"+port+path)).header("Content-Type","application/json");if(!method.equals("GET")){var csrf=client.send(HttpRequest.newBuilder(URI.create("http://localhost:"+port+"/api/auth/csrf")).GET().build(),HttpResponse.BodyHandlers.ofString());var match=Pattern.compile("\"token\":\"([^\"]+)\"").matcher(csrf.body());assertTrue(match.find());builder.header("X-CSRF-TOKEN",match.group(1));}return client.send(builder.method(method,body==null?HttpRequest.BodyPublishers.noBody():HttpRequest.BodyPublishers.ofString(body)).build(),HttpResponse.BodyHandlers.ofString());}
        void login(String username,String password)throws Exception {var csrf=json("GET","/api/auth/csrf",null);var match=Pattern.compile("\"token\":\"([^\"]+)\"").matcher(csrf.body());assertTrue(match.find());var request=HttpRequest.newBuilder(URI.create("http://localhost:"+port+"/api/auth/login")).header("Content-Type","application/x-www-form-urlencoded").header("X-CSRF-TOKEN",match.group(1)).POST(HttpRequest.BodyPublishers.ofString("username="+URLEncoder.encode(username,StandardCharsets.UTF_8)+"&password="+URLEncoder.encode(password,StandardCharsets.UTF_8))).build();assertEquals(200,client.send(request,HttpResponse.BodyHandlers.ofString()).statusCode());}
    }
    record Customer(Session session,long id){}

    @AfterEach void cleanup(){
        for(String username:accounts){String customer="SELECT k.id FROM khach_hang k JOIN tai_khoan t ON t.id=k.tai_khoan_id WHERE t.ten_dang_nhap=?";jdbc.update("DELETE FROM chi_tiet_don_hang_sach_cu WHERE don_hang_id IN(SELECT id FROM don_hang WHERE khach_hang_id IN("+customer+"))",username);jdbc.update("DELETE FROM chi_tiet_don_hang WHERE don_hang_id IN(SELECT id FROM don_hang WHERE khach_hang_id IN("+customer+"))",username);jdbc.update("DELETE FROM don_hang WHERE khach_hang_id IN("+customer+")",username);jdbc.update("DELETE FROM chi_tiet_phieu_thue WHERE phieu_thue_id IN(SELECT id FROM phieu_thue WHERE khach_hang_id IN("+customer+"))",username);jdbc.update("DELETE FROM phieu_thue WHERE khach_hang_id IN("+customer+")",username);jdbc.update("DELETE FROM khach_hang WHERE tai_khoan_id IN(SELECT id FROM tai_khoan WHERE ten_dang_nhap=?)",username);jdbc.update("DELETE FROM tai_khoan WHERE ten_dang_nhap=?",username);}
        for(Long book:books){jdbc.update("DELETE FROM sach_cu WHERE ban_sao_id IN(SELECT id FROM ban_sao_sach WHERE sach_id=?)",book);jdbc.update("DELETE FROM ban_sao_sach WHERE sach_id=?",book);jdbc.update("DELETE FROM cau_hinh_thue_sach WHERE sach_id=?",book);jdbc.update("DELETE FROM sach WHERE id=?",book);}
    }
    Session admin()throws Exception {Session s=new Session();s.login("admin","Admin@123456");return s;}
    Customer customer()throws Exception {String username="used_"+UUID.randomUUID().toString().replace("-","").substring(0,12);accounts.add(username);Session s=new Session();assertEquals(201,s.json("POST","/api/auth/register","{\"username\":\""+username+"\",\"password\":\"Password123\",\"email\":\""+username+"@example.com\",\"hoTen\":\"Khách sách cũ\"}").statusCode());long id=jdbc.queryForObject("SELECT k.id FROM khach_hang k JOIN tai_khoan t ON t.id=k.tai_khoan_id WHERE t.ten_dang_nhap=?",Long.class,username);s.login(username,"Password123");return new Customer(s,id);}
    long returnedCopy(long customerId){String code="USED"+UUID.randomUUID().toString().replace("-","").substring(0,10);jdbc.update("INSERT INTO sach(ma_sach,ten_sach,tac_gia,danh_muc_id,nha_xuat_ban_id,gia_ban,so_luong_ton) VALUES(?,?,?,1,1,100000,0)",code,"Sách đã qua thuê "+code,"Tác giả thật");long book=jdbc.queryForObject("SELECT id FROM sach WHERE ma_sach=?",Long.class,code);books.add(book);jdbc.update("INSERT INTO cau_hinh_thue_sach(sach_id,cho_thue,gia_thue_ngay,tien_coc,so_ngay_mac_dinh,so_lan_gia_han,phi_tre_ngay) VALUES(?,TRUE,2000,50000,14,1,5000)",book);String suffix=UUID.randomUUID().toString().replace("-","");jdbc.update("INSERT INTO ban_sao_sach(ma_ban_sao,sach_id,tinh_trang,trang_thai) VALUES(?,?,'TOT','SAN_SANG')","BS"+suffix,book);long copy=jdbc.queryForObject("SELECT id FROM ban_sao_sach WHERE sach_id=?",Long.class,book);jdbc.update("INSERT INTO phieu_thue(ma_phieu_thue,khach_hang_id,kenh_tao,trang_thai,client_request_id,phuong_thuc_nhan) VALUES(?,?,'TAI_QUAY','HOAN_THANH',?,'TAI_QUAY')","PT"+suffix,customerId,UUID.randomUUID().toString());long rental=jdbc.queryForObject("SELECT id FROM phieu_thue WHERE ma_phieu_thue=?",Long.class,"PT"+suffix);jdbc.update("INSERT INTO chi_tiet_phieu_thue(phieu_thue_id,ban_sao_id,gia_thue_ngay,tien_coc,phi_tre_ngay,ngay_hen_tra,ngay_tra_thuc_te,tinh_trang_truoc,tinh_trang_sau) VALUES(?,?,?,?,?,?,?,?,?)",rental,copy,2000,50000,5000,LocalDate.now(),LocalDate.now(),"MOI","TOT");return copy;}
    long field(String json,String name){var m=Pattern.compile("\""+name+"\":(\\d+)").matcher(json);assertTrue(m.find(),json);return Long.parseLong(m.group(1));}

    @Test void returnedCopyCanBeApprovedSoldCancelledRelistedAndCompleted()throws Exception {
        Session admin=admin();Customer first=customer(),second=customer();long copy=returnedCopy(first.id());
        var candidates=admin.json("GET","/api/used-books/candidates",null);assertEquals(200,candidates.statusCode());assertTrue(candidates.body().contains("\"giaDeXuat\":55000.00"),candidates.body());
        var created=admin.json("POST","/api/used-books","{\"banSaoId\":"+copy+",\"moTa\":\"Đã kiểm tra, còn tốt\"}");assertEquals(201,created.statusCode(),created.body());long used=field(created.body(),"id");
        assertEquals(400,admin.json("PATCH","/api/used-books/"+used+"/approve","{\"giaBan\":100000}").statusCode());
        assertEquals(200,admin.json("PATCH","/api/used-books/"+used+"/approve","{\"giaBan\":52000}").statusCode());assertTrue(first.session().json("GET","/api/used-books",null).body().contains("Sách đã qua thuê"));
        String token=UUID.randomUUID().toString(),body="{\"tenNguoiNhan\":\"Khách một\",\"soDienThoaiNhan\":\"0901234567\",\"diaChiGiao\":\"Quận 1\",\"ghiChu\":\"Giao giờ hành chính\",\"clientRequestId\":\""+token+"\",\"sachCuIds\":["+used+"]}";
        var order=first.session().json("POST","/api/used-books/orders",body);assertEquals(201,order.statusCode(),order.body());assertTrue(order.body().contains("\"loaiHang\":\"SACH_CU\""));long orderId=field(order.body(),"id");
        assertEquals(201,first.session().json("POST","/api/used-books/orders",body).statusCode());
        assertEquals(409,first.session().json("POST","/api/used-books/orders",body.replace("Giao giờ hành chính","Giao buổi tối")).statusCode());
        assertEquals(400,first.session().json("POST","/api/used-books/orders",body.replace(token,"------------------------------------")).statusCode());
        assertEquals(409,second.session().json("POST","/api/used-books/orders",body.replace(token,UUID.randomUUID().toString()).replace("Khách một","Khách hai")).statusCode());
        assertEquals(200,first.session().json("PATCH","/api/orders/"+orderId+"/status","{\"trangThai\":\"DA_HUY\"}").statusCode());assertEquals("DANG_BAN",jdbc.queryForObject("SELECT trang_thai FROM sach_cu WHERE id=?",String.class,used));
        String secondBody=body.replace(token,UUID.randomUUID().toString()).replace("Khách một","Khách hai");var secondOrder=second.session().json("POST","/api/used-books/orders",secondBody);assertEquals(201,secondOrder.statusCode(),secondOrder.body());long secondId=field(secondOrder.body(),"id");
        for(String state:List.of("DA_XAC_NHAN","DANG_GIAO","HOAN_THANH"))assertEquals(200,admin.json("PATCH","/api/orders/"+secondId+"/status","{\"trangThai\":\""+state+"\"}").statusCode());
        assertEquals("DA_BAN",jdbc.queryForObject("SELECT trang_thai FROM ban_sao_sach WHERE id=?",String.class,copy));
    }
}
