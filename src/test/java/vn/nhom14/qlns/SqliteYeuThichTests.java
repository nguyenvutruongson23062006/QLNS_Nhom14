package vn.nhom14.qlns;
import java.util.UUID;
import org.springframework.test.context.*;
@ActiveProfiles(value="sqlite",inheritProfiles=false)
class SqliteYeuThichTests extends YeuThichIntegrationTests {
 private static final String FILE="./target/SqliteYeuThichTests-"+UUID.randomUUID()+".sqlite";
 @DynamicPropertySource static void sqlite(DynamicPropertyRegistry r){r.add("spring.datasource.url",()->"jdbc:sqlite:"+FILE+"?foreign_keys=on&busy_timeout=10000");}
}
