package vn.nhom14.qlns;

import java.net.URI;
import java.net.http.*;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("demo")
class WeekTwoIntegrationTests {
    @Value("${local.server.port}") int port;
    @Autowired JdbcTemplate jdbc;
    private final HttpClient client = HttpClient.newBuilder().cookieHandler(new java.net.CookieManager(null, java.net.CookiePolicy.ACCEPT_ALL)).build();
    private boolean loggedIn;

    record Resource(String route, String table, String column, String body, String updatedBody) {}

    static Stream<Resource> resources() {
        return Stream.of(
            new Resource("danh-muc", "danh_muc", "ten_danh_muc",
                "{\"tenDanhMuc\":\"Danh mục kiểm thử\",\"trangThai\":true}",
                "{\"tenDanhMuc\":\"Danh mục đã sửa\",\"trangThai\":false}"),
            new Resource("nha-xuat-ban", "nha_xuat_ban", "ten_nha_xuat_ban",
                "{\"tenNhaXuatBan\":\"NXB kiểm thử\",\"email\":\"nxb@example.com\",\"trangThai\":true}",
                "{\"tenNhaXuatBan\":\"NXB đã sửa\",\"trangThai\":false}"),
            new Resource("khach-hang", "khach_hang", "ho_ten",
                "{\"hoTen\":\"Khách kiểm thử\",\"soDienThoai\":\"0901234567\",\"email\":\"khach@example.com\"}",
                "{\"hoTen\":\"Khách đã sửa\",\"diaChi\":\"Hà Nội\"}"),
            new Resource("sach", "sach", "ten_sach",
                book("W2TEST", "Sách kiểm thử", 1, "10000.50", "0", "\"978TEST\""),
                book("W2TEST", "Sách đã sửa", 2, "20000.25", "5", "null")));
    }

    static String book(String code, String title, long category, String price, String stock, String isbn) {
        return "{\"maSach\":\"" + code + "\",\"tenSach\":\"" + title
            + "\",\"tacGia\":\"Tác giả\",\"danhMucId\":" + category
            + ",\"nhaXuatBanId\":1,\"giaBan\":" + price + ",\"soLuongTon\":" + stock
            + ",\"isbn\":" + isbn + ",\"trangThai\":true}";
    }

    @ParameterizedTest @MethodSource("resources")
    void completeCrudAndPagination(Resource resource) throws Exception {
        String path = "/api/" + resource.route();
        HttpResponse<String> created = send("POST", path, resource.body());
        assertEquals(201, created.statusCode(), created.body());
        String location = created.headers().firstValue("Location").orElseThrow();
        long id = Long.parseLong(location.substring(location.lastIndexOf('/') + 1));
        try {
            assertEquals(200, send("GET", location, null).statusCode());
            HttpResponse<String> list = send("GET", path + "?page=0&size=1", null);
            assertEquals(200, list.statusCode());
            assertTrue(list.body().contains("\"size\":1"));
            assertTrue(list.body().contains("\"totalElements\":"));
            assertEquals(200, send("PUT", location, resource.updatedBody()).statusCode());
            String value = jdbc.queryForObject("SELECT " + resource.column() + " FROM "
                    + resource.table() + " WHERE id = ?", String.class, id);
            assertTrue(value.contains("đã sửa"), value);
            assertEquals(400, send("PUT", location, "{}").statusCode());
            assertEquals(value, jdbc.queryForObject("SELECT " + resource.column() + " FROM "
                    + resource.table() + " WHERE id = ?", String.class, id));
        } finally {
            assertEquals(204, send("DELETE", location, null).statusCode());
        }
        assertEquals(404, send("GET", location, null).statusCode());
        assertEquals(404, send("DELETE", location, null).statusCode());
    }

    @ParameterizedTest @MethodSource("resources")
    void rejectsBadRequestsAndMissingResources(Resource resource) throws Exception {
        String path = "/api/" + resource.route();
        assertEquals(400, send("POST", path, "{}").statusCode());
        assertEquals(400, send("POST", path, "{broken").statusCode());
        assertEquals(400, send("GET", path + "/abc", null).statusCode());
        for (String query : new String[]{"?page=-1", "?size=0", "?size=101", "?page=abc"}) {
            assertEquals(400, send("GET", path + query, null).statusCode());
        }
        assertEquals(404, send("GET", path + "/99999999", null).statusCode());
        assertEquals(404, send("PUT", path + "/99999999", resource.body()).statusCode());
        assertEquals(404, send("DELETE", path + "/99999999", null).statusCode());
    }

    @Test void duplicateNamesCodesAndIsbnReturnConflict() throws Exception {
        assertEquals(409, send("POST", "/api/danh-muc",
                "{\"tenDanhMuc\":\"Văn học\",\"trangThai\":true}").statusCode());
        assertEquals(409, send("POST", "/api/nha-xuat-ban",
                "{\"tenNhaXuatBan\":\"Nhà xuất bản Trẻ\",\"trangThai\":true}").statusCode());
        assertEquals(409, send("POST", "/api/sach",
                book("S001", "Trùng mã", 1, "0", "0", "null")).statusCode());
        String first = send("POST", "/api/sach",
                book("ISBN-A", "ISBN", 1, "0", "0", "\"SAME-ISBN\""))
                .headers().firstValue("Location").orElseThrow();
        try {
            assertEquals(409, send("POST", "/api/sach",
                    book("ISBN-B", "ISBN", 1, "0", "0", "\"SAME-ISBN\"" )).statusCode());
        } finally {
            assertEquals(204, send("DELETE", first, null).statusCode());
        }
    }

