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

@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT,properties={
        "QLNS_VIETQR_BANK_BIN=970436","QLNS_VIETQR_ACCOUNT_NUMBER=123456789",
        "QLNS_VIETQR_ACCOUNT_NAME=NHOM 14"})
@ActiveProfiles("demo")
class MembershipPaymentIntegrationTests {
    @Value("${local.server.port}") int port;
    @Autowired JdbcTemplate jdbc;
    private final List<String> accounts=new ArrayList<>();

    class Session {
        final HttpClient client=HttpClient.newBuilder().cookieHandler(new CookieManager(null,CookiePolicy.ACCEPT_ALL)).build();
        HttpResponse<String> request(String method,String path,String body,String type)throws Exception {
            var builder=HttpRequest.newBuilder(URI.create("http://localhost:"+port+path)).header("Content-Type",type);
            if(!method.equals("GET")) {
                var csrf=client.send(HttpRequest.newBuilder(URI.create("http://localhost:"+port+"/api/auth/csrf")).GET().build(),HttpResponse.BodyHandlers.ofString());
                var match=Pattern.compile("\"token\":\"([^\"]+)\"").matcher(csrf.body());assertTrue(match.find(),csrf.body());
                builder.header("X-CSRF-TOKEN",match.group(1));
            }
            return client.send(builder.method(method,body==null?HttpRequest.BodyPublishers.noBody():HttpRequest.BodyPublishers.ofString(body)).build(),HttpResponse.BodyHandlers.ofString());
        }
        HttpResponse<String> json(String method,String path,String body)throws Exception{return request(method,path,body,"application/json");}
        void login(String username,String password)throws Exception {
            var result=request("POST","/api/auth/login","username="+URLEncoder.encode(username,StandardCharsets.UTF_8)+"&password="+URLEncoder.encode(password,StandardCharsets.UTF_8),"application/x-www-form-urlencoded");
            assertEquals(200,result.statusCode(),result.body());
        }
    }

    @AfterEach void cleanup() {
        for(String username:accounts) {
            String customer="SELECT k.id FROM khach_hang k JOIN tai_khoan t ON t.id=k.tai_khoan_id WHERE t.ten_dang_nhap=?";
            jdbc.update("DELETE FROM thanh_toan WHERE khach_hang_id IN("+customer+")",username);
            jdbc.update("DELETE FROM giao_the WHERE the_thanh_vien_id IN(SELECT th.id FROM the_thanh_vien th JOIN hoi_vien h ON h.id=th.hoi_vien_id WHERE h.khach_hang_id IN("+customer+"))",username);
            jdbc.update("DELETE FROM the_thanh_vien WHERE hoi_vien_id IN(SELECT h.id FROM hoi_vien h WHERE h.khach_hang_id IN("+customer+"))",username);
            jdbc.update("DELETE FROM hoi_vien WHERE khach_hang_id IN("+customer+")",username);
            jdbc.update("DELETE FROM khach_hang WHERE tai_khoan_id IN(SELECT id FROM tai_khoan WHERE ten_dang_nhap=?)",username);
            jdbc.update("DELETE FROM tai_khoan WHERE ten_dang_nhap=?",username);
        }
    }

    Session customer()throws Exception {
        String username="member_"+UUID.randomUUID().toString().replace("-","").substring(0,12);accounts.add(username);Session session=new Session();
        assertEquals(201,session.json("POST","/api/auth/register","{\"username\":\""+username+"\",\"password\":\"Password123\",\"email\":\""+username+"@example.com\",\"hoTen\":\"Khách hội viên\"}").statusCode());
        session.login(username,"Password123");return session;
    }
    Session admin()throws Exception {Session session=new Session();session.login("admin","Admin@123456");return session;}
    Session staff()throws Exception {
        String username="staff_member_"+UUID.randomUUID().toString().replace("-","").substring(0,10);accounts.add(username);
        Session admin=admin();var created=admin.json("POST","/api/accounts","{\"username\":\""+username+"\",\"password\":\"Password123\",\"email\":\""+username+"@example.com\"}");
        assertEquals(201,created.statusCode(),created.body());Session staff=new Session();staff.login(username,"Password123");return staff;
    }
    String pickup(String payment){return "{\"phuongThucNhan\":\"TAI_QUAY\",\"phuongThucThanhToan\":\""+payment+"\"}";}
    long field(String json,String name){var match=Pattern.compile("\""+name+"\":(\\d+)").matcher(json);assertTrue(match.find(),json);return Long.parseLong(match.group(1));}
    long paymentId(String json){var match=Pattern.compile("\"payment\":\\{\"id\":(\\d+)").matcher(json);assertTrue(match.find(),json);return Long.parseLong(match.group(1));}

