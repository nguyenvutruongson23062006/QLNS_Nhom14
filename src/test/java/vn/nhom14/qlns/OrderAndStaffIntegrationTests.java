package vn.nhom14.qlns;

import java.net.*;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Pattern;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT,properties={
        "QLNS_VIETQR_BANK_BIN=970436","QLNS_VIETQR_ACCOUNT_NUMBER=123456789","QLNS_VIETQR_ACCOUNT_NAME=NHOM 14"})
@ActiveProfiles("demo")
class OrderAndStaffIntegrationTests {
    @Value("${local.server.port}") int port;
    @Autowired JdbcTemplate jdbc;
    private final List<String> accounts=new ArrayList<>();
    private final List<Long> books=new ArrayList<>();

    class Session {
        final HttpClient client=HttpClient.newBuilder().cookieHandler(new CookieManager(null,CookiePolicy.ACCEPT_ALL)).build();
        HttpResponse<String> request(String method,String path,String body,boolean csrf,String type)throws Exception{
            var builder=HttpRequest.newBuilder(URI.create("http://localhost:"+port+path)).header("Content-Type",type);
            if(csrf&&!method.equals("GET")){
                var token=request("GET","/api/auth/csrf",null,false,"application/json");
                var match=Pattern.compile("\"token\":\"([^\"]+)\"").matcher(token.body());assertTrue(match.find(),token.body());
                builder.header("X-CSRF-TOKEN",match.group(1));
            }
            return client.send(builder.method(method,body==null?HttpRequest.BodyPublishers.noBody():HttpRequest.BodyPublishers.ofString(body)).build(),HttpResponse.BodyHandlers.ofString());
        }
        HttpResponse<String> json(String method,String path,String body)throws Exception{return request(method,path,body,true,"application/json");}
        HttpResponse<String> login(String username,String password)throws Exception{return request("POST","/api/auth/login",
                "username="+URLEncoder.encode(username,StandardCharsets.UTF_8)+"&password="+URLEncoder.encode(password,StandardCharsets.UTF_8),true,"application/x-www-form-urlencoded");}
    }

    @AfterEach void cleanup(){
        for(String name:accounts){
            jdbc.update("DELETE FROM chi_tiet_don_hang WHERE don_hang_id IN(SELECT o.id FROM don_hang o JOIN khach_hang k ON k.id=o.khach_hang_id JOIN tai_khoan t ON t.id=k.tai_khoan_id WHERE t.ten_dang_nhap=?)",name);
            jdbc.update("DELETE FROM thanh_toan WHERE khach_hang_id IN(SELECT k.id FROM khach_hang k JOIN tai_khoan t ON t.id=k.tai_khoan_id WHERE t.ten_dang_nhap=?)",name);
            jdbc.update("DELETE FROM don_hang WHERE khach_hang_id IN(SELECT k.id FROM khach_hang k JOIN tai_khoan t ON t.id=k.tai_khoan_id WHERE t.ten_dang_nhap=?)",name);
            jdbc.update("DELETE FROM khach_hang WHERE tai_khoan_id IN(SELECT id FROM tai_khoan WHERE ten_dang_nhap=?)",name);
            jdbc.update("DELETE FROM tai_khoan WHERE ten_dang_nhap=?",name);
        }
        for(Long id:books)jdbc.update("DELETE FROM sach WHERE id=?",id);
    }
    Session login(String username,String password)throws Exception{var s=new Session();assertEquals(200,s.login(username,password).statusCode());return s;}
    String unique(String prefix){return prefix+UUID.randomUUID().toString().replace("-","").substring(0,12);}
    Session customer()throws Exception{
        String name=unique("customer_");accounts.add(name);var s=new Session();
        assertEquals(201,s.json("POST","/api/auth/register","{\"username\":\""+name+"\",\"password\":\"Password123\",\"email\":\""+name+"@example.com\",\"hoTen\":\"Khách kiểm thử\"}").statusCode());
        assertEquals(200,s.login(name,"Password123").statusCode());return s;
    }
    long book(int stock){String code=unique("BOOK");jdbc.update("INSERT INTO sach(ma_sach,ten_sach,tac_gia,danh_muc_id,nha_xuat_ban_id,gia_ban,so_luong_ton) VALUES(?,?,?,1,1,10000,?)",code,"Sách "+code,"Nhóm 14",stock);long id=jdbc.queryForObject("SELECT id FROM sach WHERE ma_sach=?",Long.class,code);books.add(id);return id;}
    String order(long book,int quantity,String token){return "{\"tenNguoiNhan\":\"Nguyễn Văn A\",\"soDienThoaiNhan\":\"0901234567\",\"diaChiGiao\":\"TP.HCM\",\"clientRequestId\":\""+token+"\",\"items\":[{\"sachId\":"+book+",\"soLuong\":"+quantity+"}]}";}
    long id(String json){var match=Pattern.compile("\"id\":(\\d+)").matcher(json);assertTrue(match.find(),json);return Long.parseLong(match.group(1));}

