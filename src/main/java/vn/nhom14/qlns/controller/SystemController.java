package vn.nhom14.qlns.controller;

import java.util.List;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Kiểm tra ứng dụng và kết nối cơ sở dữ liệu. */
@RestController
@RequestMapping("/api/system")
public class SystemController {
    private final JdbcTemplate jdbc;
    private final Environment environment;

    public SystemController(JdbcTemplate jdbc, Environment environment) {
        this.jdbc = jdbc;
        this.environment = environment;
    }

    @GetMapping("/status")
    public SystemStatus status() {
        jdbc.queryForObject("SELECT 1", Integer.class);
        String[] profiles = environment.getActiveProfiles();
        if (profiles.length == 0) profiles = environment.getDefaultProfiles();
        return new SystemStatus("QLNS Nhóm 14", "UP", "CONNECTED", 2, List.of(profiles));
    }

    public record SystemStatus(String application, String status, String database,
                               int week, List<String> profiles) {}
}
