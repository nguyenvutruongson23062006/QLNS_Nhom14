package vn.nhom14.qlns;

import java.net.*;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
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

@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("demo")
class RentalIntegrationTests {
    @Value("${local.server.port}") int port;
    @Autowired JdbcTemplate jdbc;
    final List<String> accounts=new ArrayList<>();final List<Long> books=new ArrayList<>();

    class Session {
        final HttpClient client=HttpClient.newBuilder().cookieHandler(new CookieManager(null,CookiePolicy.ACCEPT_ALL)).build();
        HttpResponse<String> json(String method,String path,String body)throws Exception {
            var builder=HttpRequest.newBuilder(URI.create("http://localhost:"+port+path)).header("Content-Type","application/json");
            if(!method.equals("GET")){var csrf=client.send(HttpRequest.newBuilder(URI.create("http://localhost:"+port+"/api/auth/csrf")).GET().build(),HttpResponse.BodyHandlers.ofString());var match=Pattern.compile("\"token\":\"([^\"]+)\"").matcher(csrf.body());assertTrue(match.find(),csrf.body());builder.header("X-CSRF-TOKEN",match.group(1));}
            return client.send(builder.method(method,body==null?HttpRequest.BodyPublishers.noBody():HttpRequest.BodyPublishers.ofString(body)).build(),HttpResponse.BodyHandlers.ofString());
        }
        void login(String username,String password)throws Exception {var csrf=json("GET","/api/auth/csrf",null);var match=Pattern.compile("\"token\":\"([^\"]+)\"").matcher(csrf.body());assertTrue(match.find());var request=HttpRequest.newBuilder(URI.create("http://localhost:"+port+"/api/auth/login")).header("Content-Type","application/x-www-form-urlencoded").header("X-CSRF-TOKEN",match.group(1)).POST(HttpRequest.BodyPublishers.ofString("username="+URLEncoder.encode(username,StandardCharsets.UTF_8)+"&password="+URLEncoder.encode(password,StandardCharsets.UTF_8))).build();assertEquals(200,client.send(request,HttpResponse.BodyHandlers.ofString()).statusCode());}
    }
    record Customer(Session session,long id){}

    @AfterEach void cleanup(){
        for(String username:accounts){String customer="SELECT k.id FROM khach_hang k JOIN tai_khoan t ON t.id=k.tai_khoan_id WHERE t.ten_dang_nhap=?";
            jdbc.update("DELETE FROM thanh_toan WHERE khach_hang_id IN("+customer+")",username);
            jdbc.update("DELETE FROM nhat_ky_thue WHERE phieu_thue_id IN(SELECT p.id FROM phieu_thue p WHERE p.khach_hang_id IN("+customer+"))",username);
            jdbc.update("DELETE FROM chi_tiet_phieu_thue WHERE phieu_thue_id IN(SELECT p.id FROM phieu_thue p WHERE p.khach_hang_id IN("+customer+"))",username);
            jdbc.update("DELETE FROM phieu_thue WHERE khach_hang_id IN("+customer+")",username);
            jdbc.update("DELETE FROM giao_the WHERE the_thanh_vien_id IN(SELECT th.id FROM the_thanh_vien th JOIN hoi_vien h ON h.id=th.hoi_vien_id WHERE h.khach_hang_id IN("+customer+"))",username);
            jdbc.update("DELETE FROM the_thanh_vien WHERE hoi_vien_id IN(SELECT h.id FROM hoi_vien h WHERE h.khach_hang_id IN("+customer+"))",username);
            jdbc.update("DELETE FROM hoi_vien WHERE khach_hang_id IN("+customer+")",username);
            jdbc.update("DELETE FROM khach_hang WHERE tai_khoan_id IN(SELECT id FROM tai_khoan WHERE ten_dang_nhap=?)",username);jdbc.update("DELETE FROM tai_khoan WHERE ten_dang_nhap=?",username);
        }
        for(Long book:books){jdbc.update("DELETE FROM ban_sao_sach WHERE sach_id=?",book);jdbc.update("DELETE FROM cau_hinh_thue_sach WHERE sach_id=?",book);jdbc.update("DELETE FROM sach WHERE id=?",book);}
    }