    @Test void registrationRejectsPasswordLongerThanBcryptUtf8Limit()throws Exception{
        String name=unique("unicode_");String password="đ".repeat(40);var session=new Session();
        var response=session.json("POST","/api/auth/register","{\"username\":\""+name+"\",\"password\":\""+password+"\",\"email\":\""+name+"@example.com\",\"hoTen\":\"Khách kiểm thử\"}");
        assertEquals(400,response.statusCode(),response.body());
        assertEquals(0,jdbc.queryForObject("SELECT COUNT(*) FROM tai_khoan WHERE ten_dang_nhap=?",Integer.class,name));
    }

    @Test void customerCanChangePasswordAndOldPasswordStopsWorking()throws Exception{
        var customer=customer();
        var changed=customer.json("PUT","/api/auth/password","{\"currentPassword\":\"Password123\",\"newPassword\":\"NewPassword123\"}");
        assertEquals(200,changed.statusCode(),changed.body());
        assertEquals(401,new Session().login(accounts.getLast(),"Password123").statusCode());
        assertEquals(200,new Session().login(accounts.getLast(),"NewPassword123").statusCode());
    }

    @Test void adminCreatesRealStaffRoleAndStaffPermissionsAreBackendEnforced()throws Exception{
        var admin=login("admin","Admin@123456");String name=unique("staff_");accounts.add(name);
        assertEquals(201,admin.json("POST","/api/accounts","{\"username\":\""+name+"\",\"password\":\"Password123\",\"email\":\""+name+"@example.com\"}").statusCode());
        var staff=login(name,"Password123");assertTrue(staff.json("GET","/api/auth/me",null).body().contains("\"role\":\"NHAN_VIEN\""));
        assertEquals(200,staff.json("GET","/quan-tri.html",null).statusCode());
        assertEquals(403,staff.json("GET","/api/accounts",null).statusCode());
        assertEquals(403,staff.json("POST","/api/danh-muc","{}").statusCode());
        String code=unique("STAFFBOOK");var created=staff.json("POST","/api/sach","{\"maSach\":\""+code+"\",\"tenSach\":\"Sách nhân viên\",\"tacGia\":\"Nhóm 14\",\"giaBan\":10000,\"soLuongTon\":2,\"trangThai\":true,\"danhMucId\":1,\"nhaXuatBanId\":1}");
        assertEquals(201,created.statusCode(),created.body());books.add(jdbc.queryForObject("SELECT id FROM sach WHERE ma_sach=?",Long.class,code));
    }

    @Test void checkoutUsesServerPriceIsIdempotentAndCancellationRestoresOnce()throws Exception{
        var customer=customer();long book=book(3);String token=UUID.randomUUID().toString(),body=order(book,2,token);
        var first=customer.json("POST","/api/orders",body);assertEquals(201,first.statusCode(),first.body());long order=id(first.body());
        assertTrue(first.body().contains("img.vietqr.io"),first.body());
        assertTrue(first.body().contains("\"mucDich\":\"DON_HANG\""),first.body());
        assertEquals(1,jdbc.queryForObject("SELECT so_luong_ton FROM sach WHERE id=?",Integer.class,book));
        assertEquals(order,id(customer.json("POST","/api/orders",body).body()));
        assertEquals(1,jdbc.queryForObject("SELECT COUNT(*) FROM don_hang WHERE id=?",Integer.class,order));
        assertEquals(1,jdbc.queryForObject("SELECT COUNT(*) FROM thanh_toan WHERE tham_chieu_loai='DON_HANG' AND tham_chieu_id=?",Integer.class,order));
        assertEquals(200,customer.json("PATCH","/api/orders/"+order+"/status","{\"trangThai\":\"DA_HUY\"}").statusCode());
        assertEquals(200,customer.json("PATCH","/api/orders/"+order+"/status","{\"trangThai\":\"DA_HUY\"}").statusCode());
        assertEquals("THAT_BAI",jdbc.queryForObject("SELECT trang_thai FROM thanh_toan WHERE tham_chieu_loai='DON_HANG' AND tham_chieu_id=?",String.class,order));
        assertEquals(3,jdbc.queryForObject("SELECT so_luong_ton FROM sach WHERE id=?",Integer.class,book));
    }

    @Test void customerCannotReadAnotherCustomersOrder()throws Exception{
        var owner=customer();var stranger=customer();long book=book(2);
        long order=id(owner.json("POST","/api/orders",order(book,1,UUID.randomUUID().toString())).body());
        assertEquals(404,stranger.json("GET","/api/orders/"+order,null).statusCode());
        assertTrue(stranger.json("GET","/api/orders",null).body().contains("\"totalElements\":0"));
    }

    @Test void concurrentCheckoutCannotOversell()throws Exception{
        var first=customer();var second=customer();long book=book(1);var start=new CountDownLatch(1);var pool=Executors.newFixedThreadPool(2);
        try{
            var a=pool.submit(()->{start.await();return first.json("POST","/api/orders",order(book,1,UUID.randomUUID().toString())).statusCode();});
            var b=pool.submit(()->{start.await();return second.json("POST","/api/orders",order(book,1,UUID.randomUUID().toString())).statusCode();});
            start.countDown();var statuses=new ArrayList<>(List.of(a.get(15,TimeUnit.SECONDS),b.get(15,TimeUnit.SECONDS)));Collections.sort(statuses);
            assertEquals(List.of(201,409),statuses);assertEquals(0,jdbc.queryForObject("SELECT so_luong_ton FROM sach WHERE id=?",Integer.class,book));
        }finally{pool.shutdownNow();}
    }
}
