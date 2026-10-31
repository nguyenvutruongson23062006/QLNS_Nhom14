package vn.nhom14.qlns;
import java.net.*;
import java.net.http.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ActiveProfiles;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("demo")
class YeuThichIntegrationTests {
 @Value("${local.server.port}") int port;
 @Autowired JdbcTemplate jdbc;
 HttpClient client(){return HttpClient.newBuilder().cookieHandler(new CookieManager(null,CookiePolicy.ACCEPT_ALL)).build();}
 HttpResponse<String> request(HttpClient c,String method,String path,String body,boolean csrf) throws Exception {
  var builder=HttpRequest.newBuilder(URI.create("http://localhost:"+port+path));
  if(csrf){var r=request(c,"GET","/api/auth/csrf",null,false);var m=java.util.regex.Pattern.compile("\"token\":\"([^\"]+)\"").matcher(r.body());assertTrue(m.find());builder.header("X-CSRF-TOKEN",m.group(1));}
  if(path.equals("/api/auth/login"))builder.header("Content-Type","application/x-www-form-urlencoded");
  return c.send(builder.method(method,body==null?HttpRequest.BodyPublishers.noBody():HttpRequest.BodyPublishers.ofString(body)).build(),HttpResponse.BodyHandlers.ofString());
 }
 HttpClient login(String username,String password) throws Exception {var c=client();assertEquals(200,request(c,"POST","/api/auth/login","username="+username+"&password="+URLEncoder.encode(password,java.nio.charset.StandardCharsets.UTF_8),true).statusCode());return c;}
 @Test void guestCannotAccessFavorites() throws Exception {
  assertEquals(401,request(client(),"GET","/api/yeu-thich",null,false).statusCode());
  assertEquals(401,request(client(),"GET","/api/yeu-thich/the-loai",null,false).statusCode());
 }
 @Test void savesAreUniqueAndStatisticsFollowRemoval() throws Exception {
  var c=login("bandoc","BanDoc@123456");
  var sameCategory=jdbc.queryForList("SELECT id FROM sach WHERE danh_muc_id=(SELECT danh_muc_id FROM sach GROUP BY danh_muc_id HAVING COUNT(*)>=2 ORDER BY danh_muc_id LIMIT 1) ORDER BY id LIMIT 2",Long.class);
  long first=sameCategory.get(0),second=sameCategory.get(1);
  long other=jdbc.queryForObject("SELECT id FROM sach WHERE danh_muc_id<>(SELECT danh_muc_id FROM sach WHERE id=?) ORDER BY id LIMIT 1",Long.class,first);
  try {
   for(long id:new long[]{first,second,other,first})assertEquals(204,request(c,"PUT","/api/yeu-thich/"+id,null,true).statusCode());
   var list=request(c,"GET","/api/yeu-thich?size=2",null,false);assertEquals(200,list.statusCode());assertTrue(list.body().contains("\"totalElements\":3"));assertTrue(list.body().contains("\"totalPages\":2"));
   var stats=request(c,"GET","/api/yeu-thich/the-loai",null,false);assertTrue(stats.body().contains("\"tongSoSach\":3"));assertTrue(stats.body().contains("\"soSach\":2"));
   request(c,"DELETE","/api/yeu-thich/"+second,null,true);
   stats=request(c,"GET","/api/yeu-thich/the-loai",null,false);assertTrue(stats.body().contains("\"tongSoSach\":2"));assertFalse(stats.body().contains("\"soSach\":2"));
   var c2=login("bandoc","BanDoc@123456");assertTrue(request(c2,"GET","/api/yeu-thich/ids",null,false).body().contains(String.valueOf(first)));
  } finally {for(long id:new long[]{first,second,other})request(c,"DELETE","/api/yeu-thich/"+id,null,true);}
 }
 @Test void accountIsolationAndCsrf() throws Exception {
  var reader=login("bandoc","BanDoc@123456");var admin=login("admin","Admin@123456");
  try {assertEquals(403,request(reader,"PUT","/api/yeu-thich/1",null,false).statusCode());
   assertEquals(204,request(reader,"PUT","/api/yeu-thich/1",null,true).statusCode());
   assertEquals(403,request(admin,"GET","/api/yeu-thich/ids",null,false).statusCode());
   assertEquals(403,request(admin,"DELETE","/api/yeu-thich/1",null,true).statusCode());
   assertEquals("[1]",request(reader,"GET","/api/yeu-thich/ids",null,false).body());
  }finally{request(reader,"DELETE","/api/yeu-thich/1",null,true);}
 }
 @Test void rejectsMissingBooksAndBadPagination() throws Exception {
  var c=login("bandoc","BanDoc@123456");
  assertEquals(404,request(c,"PUT","/api/yeu-thich/999999",null,true).statusCode());
  assertEquals(400,request(c,"GET","/api/yeu-thich?page=-1",null,false).statusCode());
  assertEquals(400,request(c,"GET","/api/yeu-thich?size=101",null,false).statusCode());
  assertEquals(204,request(c,"DELETE","/api/yeu-thich/999999",null,true).statusCode());
 }
}
