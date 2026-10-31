package vn.nhom14.qlns.controller;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController @RequestMapping("/api/auth")
public class PasswordController {
    private final JdbcTemplate jdbc; private final PasswordEncoder encoder;
    public PasswordController(JdbcTemplate jdbc,PasswordEncoder encoder){this.jdbc=jdbc;this.encoder=encoder;}
    public record PasswordChange(@NotBlank @Size(max=64) String currentPassword,
                                 @NotBlank @Size(min=8,max=64) String newPassword){}
    @PutMapping("/password") @Transactional(timeout=10)
    public Map<String,String> change(Authentication auth,@Valid @RequestBody PasswordChange body,HttpServletRequest request){
        if(body.currentPassword().getBytes(StandardCharsets.UTF_8).length>72
                ||body.newPassword().getBytes(StandardCharsets.UTF_8).length>72)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Mật khẩu không được vượt quá 72 byte UTF-8");
        String hash=jdbc.queryForObject("SELECT mat_khau_hash FROM tai_khoan WHERE ten_dang_nhap=?",String.class,auth.getName());
        if(!encoder.matches(body.currentPassword(),hash))throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Mật khẩu hiện tại không đúng");
        if(body.currentPassword().equals(body.newPassword()))throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Mật khẩu mới phải khác mật khẩu hiện tại");
        int changed=jdbc.update("UPDATE tai_khoan SET mat_khau_hash=? WHERE ten_dang_nhap=? AND mat_khau_hash=?",
                encoder.encode(body.newPassword()),auth.getName(),hash);
        if(changed!=1)throw new ResponseStatusException(HttpStatus.CONFLICT,"Mật khẩu vừa được thay đổi ở phiên khác. Vui lòng đăng nhập lại");
        if(request.getSession(false)!=null)request.getSession(false).invalidate();
        SecurityContextHolder.clearContext();
        return Map.of("message","Đã đổi mật khẩu. Vui lòng đăng nhập lại.");
    }
}
