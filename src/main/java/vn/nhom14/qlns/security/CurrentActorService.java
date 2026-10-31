package vn.nhom14.qlns.security;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class CurrentActorService {
    private final JdbcTemplate jdbc;
    public CurrentActorService(JdbcTemplate jdbc) { this.jdbc=jdbc; }

    public record Actor(long accountId, String role, Long customerId) {
        public boolean staff() { return role.equals("ADMIN") || role.equals("NHAN_VIEN"); }
        public void requireStaff() {
            if (!staff()) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Chỉ nhân viên được thực hiện thao tác này");
        }
        public long requireCustomer() {
            if (!role.equals("KHACH_HANG") || customerId==null)
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Tài khoản không có hồ sơ khách hàng");
            return customerId;
        }
    }

    public Actor require(Authentication authentication) {
        if (authentication==null || !authentication.isAuthenticated())
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Vui lòng đăng nhập");
        List<Actor> actors=jdbc.query("""
                SELECT t.id,v.ma_vai_tro,k.id AS customer_id FROM tai_khoan t
                JOIN vai_tro v ON v.id=t.vai_tro_id LEFT JOIN khach_hang k ON k.tai_khoan_id=t.id
                WHERE t.ten_dang_nhap=? AND t.trang_thai=TRUE
                """,(result,index)->{
            Number customer=(Number)result.getObject("customer_id");
            return new Actor(result.getLong("id"),result.getString("ma_vai_tro"),customer==null?null:customer.longValue());
        },authentication.getName());
        if (actors.isEmpty()) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Tài khoản không còn hoạt động");
        return actors.getFirst();
    }
}
