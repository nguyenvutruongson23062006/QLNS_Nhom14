package vn.nhom14.qlns.controller;
import java.util.Map;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ho-so")
public class HoSoController {
 private final JdbcTemplate jdbc;
 public HoSoController(JdbcTemplate jdbc){this.jdbc=jdbc;}
 public record HoSo(String tenDangNhap,String hoTen,String email,String soDienThoai,String diaChi,String anhDaiDien,String theLoaiYeuThich){}
 public record CapNhat(@NotBlank @Size(max=150) String hoTen,@NotBlank @Email @Size(max=150) String email,@Pattern(regexp="^$|[0-9+() .-]{8,20}$") String soDienThoai,@Size(max=500) String diaChi,@Pattern(regexp="^$|/images/uploads/[a-f0-9-]+\\.png") String anhDaiDien,@Size(max=500) String theLoaiYeuThich){}
 private Long id(Authentication auth){return jdbc.queryForObject("SELECT id FROM tai_khoan WHERE ten_dang_nhap=?",Long.class,auth.getName());}
 @GetMapping public HoSo get(Authentication auth){return jdbc.queryForObject("SELECT t.ten_dang_nhap,COALESCE(k.ho_ten,''),t.email,COALESCE(k.so_dien_thoai,''),COALESCE(k.dia_chi,''),COALESCE(h.anh_dai_dien,''),COALESCE(h.the_loai_yeu_thich,'') FROM tai_khoan t LEFT JOIN khach_hang k ON k.tai_khoan_id=t.id LEFT JOIN ho_so_ca_nhan h ON h.tai_khoan_id=t.id WHERE t.id=?",(rs,n)->new HoSo(rs.getString(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5),rs.getString(6),rs.getString(7)),id(auth));}
 @PutMapping @Transactional public HoSo update(Authentication auth,@Valid @RequestBody CapNhat r){
  long id=id(auth);
  jdbc.update("UPDATE tai_khoan SET email=? WHERE id=?",r.email().trim(),id);
  if(jdbc.queryForObject("SELECT COUNT(*) FROM khach_hang WHERE tai_khoan_id=?",Integer.class,id)==0)
   jdbc.update("INSERT INTO khach_hang(tai_khoan_id,ho_ten,email,so_dien_thoai,dia_chi) VALUES (?,?,?,?,?)",id,r.hoTen().trim(),r.email().trim(),r.soDienThoai(),r.diaChi());
  else jdbc.update("UPDATE khach_hang SET ho_ten=?,email=?,so_dien_thoai=?,dia_chi=? WHERE tai_khoan_id=?",r.hoTen().trim(),r.email().trim(),r.soDienThoai(),r.diaChi(),id);
  if(jdbc.queryForObject("SELECT COUNT(*) FROM ho_so_ca_nhan WHERE tai_khoan_id=?",Integer.class,id)==0)
   jdbc.update("INSERT INTO ho_so_ca_nhan(tai_khoan_id,anh_dai_dien,the_loai_yeu_thich) VALUES (?,?,?)",id,r.anhDaiDien(),r.theLoaiYeuThich());
  else jdbc.update("UPDATE ho_so_ca_nhan SET anh_dai_dien=?,the_loai_yeu_thich=? WHERE tai_khoan_id=?",r.anhDaiDien(),r.theLoaiYeuThich(),id);
  return get(auth);
 }
}
