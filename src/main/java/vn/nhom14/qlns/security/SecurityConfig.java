package vn.nhom14.qlns.security;

import java.util.List;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.http.HttpMethod;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;

@Configuration
public class SecurityConfig {
    @Bean PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    @Bean UserDetailsService users(JdbcTemplate jdbc) {
        return username -> {
            List<UserDetails> rows = jdbc.query("""
                    SELECT t.ten_dang_nhap,t.mat_khau_hash,t.trang_thai,v.ma_vai_tro
                    FROM tai_khoan t JOIN vai_tro v ON t.vai_tro_id=v.id
                    WHERE t.ten_dang_nhap=?
                    """, (rs, row) -> new AccountPrincipal(rs.getString(1), rs.getString(2),
                    rs.getBoolean(3), rs.getString(4)), username);
            if (rows.isEmpty()) throw new UsernameNotFoundException("Không tìm thấy tài khoản");
            return rows.getFirst();
        };
    }

    @Bean SecurityFilterChain security(HttpSecurity http, JdbcTemplate jdbc) throws Exception {
        http.authorizeHttpRequests(a -> a
                .requestMatchers("/", "/index.html", "/sach.html", "/chi-tiet.html", "/thue-sach.html", "/sach-cu.html",
                        "/dang-nhap.html", "/admin-login.html", "/dang-ky.html",
                        "/css/**", "/js/**", "/images/**", "/error").permitAll()
                .requestMatchers("/quan-tri.html", "/khach-hang.html").hasAnyRole("ADMIN", "NHAN_VIEN")
                .requestMatchers("/gio-hang.html", "/don-hang.html", "/ca-nhan.html", "/yeu-thich.html")
                    .hasRole("KHACH_HANG")
                .requestMatchers(HttpMethod.GET, "/api/system/status", "/api/auth/csrf", "/api/auth/me").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/auth/register", "/api/auth/login").permitAll()
                .requestMatchers("/api/accounts/**").hasRole("ADMIN")
                .requestMatchers("/api/payments/**").hasAnyRole("ADMIN", "NHAN_VIEN")
                .requestMatchers("/api/memberships/**").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/rental-catalog/**").permitAll()
                .requestMatchers("/api/rental-catalog/**").hasAnyRole("ADMIN", "NHAN_VIEN")
                .requestMatchers("/api/rentals/**").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/used-books").permitAll()
                .requestMatchers("/api/used-books/manage", "/api/used-books/candidates").hasAnyRole("ADMIN", "NHAN_VIEN")
                .requestMatchers(HttpMethod.POST, "/api/used-books/orders").hasRole("KHACH_HANG")
                .requestMatchers("/api/used-books/**").hasAnyRole("ADMIN", "NHAN_VIEN")
                .requestMatchers("/api/ho-so/**", "/api/yeu-thich/**", "/api/anh/dai-dien").hasRole("KHACH_HANG")
                .requestMatchers("/api/anh/sach").hasAnyRole("ADMIN", "NHAN_VIEN")
                .requestMatchers("/api/orders/**").authenticated()
                .requestMatchers("/api/khach-hang/**").hasAnyRole("ADMIN", "NHAN_VIEN")
                .requestMatchers(HttpMethod.GET, "/api/sach/**", "/api/danh-muc/**", "/api/nha-xuat-ban/**").permitAll()
                .requestMatchers("/api/sach/**").hasAnyRole("ADMIN", "NHAN_VIEN")
                .requestMatchers("/api/danh-muc/**", "/api/nha-xuat-ban/**").hasRole("ADMIN")
                .requestMatchers("/api/auth/**").authenticated()
                .anyRequest().denyAll())
            .requestCache(cache -> cache.disable())
            .formLogin(f -> f.loginPage("/dang-nhap.html").loginProcessingUrl("/api/auth/login")
                    .successHandler((req, res, auth) -> {
                        res.setContentType("application/json;charset=UTF-8");
                        res.getWriter().write("{\"role\":\"" + role(auth) + "\"}");
                    })
                    .failureHandler((req, res, ex) -> {
                        res.setStatus(401);
                        res.setContentType("application/json;charset=UTF-8");
                        res.getWriter().write("{\"message\":\"Tên đăng nhập hoặc mật khẩu không đúng\"}");
                    }))
            .logout(l -> l.logoutUrl("/api/auth/logout")
                    .logoutSuccessHandler((req, res, auth) -> res.setStatus(204)))
            .exceptionHandling(e -> e
                .authenticationEntryPoint((req, res, ex) -> {
                    if (req.getRequestURI().startsWith("/api/")) res.sendError(401);
                    else res.sendRedirect("/dang-nhap.html");
                })
                .accessDeniedHandler((req, res, ex) -> {
                    if (req.getRequestURI().startsWith("/api/")) res.sendError(403);
                    else res.sendRedirect("/");
                })
            )
            .addFilterBefore(new AccountSessionFilter(jdbc), AuthorizationFilter.class);
        return http.build();
    }

    public static String role(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) return "GUEST";
        return auth.getAuthorities().stream().map(a -> a.getAuthority())
                .filter(a -> a.startsWith("ROLE_")).map(a -> a.substring(5))
                .findFirst().orElse("GUEST");
    }

    /** Fixed credentials exist only in disposable demo/test databases. */
    @Bean ApplicationRunner demoAccounts(JdbcTemplate jdbc, PasswordEncoder encoder, Environment env) {
        return args -> {
            if (!env.acceptsProfiles(Profiles.of("demo", "test"))) return;
            for (String[] account : new String[][]{
                    {"admin", "Admin@123456", "ADMIN"},
                    {"bandoc", "BanDoc@123456", "KHACH_HANG"}}) {
                if (jdbc.queryForObject("SELECT COUNT(*) FROM tai_khoan WHERE ten_dang_nhap=?",
                        Integer.class, account[0]) == 0) {
                    jdbc.update("""
                            INSERT INTO tai_khoan(ten_dang_nhap,mat_khau_hash,email,vai_tro_id)
                            SELECT ?,?,?,id FROM vai_tro WHERE ma_vai_tro=?
                            """, account[0], encoder.encode(account[1]), account[0] + "@example.com", account[2]);
                }
                if (account[2].equals("KHACH_HANG")) {
                    jdbc.update("""
                            INSERT INTO khach_hang(tai_khoan_id,ho_ten,email)
                            SELECT t.id,'Bạn đọc demo',t.email FROM tai_khoan t
                            WHERE t.ten_dang_nhap=? AND NOT EXISTS(
                                SELECT 1 FROM khach_hang k WHERE k.tai_khoan_id=t.id)
                            """, account[0]);
                    Long khachHangId = jdbc.queryForObject(
                            "SELECT k.id FROM khach_hang k JOIN tai_khoan t ON t.id=k.tai_khoan_id WHERE t.ten_dang_nhap=?",
                            Long.class, account[0]);
                    if (khachHangId != null && jdbc.queryForObject("SELECT COUNT(*) FROM hoi_vien WHERE khach_hang_id=?", Integer.class, khachHangId) == 0) {
                        String suffix = java.util.UUID.randomUUID().toString().replace("-", "");
                        jdbc.update("""
                                INSERT INTO hoi_vien(khach_hang_id,ma_hoi_vien,ngay_bat_dau,ngay_het_han,trang_thai)
                                VALUES(?,?,?,?,'DANG_HOAT_DONG')
                                """, khachHangId, "HV" + suffix.substring(0, 16).toUpperCase(java.util.Locale.ROOT),
                                java.time.LocalDate.now(), java.time.LocalDate.now().plusYears(1));
                        Long hoiVienId = jdbc.queryForObject("SELECT id FROM hoi_vien WHERE khach_hang_id=?", Long.class, khachHangId);
                        jdbc.update("""
                                INSERT INTO the_thanh_vien(hoi_vien_id,ma_the,token_quet,phuong_thuc_nhan,trang_thai,ngay_phat_hanh,ngay_kich_hoat)
                                VALUES(?,?,?,'TAI_QUAY','DA_KICH_HOAT',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP)
                                """, hoiVienId, "THE" + suffix.substring(0, 16).toUpperCase(java.util.Locale.ROOT), java.util.UUID.randomUUID().toString());
                    }
                }
            }
        };
    }
}