    @Test void rejectsNegativeMoneyStockInvalidYearAndMissingRelations() throws Exception {
        for (String body : new String[]{
                book("BAD", "Test", 1, "-1", "0", "null"),
                book("BAD", "Test", 1, "1.001", "0", "null"),
                book("BAD", "Test", 1, "1", "-1", "null"),
                book("BAD", "Test", 1, "1", "0", "null").replace("\"trangThai\":true", "\"namXuatBan\":999,\"trangThai\":true"),
                book("BAD", "   ", 1, "1", "0", "null")}) {
            assertEquals(400, send("POST", "/api/sach", body).statusCode());
        }
        assertEquals(404, send("POST", "/api/sach",
                book("BAD", "Test", 99999999, "1", "0", "null")).statusCode());
        assertEquals(404, send("POST", "/api/sach",
                book("BAD", "Test", 1, "1", "0", "null").replace("\"nhaXuatBanId\":1", "\"nhaXuatBanId\":99999999")).statusCode());
        assertEquals(0, jdbc.queryForObject("SELECT COUNT(*) FROM sach WHERE ma_sach = 'BAD'", Integer.class));
    }

    @Test void invalidContactsAreRejected() throws Exception {
        assertEquals(400, send("POST", "/api/khach-hang",
                "{\"hoTen\":\"Khách\",\"email\":\"sai-email\"}").statusCode());
        assertEquals(400, send("POST", "/api/khach-hang",
                "{\"hoTen\":\"Khách\",\"soDienThoai\":\"abc\"}").statusCode());
        assertEquals(400, send("POST", "/api/nha-xuat-ban",
                "{\"tenNhaXuatBan\":\"NXB\",\"email\":\"sai-email\",\"trangThai\":true}").statusCode());
    }

    @Test void foreignKeysProtectCategoriesPublishersBooksAndCustomers() throws Exception {
        assertEquals(409, send("DELETE", "/api/danh-muc/1", null).statusCode());
        assertEquals(409, send("DELETE", "/api/nha-xuat-ban/1", null).statusCode());
        String customer = send("POST", "/api/khach-hang", "{\"hoTen\":\"Khách có đơn\"}")
                .headers().firstValue("Location").orElseThrow();
        long customerId = Long.parseLong(customer.substring(customer.lastIndexOf('/') + 1));
        long bookId = jdbc.queryForObject("SELECT id FROM sach WHERE ma_sach = 'S001'", Long.class);
        try {
            jdbc.update("INSERT INTO don_hang(ma_don_hang,khach_hang_id,ten_nguoi_nhan,so_dien_thoai_nhan,dia_chi_giao) VALUES ('W2ORDER',?,'Khách','0901234567','Hà Nội')", customerId);
            long orderId = jdbc.queryForObject("SELECT id FROM don_hang WHERE ma_don_hang = 'W2ORDER'", Long.class);
            jdbc.update("INSERT INTO chi_tiet_don_hang(don_hang_id,sach_id,so_luong,don_gia) VALUES (?,?,1,120000)", orderId, bookId);
            assertEquals(409, send("DELETE", customer, null).statusCode());
            assertEquals(409, send("DELETE", "/api/sach/" + bookId, null).statusCode());
            assertEquals(200, send("GET", customer, null).statusCode());
            assertEquals(200, send("GET", "/api/sach/" + bookId, null).statusCode());
        } finally {
            jdbc.update("DELETE FROM chi_tiet_don_hang WHERE don_hang_id IN (SELECT id FROM don_hang WHERE ma_don_hang = 'W2ORDER')");
            jdbc.update("DELETE FROM don_hang WHERE ma_don_hang = 'W2ORDER'");
            send("DELETE", customer, null);
        }
    }

    @Test void failedBookUpdateRollsBackAndDtoHasRelationIds() throws Exception {
        String path = send("POST", "/api/sach", book("ROLLBACK", "Ban đầu", 1, "10", "1", "null"))
                .headers().firstValue("Location").orElseThrow();
        try {
            assertEquals(404, send("PUT", path, book("ROLLBACK", "Sai quan hệ", 99999999, "20", "2", "null")).statusCode());
            String result = send("GET", path, null).body();
            assertTrue(result.contains("Ban đầu"));
            assertTrue(result.contains("\"danhMucId\":1"));
            assertFalse(result.contains("hibernateLazyInitializer"));
            assertEquals(409, send("PUT", path, book("S001", "Trùng mã", 1, "20", "2", "null")).statusCode());
            assertTrue(send("GET", path, null).body().contains("ROLLBACK"));
        } finally {
            send("DELETE", path, null);
        }
    }

    private String csrf() throws Exception {
        String body=client.send(HttpRequest.newBuilder(URI.create("http://localhost:"+port+"/api/auth/csrf")).GET().build(),HttpResponse.BodyHandlers.ofString()).body();
        var matcher=java.util.regex.Pattern.compile("\"token\":\"([^\"]+)\"").matcher(body);
        if(!matcher.find())throw new IllegalStateException(body);return matcher.group(1);
    }
    private HttpResponse<String> send(String method, String path, String body) throws Exception {
        if (!loggedIn) {
            String token = csrf();
            var login = client.send(HttpRequest.newBuilder(URI.create("http://localhost:"+port+"/api/auth/login"))
                .header("Content-Type","application/x-www-form-urlencoded").header("X-CSRF-TOKEN",token)
                .POST(HttpRequest.BodyPublishers.ofString("username=admin&password=Admin%40123456")).build(),HttpResponse.BodyHandlers.ofString());
            assertEquals(200,login.statusCode()); loggedIn=true;
        }
        HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:" + port + path))
                .header("X-CSRF-TOKEN", csrf()).header("Content-Type", "application/json")
                .method(method, body == null ? HttpRequest.BodyPublishers.noBody()
                        : HttpRequest.BodyPublishers.ofString(body)).build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
