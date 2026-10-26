package vn.nhom14.qlns.security;
import java.nio.file.*;
import javax.sql.DataSource;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

@Configuration
@Profile("sqlite")
public class SqliteConfig {
 @Bean DataSource sqliteDataSource(@Value("${spring.datasource.url}") String url) throws Exception {
  String path=url.substring("jdbc:sqlite:".length()).split("\\?")[0];
  Path file=Path.of(path).toAbsolutePath();Files.createDirectories(file.getParent());
  HikariDataSource ds=new HikariDataSource();ds.setDriverClassName("org.sqlite.JDBC");ds.setJdbcUrl(url);ds.setMaximumPoolSize(1);ds.setConnectionInitSql("PRAGMA foreign_keys=ON");
  try(var c=ds.getConnection()) {
   c.setAutoCommit(false);
   try {ScriptUtils.executeSqlScript(c,new org.springframework.core.io.support.EncodedResource(new ClassPathResource("db/sqlite/schema.sql"),"UTF-8"));
    try(var stmt=c.createStatement();var result=stmt.executeQuery("SELECT COUNT(*) FROM vai_tro")) {
     if(result.next()&&result.getInt(1)==0)ScriptUtils.executeSqlScript(c,new org.springframework.core.io.support.EncodedResource(new ClassPathResource("db/data.sql"),"UTF-8"));
    }
    c.commit();
   }catch(Exception e){c.rollback();ds.close();throw e;}
  }
  return ds;
 }
 @Bean org.springframework.jdbc.core.JdbcTemplate sqliteJdbc(DataSource ds) {
  var jdbc=new org.springframework.jdbc.core.JdbcTemplate(ds);
  jdbc.setExceptionTranslator((task,sql,ex)->{
   if(ex instanceof org.sqlite.SQLiteException se){
    int code=se.getResultCode().code;
    if(code==1555||code==2067)return new org.springframework.dao.DuplicateKeyException("Dữ liệu bị trùng",ex);
    if((code&255)==19)return new org.springframework.dao.DataIntegrityViolationException("Vi phạm ràng buộc dữ liệu",ex);
   }
   return new org.springframework.jdbc.UncategorizedSQLException(task,sql,ex);
  });return jdbc;
 }
}
