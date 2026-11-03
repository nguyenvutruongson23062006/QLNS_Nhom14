package vn.nhom14.qlns;

import java.util.UUID;
import org.springframework.test.context.*;

@ActiveProfiles(value={"sqlite","test"},inheritProfiles=false)
class SqliteRentalTests extends RentalIntegrationTests {
    private static final String FILE="./target/sqlite-rental-"+UUID.randomUUID()+".sqlite";
    @DynamicPropertySource static void sqlite(DynamicPropertyRegistry registry){registry.add("spring.datasource.url",()->"jdbc:sqlite:"+FILE+"?foreign_keys=on&busy_timeout=10000");}
}
