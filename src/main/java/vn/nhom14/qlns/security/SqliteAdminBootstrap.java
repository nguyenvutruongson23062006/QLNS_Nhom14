package vn.nhom14.qlns.security;

import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** Creates the first persistent administrator only from an explicit environment secret. */
@Component @Profile({"sqlite","mysql"})
public class SqliteAdminBootstrap implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(SqliteAdminBootstrap.class);
    private final JdbcTemplate jdbc; private final PasswordEncoder encoder; private final Environment environment;
    public SqliteAdminBootstrap(JdbcTemplate jdbc, PasswordEncoder encoder, Environment environment) {
        this.jdbc=jdbc; this.encoder=encoder; this.environment=environment;
    }
    @Override @Transactional(timeout=10) public void run(String... args) {
        if(environment.acceptsProfiles(Profiles.of("test")))return;
        int admins=jdbc.queryForObject("""
                SELECT COUNT(*) FROM tai_khoan t JOIN vai_tro v ON t.vai_tro_id=v.id
                WHERE v.ma_vai_tro='ADMIN'
                """,Integer.class);
        if(admins>0)return;
        String password=environment.getProperty("QLNS_INITIAL_ADMIN_PASSWORD");
        if(password==null||password.isBlank()) {
            log.warn("Chưa có quản trị viên. Đặt QLNS_INITIAL_ADMIN_PASSWORD rồi khởi động lại để tạo admin đầu tiên.");
            return;
        }
        if(password.length()<12||password.length()>64||password.getBytes(StandardCharsets.UTF_8).length>72)
            throw new IllegalStateException("QLNS_INITIAL_ADMIN_PASSWORD phải dài 12-64 ký tự và tối đa 72 byte UTF-8");
        String username=environment.getProperty("QLNS_INITIAL_ADMIN_USERNAME","admin");
        if(!username.matches("[a-zA-Z0-9_]{4,50}"))throw new IllegalStateException("QLNS_INITIAL_ADMIN_USERNAME không hợp lệ");
        jdbc.update("""
                INSERT INTO tai_khoan(ten_dang_nhap,mat_khau_hash,email,vai_tro_id)
                SELECT ?,?,?,id FROM vai_tro WHERE ma_vai_tro='ADMIN'
                """,username,encoder.encode(password),username+"@qlns.local");
        log.info("Đã tạo quản trị viên SQLite ban đầu: {}",username);
    }
}
