package vn.nhom14.qlns;
import java.util.UUID;
import org.springframework.test.context.*;
@ActiveProfiles(value="sqlite",inheritProfiles=false)
class SqliteHoSoTests extends HoSoIntegrationTests {
 private static final String FILE="./target/SqliteHoSoTests-"+UUID.randomUUID()+".sqlite";
 @DynamicPropertySource static void sqlite(DynamicPropertyRegistry r){r.add("spring.datasource.url",()->"jdbc:sqlite:"+FILE+"?foreign_keys=on&busy_timeout=10000");}
}
