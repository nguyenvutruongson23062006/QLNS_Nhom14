package vn.nhom14.qlns.security;

import java.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.filter.OncePerRequestFilter;

/** Revokes existing sessions after a password, role, or enabled-state change. */
final class AccountSessionFilter extends OncePerRequestFilter {
    private final JdbcTemplate jdbc;
    private final SecurityContextLogoutHandler logout = new SecurityContextLogoutHandler();
    AccountSessionFilter(JdbcTemplate jdbc) { this.jdbc = jdbc; }
    @Override protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain chain) throws ServletException, IOException {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            var roles = auth.getAuthorities().stream().map(a -> a.getAuthority())
                    .filter(a -> a.startsWith("ROLE_")).toList();
            var valid = jdbc.query("""
                    SELECT t.mat_khau_hash,t.trang_thai,v.ma_vai_tro
                    FROM tai_khoan t JOIN vai_tro v ON v.id=t.vai_tro_id
                    WHERE t.ten_dang_nhap=?
                    """, (rs, row) -> auth.getPrincipal() instanceof AccountPrincipal principal
                    && rs.getBoolean("trang_thai")
                    && principal.hasCredentialVersion(rs.getString("mat_khau_hash"))
                    && roles.size() == 1
                    && roles.getFirst().equals("ROLE_" + rs.getString("ma_vai_tro")), auth.getName());
            if (valid.isEmpty() || !valid.getFirst()) logout.logout(request, response, auth);
        }
        chain.doFilter(request, response);
    }
}
