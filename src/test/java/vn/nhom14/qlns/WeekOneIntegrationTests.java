package vn.nhom14.qlns;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("demo")
class WeekOneIntegrationTests {
    @Value("${local.server.port}") int port;
    @Autowired JdbcTemplate jdbc;

    @Test void schemaHasEightTablesAndSeedData() {
        for (String table : new String[] {"vai_tro", "tai_khoan", "khach_hang", "danh_muc",
                "nha_xuat_ban", "sach", "don_hang", "chi_tiet_don_hang"}) {
            assertNotNull(jdbc.queryForObject("SELECT COUNT(*) FROM " + table, Integer.class));
        }
        assertEquals(3, jdbc.queryForObject("SELECT COUNT(*) FROM vai_tro", Integer.class));
        assertEquals(40, jdbc.queryForObject("SELECT COUNT(*) FROM sach", Integer.class));
        assertEquals(10, jdbc.queryForObject("SELECT COUNT(*) FROM danh_muc", Integer.class));
        assertEquals(8, jdbc.queryForObject("SELECT COUNT(*) FROM nha_xuat_ban", Integer.class));
    }

    @Test void databaseRejectsNegativeStockAndInvalidForeignKey() {
        assertThrows(DataIntegrityViolationException.class,
            () -> jdbc.update("UPDATE sach SET so_luong_ton = -1 WHERE ma_sach = 'S001'"));
        assertThrows(DataIntegrityViolationException.class,
            () -> jdbc.update("UPDATE sach SET danh_muc_id = 99999 WHERE ma_sach = 'S001'"));
    }

    @Test void statusEndpointConnectsToDatabase() throws Exception {
        var response = get("/api/system/status");
        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("\"database\":\"CONNECTED\""));
        assertTrue(response.body().contains("\"week\":2"));
    }

    @Test void homepageIsServed() throws Exception {
        var response = get("/");
        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Quản lý nhà sách"));
    }

    private HttpResponse<String> get(String path) throws Exception {
        return HttpClient.newHttpClient().send(HttpRequest.newBuilder(
            URI.create("http://localhost:" + port + path)).GET().build(), HttpResponse.BodyHandlers.ofString());
    }
}

