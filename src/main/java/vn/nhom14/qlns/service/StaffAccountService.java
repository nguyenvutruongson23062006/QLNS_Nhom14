package vn.nhom14.qlns.service;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import vn.nhom14.qlns.dto.*;

@Service
@Transactional(readOnly=true,timeout=10)
public class StaffAccountService {
    private static final String STAFF="vai_tro_id=(SELECT id FROM vai_tro WHERE ma_vai_tro='NHAN_VIEN')";
    private static final String COLUMNS="id,ten_dang_nhap,email,trang_thai,ngay_tao";
    private static final RowMapper<StaffAccountResponse> ROW=(rs,row)->new StaffAccountResponse(
            rs.getLong("id"),rs.getString("ten_dang_nhap"),rs.getString("email"),"NHAN_VIEN",
            rs.getBoolean("trang_thai"),rs.getTimestamp("ngay_tao").toLocalDateTime());
    private final JdbcTemplate jdbc; private final PasswordEncoder passwords;
    public StaffAccountService(JdbcTemplate jdbc,PasswordEncoder passwords){this.jdbc=jdbc;this.passwords=passwords;}

    public PageResponse<StaffAccountResponse> list(int page,int size,String q){
        if(page<0||size<1||size>100)throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Phân trang không hợp lệ");
        String search=q==null?"":q.strip().toLowerCase(Locale.ROOT);
        if(search.length()>150)throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Từ khóa tối đa 150 ký tự");
        String pattern="%"+search.replace("!","!!").replace("%","!%").replace("_","!_")+"%";
        String where=" WHERE "+STAFF+" AND (LOWER(ten_dang_nhap) LIKE ? ESCAPE '!' OR LOWER(email) LIKE ? ESCAPE '!')";
        long count=jdbc.queryForObject("SELECT COUNT(*) FROM tai_khoan"+where,Long.class,pattern,pattern);
        var rows=jdbc.query("SELECT "+COLUMNS+" FROM tai_khoan"+where+" ORDER BY id DESC LIMIT ? OFFSET ?",
                ROW,pattern,pattern,size,(long)page*size);
        return new PageResponse<>(rows,page,size,count,(int)((count+size-1)/size));
    }
    public StaffAccountResponse get(Long id){
        var rows=jdbc.query("SELECT "+COLUMNS+" FROM tai_khoan WHERE id=? AND "+STAFF,ROW,id);
        if(rows.isEmpty())throw missing();return rows.getFirst();
    }
    @Transactional(timeout=10) public StaffAccountResponse create(StaffAccountCreateRequest request){
        validatePassword(request.password());
        jdbc.update("""
                INSERT INTO tai_khoan(ten_dang_nhap,mat_khau_hash,email,vai_tro_id,trang_thai)
                SELECT ?,?,?,id,TRUE FROM vai_tro WHERE ma_vai_tro='NHAN_VIEN'
                """,request.username(),passwords.encode(request.password()),request.email().strip());
        return jdbc.query("SELECT "+COLUMNS+" FROM tai_khoan WHERE ten_dang_nhap=? AND "+STAFF,ROW,request.username()).getFirst();
    }
    @Transactional(timeout=10) public StaffAccountResponse update(Long id,StaffAccountUpdateRequest request){
        if(jdbc.update("UPDATE tai_khoan SET email=?,trang_thai=? WHERE id=? AND "+STAFF,
                request.email().strip(),request.enabled(),id)==0)throw missing();return get(id);
    }
    @Transactional(timeout=10) public void resetPassword(Long id,StaffPasswordRequest request){
        validatePassword(request.password());
        if(jdbc.update("UPDATE tai_khoan SET mat_khau_hash=? WHERE id=? AND "+STAFF,
                passwords.encode(request.password()),id)==0)throw missing();
    }
    private static void validatePassword(String password){
        if(password.getBytes(StandardCharsets.UTF_8).length>72)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Mật khẩu không được vượt quá 72 byte UTF-8");
    }
    private static ResponseStatusException missing(){return new ResponseStatusException(HttpStatus.NOT_FOUND,"Không tìm thấy tài khoản nhân viên");}
}
