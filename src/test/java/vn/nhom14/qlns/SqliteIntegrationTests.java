package vn.nhom14.qlns;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.mock.web.MockHttpServletRequest;
import vn.nhom14.qlns.controller.PasswordController;
import vn.nhom14.qlns.dto.OrderRequest;
import vn.nhom14.qlns.service.OrderService;

@ActiveProfiles(value={"sqlite","test"},inheritProfiles=false)
class SqliteIntegrationTests extends WeekTwoIntegrationTests {
 @Autowired OrderService orders;
 @Autowired PasswordController passwordController;
 @Autowired PasswordEncoder passwordEncoder;
 private static final String FILE="./target/sqlite-test-"+UUID.randomUUID()+".sqlite";
 @DynamicPropertySource static void sqlite(DynamicPropertyRegistry registry){registry.add("spring.datasource.url",()->"jdbc:sqlite:"+FILE+"?foreign_keys=on&busy_timeout=10000");}
 @Test void usesSqliteWithForeignKeys(){
  assertNotNull(jdbc.queryForObject("SELECT sqlite_version()",String.class));
  assertEquals(1,jdbc.queryForObject("PRAGMA foreign_keys",Integer.class));
  assertEquals(40,jdbc.queryForObject("SELECT COUNT(*) FROM sach",Integer.class));
  assertEquals(1,jdbc.queryForObject("SELECT COUNT(*) FROM schema_migration WHERE version=2",Integer.class));
 }
 @Test void sqliteCheckoutIsIdempotentAndCancellationRestoresStock(){
  var auth=new UsernamePasswordAuthenticationToken("bandoc","",List.of(new SimpleGrantedAuthority("ROLE_KHACH_HANG")));
  int before=jdbc.queryForObject("SELECT so_luong_ton FROM sach WHERE id=1",Integer.class);
  String token=UUID.randomUUID().toString();
  var request=new OrderRequest("Bạn đọc demo","0901234567","Thành phố Hồ Chí Minh",null,token,List.of(new OrderRequest.Item(1L,1)));
  var first=orders.checkout(auth,request);var replay=orders.checkout(auth,request);
  assertEquals(first.id(),replay.id());assertEquals(before-1,jdbc.queryForObject("SELECT so_luong_ton FROM sach WHERE id=1",Integer.class));
  orders.changeStatus(auth,first.id(),"DA_HUY");orders.changeStatus(auth,first.id(),"DA_HUY");
  assertEquals(before,jdbc.queryForObject("SELECT so_luong_ton FROM sach WHERE id=1",Integer.class));
 }
 @Test void sqliteCustomerCanChangePassword(){
  String username="sqlite_password_"+UUID.randomUUID().toString().replace("-","").substring(0,8);
  jdbc.update("INSERT INTO tai_khoan(ten_dang_nhap,mat_khau_hash,email,vai_tro_id) SELECT ?,?,?,id FROM vai_tro WHERE ma_vai_tro='KHACH_HANG'",
          username,passwordEncoder.encode("Password123"),username+"@example.com");
  try{
   var auth=new UsernamePasswordAuthenticationToken(username,"",List.of(new SimpleGrantedAuthority("ROLE_KHACH_HANG")));
   var result=passwordController.change(auth,new PasswordController.PasswordChange("Password123","NewPassword123"),new MockHttpServletRequest());
   assertEquals("Đã đổi mật khẩu. Vui lòng đăng nhập lại.",result.get("message"));
   String hash=jdbc.queryForObject("SELECT mat_khau_hash FROM tai_khoan WHERE ten_dang_nhap=?",String.class,username);
   assertTrue(passwordEncoder.matches("NewPassword123",hash));
   assertFalse(passwordEncoder.matches("Password123",hash));
  }finally{jdbc.update("DELETE FROM tai_khoan WHERE ten_dang_nhap=?",username);}
 }
}