    Session admin()throws Exception {Session session=new Session();session.login("admin","Admin@123456");return session;}
    Customer customer(boolean member)throws Exception {String username="renter_"+UUID.randomUUID().toString().replace("-","").substring(0,12);accounts.add(username);Session session=new Session();assertEquals(201,session.json("POST","/api/auth/register","{\"username\":\""+username+"\",\"password\":\"Password123\",\"email\":\""+username+"@example.com\",\"hoTen\":\"Khách thuê\"}").statusCode());long id=jdbc.queryForObject("SELECT k.id FROM khach_hang k JOIN tai_khoan t ON t.id=k.tai_khoan_id WHERE t.ten_dang_nhap=?",Long.class,username);if(member){String suffix=UUID.randomUUID().toString().replace("-","");jdbc.update("INSERT INTO hoi_vien(khach_hang_id,ma_hoi_vien,ngay_bat_dau,ngay_het_han,trang_thai) VALUES(?,?,?,?,'DANG_HOAT_DONG')",id,"HV"+suffix.substring(0,20),LocalDate.now(),LocalDate.now().plusYears(1));long membership=jdbc.queryForObject("SELECT id FROM hoi_vien WHERE khach_hang_id=?",Long.class,id);jdbc.update("INSERT INTO the_thanh_vien(hoi_vien_id,ma_the,token_quet,phuong_thuc_nhan,trang_thai) VALUES(?,?,?,'TAI_QUAY','DA_KICH_HOAT')",membership,"THE"+suffix.substring(0,24),UUID.randomUUID().toString());}session.login(username,"Password123");return new Customer(session,id);}
    long configuredBook(Session admin,int copies)throws Exception {String code="RENT"+UUID.randomUUID().toString().replace("-","").substring(0,10);jdbc.update("INSERT INTO sach(ma_sach,ten_sach,tac_gia,danh_muc_id,nha_xuat_ban_id,gia_ban,so_luong_ton) VALUES(?,?,?,1,1,100000,7)",code,"Sách thuê "+code,"Nhóm 14");long id=jdbc.queryForObject("SELECT id FROM sach WHERE ma_sach=?",Long.class,code);books.add(id);var config=admin.json("PUT","/api/rental-catalog/"+id,"{\"choThue\":true,\"giaThueNgay\":2000,\"tienCoc\":50000,\"soNgayMacDinh\":14,\"soLanGiaHan\":1,\"phiTreNgay\":5000}");assertEquals(200,config.statusCode(),config.body());assertEquals(200,admin.json("POST","/api/rental-catalog/"+id+"/copies","{\"soLuong\":"+copies+",\"tinhTrang\":\"MOI\"}").statusCode());return id;}
    String rental(long book,String token){return "{\"clientRequestId\":\""+token+"\",\"items\":[{\"sachId\":"+book+",\"soLuong\":1}],\"phuongThucNhan\":\"TAI_QUAY\",\"phuongThucThanhToan\":\"VIETQR\"}";}
    long field(String json,String field){var match=Pattern.compile("\""+field+"\":(\\d+)").matcher(json);assertTrue(match.find(),json);return Long.parseLong(match.group(1));}
    long paymentId(String json){var match=Pattern.compile("\"payment\":\\{\"id\":(\\d+)").matcher(json);assertTrue(match.find(),json);return Long.parseLong(match.group(1));}

    @Test void rentalRequiresActiveCardAndCatalogDoesNotChangeSaleStock()throws Exception {Session admin=admin();long book=configuredBook(admin,1);Customer noMember=customer(false);assertEquals(409,noMember.session().json("POST","/api/rentals",rental(book,UUID.randomUUID().toString())).statusCode());assertEquals(7,jdbc.queryForObject("SELECT so_luong_ton FROM sach WHERE id=?",Integer.class,book));var catalog=noMember.session().json("GET","/api/rental-catalog",null);assertEquals(200,catalog.statusCode());assertTrue(catalog.body().contains("\"soBanSaoSanSang\":1"));}

    @Test void createIsIdempotentConflictSafePrivateAndCancelReleasesCopy()throws Exception {Session admin=admin();long book=configuredBook(admin,1);Customer owner=customer(true),stranger=customer(true);String token=UUID.randomUUID().toString(),body=rental(book,token);var first=owner.session().json("POST","/api/rentals",body);assertEquals(201,first.statusCode(),first.body());long rental=field(first.body(),"id");assertEquals(rental,field(owner.session().json("POST","/api/rentals",body).body(),"id"));String changed=body.replace("\"TAI_QUAY\"","\"GIAO_HANG\"");assertEquals(409,owner.session().json("POST","/api/rentals",changed).statusCode());assertEquals(404,stranger.session().json("GET","/api/rentals/"+rental,null).statusCode());assertEquals("DA_GIU",jdbc.queryForObject("SELECT trang_thai FROM ban_sao_sach WHERE sach_id=?",String.class,book));assertEquals(200,owner.session().json("PATCH","/api/rentals/"+rental+"/cancel",null).statusCode());assertEquals("SAN_SANG",jdbc.queryForObject("SELECT trang_thai FROM ban_sao_sach WHERE sach_id=?",String.class,book));assertEquals(7,jdbc.queryForObject("SELECT so_luong_ton FROM sach WHERE id=?",Integer.class,book));}

