package vn.nhom14.qlns;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.*;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles(value="sqlite",inheritProfiles=false)
class SqliteIntegrationTests extends WeekTwoIntegrationTests {
 private static final String FILE="./target/sqlite-test-"+UUID.randomUUID()+".sqlite";
 @DynamicPropertySource static void sqlite(DynamicPropertyRegistry registry){registry.add("spring.datasource.url",()->"jdbc:sqlite:"+FILE+"?foreign_keys=on&busy_timeout=10000");}
 @Test void usesSqliteWithForeignKeys(){
  assertNotNull(jdbc.queryForObject("SELECT sqlite_version()",String.class));
  assertEquals(1,jdbc.queryForObject("PRAGMA foreign_keys",Integer.class));
  assertEquals(40,jdbc.queryForObject("SELECT COUNT(*) FROM sach",Integer.class));
 }
}