    @Test void customerRegistersOnePendingVietQrMembershipAndCannotSelfReconcile()throws Exception {
        Session customer=customer();var created=customer.json("POST","/api/memberships",pickup("VIETQR"));
        assertEquals(201,created.statusCode(),created.body());
        assertTrue(created.body().contains("\"phiThanhVien\":50000.00"),created.body());
        assertTrue(created.body().contains("\"trangThai\":\"CHO_THANH_TOAN\""),created.body());
        assertTrue(created.body().contains("img.vietqr.io"),created.body());
        assertTrue(created.body().contains("amount=50000.00"),created.body());
        long payment=paymentId(created.body());
        assertEquals(409,customer.json("POST","/api/memberships",pickup("VIETQR")).statusCode());
        assertEquals(403,customer.json("GET","/api/memberships",null).statusCode());
        assertEquals(403,customer.json("PATCH","/api/payments/"+payment+"/reconcile","{\"maGiaoDichNhaCungCap\":\"BANK-SELF\"}").statusCode());
        assertEquals(1,jdbc.queryForObject("SELECT COUNT(*) FROM hoi_vien",Integer.class));
        assertEquals("CHO_DOI_SOAT",jdbc.queryForObject("SELECT trang_thai FROM thanh_toan WHERE id=?",String.class,payment));
    }

    @Test void staffReconciliationActivatesOneYearMembershipAndCardLifecycle()throws Exception {
        Session customer=customer(),admin=staff();var created=customer.json("POST","/api/memberships",pickup("VIETQR"));
        long membership=field(created.body(),"id"),payment=paymentId(created.body());
        assertEquals(409,admin.json("PATCH","/api/memberships/"+membership+"/card","{\"trangThai\":\"CHO_NHAN\"}").statusCode());
        String reference="BANK-"+UUID.randomUUID();
        var reconciled=admin.json("PATCH","/api/payments/"+payment+"/reconcile","{\"maGiaoDichNhaCungCap\":\""+reference+"\",\"ghiChu\":\"Đã kiểm tra sao kê\"}");
        assertEquals(200,reconciled.statusCode(),reconciled.body());
        assertEquals("DANG_HOAT_DONG",jdbc.queryForObject("SELECT trang_thai FROM hoi_vien WHERE id=?",String.class,membership));
        LocalDate start=LocalDate.parse(jdbc.queryForObject("SELECT ngay_bat_dau FROM hoi_vien WHERE id=?",String.class,membership));
        LocalDate end=LocalDate.parse(jdbc.queryForObject("SELECT ngay_het_han FROM hoi_vien WHERE id=?",String.class,membership));
        assertEquals(start.plusYears(1),end);
        assertEquals(200,admin.json("PATCH","/api/memberships/"+membership+"/card","{\"trangThai\":\"CHO_NHAN\"}").statusCode());
        assertEquals(200,admin.json("PATCH","/api/memberships/"+membership+"/card","{\"trangThai\":\"DA_KICH_HOAT\"}").statusCode());
        assertTrue(customer.json("GET","/api/memberships/me",null).body().contains("DA_KICH_HOAT"));
        assertEquals(200,admin.json("PATCH","/api/payments/"+payment+"/reconcile","{\"maGiaoDichNhaCungCap\":\""+reference+"\"}").statusCode());
    }

    @Test void sameBankReferenceCannotActivateTwoMemberships()throws Exception {
        Session first=customer(),second=customer(),admin=admin();
        long firstPayment=paymentId(first.json("POST","/api/memberships",pickup("VIETQR")).body());
        long secondPayment=paymentId(second.json("POST","/api/memberships",pickup("VIETQR")).body());
        String body="{\"maGiaoDichNhaCungCap\":\"BANK-ONE-TRANSFER\"}";
        assertEquals(200,admin.json("PATCH","/api/payments/"+firstPayment+"/reconcile",body).statusCode());
        assertEquals(409,admin.json("PATCH","/api/payments/"+secondPayment+"/reconcile",body).statusCode());
        assertEquals("CHO_DOI_SOAT",jdbc.queryForObject("SELECT trang_thai FROM thanh_toan WHERE id=?",String.class,secondPayment));
    }

    @Test void shippingRequiresAddressAndStaffCanRegisterCashAtCounter()throws Exception {
        Session customer=customer(),admin=admin();
        assertEquals(400,customer.json("POST","/api/memberships","{\"phuongThucNhan\":\"GIAO_HANG\",\"phuongThucThanhToan\":\"VIETQR\"}").statusCode());
        var shipped=customer.json("POST","/api/memberships","{\"phuongThucNhan\":\"GIAO_HANG\",\"phuongThucThanhToan\":\"VIETQR\",\"tenNguoiNhan\":\"Nguyễn Văn A\",\"soDienThoai\":\"0901234567\",\"diaChi\":\"TP.HCM\"}");
        assertEquals(201,shipped.statusCode(),shipped.body());assertTrue(shipped.body().contains("\"trangThai\":\"CHO_GIAO\""));

        Session counterCustomer=customer();String username=accounts.getLast();
        long customerId=jdbc.queryForObject("SELECT k.id FROM khach_hang k JOIN tai_khoan t ON t.id=k.tai_khoan_id WHERE t.ten_dang_nhap=?",Long.class,username);
        var counter=admin.json("POST","/api/memberships/customers/"+customerId,pickup("TIEN_MAT"));
        assertEquals(201,counter.statusCode(),counter.body());assertTrue(counter.body().contains("\"phuongThuc\":\"TIEN_MAT\""));
        assertEquals(200,counterCustomer.json("GET","/api/memberships/me",null).statusCode());
    }
}
