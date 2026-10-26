package vn.nhom14.qlns.security;

import java.util.Map;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;

@Configuration
public class SecurityConfig {
 @Bean PasswordEncoder passwordEncoder() { return new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder(); }
 @Bean UserDetailsService users(JdbcTemplate jdbc) {
  return username -> jdbc.query("SELECT t.ten_dang_nhap,t.mat_khau_hash,t.trang_thai,v.ma_vai_tro FROM tai_khoan t JOIN vai_tro v ON t.vai_tro_id=v.id WHERE t.ten_dang_nhap=?", (rs,n)->User.withUsername(rs.getString(1)).password(rs.getString(2)).disabled(!rs.getBoolean(3)).roles(rs.getString(4)).build(),username).stream().findFirst().orElseThrow(()->new UsernameNotFoundException("Không tìm thấy tài khoản"));
 }
 @Bean SecurityFilterChain security(HttpSecurity http) throws Exception {
  http.authorizeHttpRequests(a->a
   .requestMatchers("/api/auth/**","/dang-nhap.html","/admin-login.html","/dang-ky.html","/css/**","/js/**","/images/**","/error","/api/system/status").permitAll()
   .requestMatchers("/api/anh/sach").hasRole("ADMIN")
   .requestMatchers("/quan-tri.html","/khach-hang.html","/api/khach-hang/**").hasRole("ADMIN")
   .requestMatchers(HttpMethod.GET,"/api/sach/**","/api/danh-muc/**","/api/nha-xuat-ban/**","/","/index.html","/sach.html").permitAll()
   .requestMatchers("/api/sach/**","/api/danh-muc/**","/api/nha-xuat-ban/**").hasRole("ADMIN")
   .anyRequest().authenticated())
   .formLogin(f->f.loginPage("/dang-nhap.html").loginProcessingUrl("/api/auth/login")
    .successHandler((req,res,auth)->{res.setContentType("application/json;charset=UTF-8");res.getWriter().write("{\"role\":\""+(auth.getAuthorities().stream().anyMatch(x->x.getAuthority().equals("ROLE_ADMIN"))?"ADMIN":"KHACH_HANG")+"\"}");})
    .failureHandler((req,res,ex)->{res.setStatus(401);res.setContentType("application/json;charset=UTF-8");res.getWriter().write("{\"message\":\"Tên đăng nhập hoặc mật khẩu không đúng\"}");}))
   .logout(l->l.logoutUrl("/api/auth/logout").logoutSuccessHandler((req,res,auth)->res.setStatus(204)))
   .exceptionHandling(e->e.authenticationEntryPoint((req,res,ex)->{if(req.getRequestURI().startsWith("/api/"))res.sendError(401);else res.sendRedirect("/dang-nhap.html");}));
  return http.build();
 }
 @Bean ApplicationRunner demoAccounts(JdbcTemplate jdbc, PasswordEncoder encoder, Environment env) {
  return args->{if(!env.acceptsProfiles(org.springframework.core.env.Profiles.of("demo", "sqlite")))return;
   for(String[] a:new String[][]{{"admin","Admin@123456","1"},{"bandoc","BanDoc@123456","3"}}){
    if(jdbc.queryForObject("SELECT COUNT(*) FROM tai_khoan WHERE ten_dang_nhap=?",Integer.class,a[0])==0)
     jdbc.update("INSERT INTO tai_khoan(ten_dang_nhap,mat_khau_hash,email,vai_tro_id) VALUES (?,?,?,?)",a[0],encoder.encode(a[1]),a[0]+"@example.com",Long.parseLong(a[2]));
   }
  };
 }
}