    @Test void paymentHandoverRenewalAndReturnLifecycleEnforceTransitions()throws Exception {Session admin=admin();long book=configuredBook(admin,1);Customer customer=customer(true);var created=customer.session().json("POST","/api/rentals",rental(book,UUID.randomUUID().toString()));long rental=field(created.body(),"id"),payment=paymentId(created.body()),copy=field(created.body(),"banSaoId");assertTrue(admin.json("PATCH","/api/rentals/"+rental+"/approve",null).body().contains("CHO_THANH_TOAN"));assertEquals(409,admin.json("PATCH","/api/rentals/"+rental+"/handover",null).statusCode());assertEquals(200,admin.json("PATCH","/api/payments/"+payment+"/reconcile","{\"maGiaoDichNhaCungCap\":\"RENT-PAY-"+UUID.randomUUID()+"\"}").statusCode());assertEquals(200,admin.json("PATCH","/api/rentals/"+rental+"/handover",null).statusCode());assertEquals("DANG_THUE",jdbc.queryForObject("SELECT trang_thai FROM ban_sao_sach WHERE id=?",String.class,copy));String dueBefore=jdbc.queryForObject("SELECT ngay_hen_tra FROM chi_tiet_phieu_thue WHERE phieu_thue_id=?",String.class,rental);var renewed=customer.session().json("POST","/api/rentals/"+rental+"/renew","{\"soNgay\":7,\"phuongThucThanhToan\":\"VIETQR\"}");assertEquals(200,renewed.statusCode(),renewed.body());assertEquals(dueBefore,jdbc.queryForObject("SELECT ngay_hen_tra FROM chi_tiet_phieu_thue WHERE phieu_thue_id=?",String.class,rental));assertEquals(409,customer.session().json("POST","/api/rentals/"+rental+"/renew","{\"soNgay\":7,\"phuongThucThanhToan\":\"VIETQR\"}").statusCode());long renewPayment=jdbc.queryForObject("SELECT id FROM thanh_toan WHERE tham_chieu_loai='GIA_HAN' AND tham_chieu_id=?",Long.class,rental);assertEquals(200,admin.json("PATCH","/api/payments/"+renewPayment+"/reconcile","{\"maGiaoDichNhaCungCap\":\"RENEW-"+UUID.randomUUID()+"\"}").statusCode());assertEquals(LocalDate.parse(dueBefore).plusDays(7).toString(),jdbc.queryForObject("SELECT ngay_hen_tra FROM chi_tiet_phieu_thue WHERE phieu_thue_id=?",String.class,rental));assertEquals(409,customer.session().json("POST","/api/rentals/"+rental+"/renew","{\"soNgay\":1,\"phuongThucThanhToan\":\"TIEN_MAT\"}").statusCode());jdbc.update("UPDATE chi_tiet_phieu_thue SET ngay_hen_tra=? WHERE phieu_thue_id=?",LocalDate.now().minusDays(2),rental);var returned=admin.json("POST","/api/rentals/"+rental+"/return","{\"items\":[{\"banSaoId\":"+copy+",\"tinhTrangSau\":\"HU_HONG\",\"phiHuHong\":10000}]}");assertEquals(200,returned.statusCode(),returned.body());assertTrue(returned.body().contains("\"tongPhiPhat\":20000.00"),returned.body());assertEquals("BAO_TRI",jdbc.queryForObject("SELECT trang_thai FROM ban_sao_sach WHERE id=?",String.class,copy));assertEquals(1,jdbc.queryForObject("SELECT COUNT(*) FROM thanh_toan WHERE tham_chieu_loai='TRA_PHIEU_THUE' AND tham_chieu_id=?",Integer.class,rental));}

    @Test void counterAndOnlineUseSameAllocationAndOneCopyCannotBeDoubleHeld()throws Exception {Session admin=admin();long counterBook=configuredBook(admin,1),book=configuredBook(admin,1);Customer first=customer(true),second=customer(true);var counter=admin.json("POST","/api/rentals/customers/"+second.id(),rental(counterBook,UUID.randomUUID().toString()));assertEquals(201,counter.statusCode(),counter.body());assertTrue(counter.body().contains("\"kenhTao\":\"TAI_QUAY\""));var start=new CountDownLatch(1);var pool=Executors.newFixedThreadPool(2);try{var a=pool.submit(()->{start.await();return first.session().json("POST","/api/rentals",rental(book,UUID.randomUUID().toString())).statusCode();});var b=pool.submit(()->{start.await();return second.session().json("POST","/api/rentals",rental(book,UUID.randomUUID().toString())).statusCode();});start.countDown();var statuses=new ArrayList<>(List.of(a.get(15,TimeUnit.SECONDS),b.get(15,TimeUnit.SECONDS)));Collections.sort(statuses);assertEquals(List.of(201,409),statuses);
            var failedCounter=admin.json("POST","/api/rentals/customers/"+second.id(),rental(book,UUID.randomUUID().toString()));assertEquals(409,failedCounter.statusCode());assertEquals(1,jdbc.queryForObject("SELECT COUNT(*) FROM ban_sao_sach WHERE sach_id=? AND trang_thai='DA_GIU'",Integer.class,book));
        }finally{pool.shutdownNow();}}
}
