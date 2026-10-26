package vn.nhom14.qlns.service;

import java.util.List;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import vn.nhom14.qlns.dto.PageResponse;
import vn.nhom14.qlns.dto.SachResponse;

@Service
public class YeuThichService {
    private final JdbcTemplate jdbc;
    private final SachService books;
    public YeuThichService(JdbcTemplate jdbc, SachService books) { this.jdbc=jdbc; this.books=books; }
    private long account(String username) {
        return jdbc.queryForObject("SELECT id FROM tai_khoan WHERE ten_dang_nhap=?",Long.class,username);
    }
    public record TheLoai(Long id, String tenDanhMuc, long soSach) {}
    public record TongHop(long tongSoSach, List<TheLoai> theLoai) {}
    public List<Long> ids(String username) {
        return jdbc.query("SELECT sach_id FROM sach_yeu_thich WHERE tai_khoan_id=? ORDER BY ngay_luu DESC,sach_id DESC",(rs,n)->rs.getLong(1),account(username));
    }
    public PageResponse<SachResponse> list(String username, int page, int size) {
        if(page<0||size<1||size>100) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Trang hoặc kích thước không hợp lệ");
        long account=account(username);
        long count=jdbc.queryForObject("SELECT COUNT(*) FROM sach_yeu_thich WHERE tai_khoan_id=?",Long.class,account);
        List<Long> ids=jdbc.query("SELECT sach_id FROM sach_yeu_thich WHERE tai_khoan_id=? ORDER BY ngay_luu DESC,sach_id DESC LIMIT ? OFFSET ?",(rs,n)->rs.getLong(1),account,size,(long)page*size);
        return new PageResponse<>(ids.stream().map(books::get).toList(),page,size,count,(int)((count+size-1)/size));
    }
    public void save(String username,long bookId) {
        long account=account(username);
        books.get(bookId);
        try { jdbc.update("INSERT INTO sach_yeu_thich(tai_khoan_id,sach_id) VALUES (?,?)",account,bookId); }
        catch(DuplicateKeyException ignored) { /* Lưu lại cùng sách không tăng số lượng. */ }
    }
    public void remove(String username,long bookId) {
        jdbc.update("DELETE FROM sach_yeu_thich WHERE tai_khoan_id=? AND sach_id=?",account(username),bookId);
    }
    public TongHop summary(String username) {
        List<TheLoai> categories=jdbc.query("SELECT d.id,d.ten_danh_muc,COUNT(*) AS so_sach FROM sach_yeu_thich y JOIN sach s ON y.sach_id=s.id JOIN danh_muc d ON s.danh_muc_id=d.id WHERE y.tai_khoan_id=? GROUP BY d.id,d.ten_danh_muc ORDER BY so_sach DESC,d.id ASC",(rs,n)->new TheLoai(rs.getLong(1),rs.getString(2),rs.getLong(3)),account(username));
        return new TongHop(categories.stream().mapToLong(TheLoai::soSach).sum(),categories);
    }
}
