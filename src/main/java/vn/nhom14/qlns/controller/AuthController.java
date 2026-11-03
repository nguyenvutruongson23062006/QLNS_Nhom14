package vn.nhom14.qlns.controller;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import vn.nhom14.qlns.security.SecurityConfig;
@RestController
@RequestMapping("/api/auth")
public class AuthController {
 private final JdbcTemplate jdbc; private final PasswordEncoder encoder;
 public AuthController(JdbcTemplate jdbc,PasswordEncoder encoder){this.jdbc=jdbc;this.encoder=encoder;}
 @GetMapping("/csrf") public Map<String,String> csrf(CsrfToken token){return Map.of("token",token.getToken(),"headerName",token.getHeaderName());}
 @GetMapping("/me") public Map<String,String> me(Authentication auth){
  if(auth==null)return Map.of("role","GUEST");
  return Map.of("username",auth.getName(),"role",SecurityConfig.role(auth));
 }
 public record Register(@NotBlank @Pattern(regexp="[a-zA-Z0-9_]{4,50}") String username,@NotBlank @Size(min=8,max=72) String password,@NotBlank @Email @Size(max=150) String email,@NotBlank @Size(max=150) String hoTen){}
 @PostMapping("/register") @ResponseStatus(HttpStatus.CREATED) @Transactional
 public Map<String,String> register(@Valid @RequestBody Register r){
  if(r.password().getBytes(StandardCharsets.UTF_8).length>72)
   throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Mật khẩu không được vượt quá 72 byte UTF-8");
  Integer countUsername=jdbc.queryForObject("SELECT COUNT(*) FROM tai_khoan WHERE ten_dang_nhap=?",Integer.class,r.username());
  if(countUsername!=null&&countUsername>0)
   throw new ResponseStatusException(HttpStatus.CONFLICT,"Tên đăng nhập đã tồn tại. Vui lòng chọn tên khác.");
  Integer countEmail=jdbc.queryForObject("SELECT COUNT(*) FROM tai_khoan WHERE email=?",Integer.class,r.email());
  if(countEmail!=null&&countEmail>0)
   throw new ResponseStatusException(HttpStatus.CONFLICT,"Email đã được sử dụng bởi tài khoản khác.");
  Long role=jdbc.queryForObject("SELECT id FROM vai_tro WHERE ma_vai_tro='KHACH_HANG'",Long.class);
  jdbc.update("INSERT INTO tai_khoan(ten_dang_nhap,mat_khau_hash,email,vai_tro_id) VALUES (?,?,?,?)",r.username(),encoder.encode(r.password()),r.email(),role);
  Long id=jdbc.queryForObject("SELECT id FROM tai_khoan WHERE ten_dang_nhap=?",Long.class,r.username());
  jdbc.update("INSERT INTO khach_hang(tai_khoan_id,ho_ten,email) VALUES (?,?,?)",id,r.hoTen(),r.email());
  return Map.of("message","Đăng ký thành công");
 }
}
