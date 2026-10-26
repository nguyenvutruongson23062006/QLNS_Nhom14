package vn.nhom14.qlns;
import java.net.*;
import java.net.http.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ActiveProfiles;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("demo")
class HoSoIntegrationTests {
 @Value("${local.server.port}") int port;
 HttpClient client(){return HttpClient.newBuilder().cookieHandler(new CookieManager(null,CookiePolicy.ACCEPT_ALL)).build();}
 HttpResponse<String> request(HttpClient c,String method,String path,String body,boolean csrf) throws Exception {
  var builder=HttpRequest.newBuilder(URI.create("http://localhost:"+port+path));
  if(csrf){var r=request(c,"GET","/api/auth/csrf",null,false);var m=java.util.regex.Pattern.compile("\"token\":\"([^\"]+)\"").matcher(r.body());assertTrue(m.find());builder.header("X-CSRF-TOKEN",m.group(1));}
  if(path.equals("/api/auth/login"))builder.header("Content-Type","application/x-www-form-urlencoded");
  if(body!=null&&!path.equals("/api/auth/login"))builder.header("Content-Type","application/json");
  return c.send(builder.method(method,body==null?HttpRequest.BodyPublishers.noBody():HttpRequest.BodyPublishers.ofString(body)).build(),HttpResponse.BodyHandlers.ofString());
 }
 HttpClient login(String username,String password) throws Exception {var c=client();assertEquals(200,request(c,"POST","/api/auth/login","username="+username+"&password="+URLEncoder.encode(password,java.nio.charset.StandardCharsets.UTF_8),true).statusCode());return c;}

 @Test void profileIsPrivateAndEditable() throws Exception {
  assertEquals(401,request(client(),"GET","/api/ho-so",null,false).statusCode());
  var c=login("bandoc","BanDoc@123456");
  String json="{\"hoTen\":\"Bạn đọc cập nhật\",\"email\":\"bandoc@example.com\",\"soDienThoai\":\"0901234567\",\"diaChi\":\"Địa chỉ thử nghiệm\",\"anhDaiDien\":\"\",\"theLoaiYeuThich\":\"Trinh thám, Khoa học\"}";
  assertEquals(200,request(c,"PUT","/api/ho-so",json,true).statusCode());
  var c2=login("bandoc","BanDoc@123456");var saved=request(c2,"GET","/api/ho-so",null,false);assertTrue(saved.body().contains("Trinh thám, Khoa học"));assertTrue(saved.body().contains("0901234567"));
  var a=login("admin","Admin@123456");assertFalse(request(a,"GET","/api/ho-so",null,false).body().contains("Trinh thám"));
  assertEquals(409,request(c,"PUT","/api/ho-so",json.replace("bandoc@example.com","admin@example.com"),true).statusCode());
  assertTrue(request(c,"GET","/api/ho-so",null,false).body().contains("bandoc@example.com"));
  assertEquals(400,request(c,"PUT","/api/ho-so",json.replace("bandoc@example.com","invalid-email"),true).statusCode());
  assertEquals(403,request(c,"PUT","/api/ho-so",json,false).statusCode());
 }
 @Test void uploadedImagesAreValidatedAndBooksRequireAdmin() throws Exception {
  var c=login("bandoc","BanDoc@123456");
  assertEquals(403,request(c,"POST","/api/anh/sach",null,true).statusCode());
  assertEquals(400,upload(c,"dai-dien","not an image".getBytes()).statusCode());
  var image=new java.awt.image.BufferedImage(8,8,java.awt.image.BufferedImage.TYPE_INT_RGB);var buffer=new java.io.ByteArrayOutputStream();javax.imageio.ImageIO.write(image,"png",buffer);
  var res=upload(c,"dai-dien",buffer.toByteArray());assertEquals(200,res.statusCode());
  var m=java.util.regex.Pattern.compile("\"url\":\"([^\"]+)\"").matcher(res.body());assertTrue(m.find());
  assertEquals(200,request(client(),"GET",m.group(1),null,false).statusCode());
  var admin=login("admin","Admin@123456");assertEquals(200,upload(admin,"sach",buffer.toByteArray()).statusCode());
 }
 HttpResponse<String> upload(HttpClient c,String type,byte[] image) throws Exception {
  var csrf=request(c,"GET","/api/auth/csrf",null,false);var m=java.util.regex.Pattern.compile("\"token\":\"([^\"]+)\"").matcher(csrf.body());assertTrue(m.find());
  String boundary="QlnsBoundary";var bytes=new java.io.ByteArrayOutputStream();bytes.write(("--"+boundary+"\r\nContent-Disposition: form-data; name=\"file\"; filename=\"test.png\"\r\nContent-Type: image/png\r\n\r\n").getBytes());bytes.write(image);bytes.write(("\r\n--"+boundary+"--\r\n").getBytes());
  return c.send(HttpRequest.newBuilder(URI.create("http://localhost:"+port+"/api/anh/"+type)).header("X-CSRF-TOKEN",m.group(1)).header("Content-Type","multipart/form-data; boundary="+boundary).POST(HttpRequest.BodyPublishers.ofByteArray(bytes.toByteArray())).build(),HttpResponse.BodyHandlers.ofString());
 }
}
