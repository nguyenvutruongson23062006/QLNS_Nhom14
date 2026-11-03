package vn.nhom14.qlns.service;

import java.math.RoundingMode;
import java.util.*;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import vn.nhom14.qlns.dto.*;
import vn.nhom14.qlns.security.CurrentActorService;

@Service
@Transactional(readOnly=true,timeout=10)
public class RentalCatalogService {
    private final JdbcTemplate jdbc;private final CurrentActorService actors;
    public RentalCatalogService(JdbcTemplate jdbc,CurrentActorService actors){this.jdbc=jdbc;this.actors=actors;}

    public List<RentalCatalogResponse> list(){return jdbc.query("""
            SELECT s.id,s.ma_sach,s.ten_sach,s.tac_gia,s.hinh_anh,c.cho_thue,c.gia_thue_ngay,c.tien_coc,
                c.so_ngay_mac_dinh,c.so_lan_gia_han,c.phi_tre_ngay,
                SUM(CASE WHEN b.trang_thai='SAN_SANG' AND b.hoat_dong=TRUE THEN 1 ELSE 0 END) AS san_sang,
                COUNT(b.id) AS tong
            FROM cau_hinh_thue_sach c JOIN sach s ON s.id=c.sach_id
            LEFT JOIN ban_sao_sach b ON b.sach_id=s.id
            WHERE s.trang_thai=TRUE GROUP BY s.id,s.ma_sach,s.ten_sach,s.tac_gia,s.hinh_anh,c.cho_thue,
                c.gia_thue_ngay,c.tien_coc,c.so_ngay_mac_dinh,c.so_lan_gia_han,c.phi_tre_ngay
            ORDER BY s.ten_sach,s.id
            """,(result,index)->new RentalCatalogResponse(result.getLong("id"),result.getString("ma_sach"),
            result.getString("ten_sach"),result.getString("tac_gia"),result.getString("hinh_anh"),result.getBoolean("cho_thue"),
            result.getBigDecimal("gia_thue_ngay").setScale(2,RoundingMode.UNNECESSARY),result.getBigDecimal("tien_coc").setScale(2,RoundingMode.UNNECESSARY),
            result.getInt("so_ngay_mac_dinh"),result.getInt("so_lan_gia_han"),result.getBigDecimal("phi_tre_ngay").setScale(2,RoundingMode.UNNECESSARY),
            result.getInt("san_sang"),result.getInt("tong")));
    }

    @Transactional(timeout=10)
    public RentalCatalogResponse configure(Authentication authentication,long bookId,RentalConfigRequest request){
        actors.require(authentication).requireStaff();
        if(jdbc.queryForObject("SELECT COUNT(*) FROM sach WHERE id=?",Integer.class,bookId)==0)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Không tìm thấy sách");
        if(jdbc.queryForObject("SELECT COUNT(*) FROM cau_hinh_thue_sach WHERE sach_id=?",Integer.class,bookId)==0)
            jdbc.update("INSERT INTO cau_hinh_thue_sach(sach_id,cho_thue,gia_thue_ngay,tien_coc,so_ngay_mac_dinh,so_lan_gia_han,phi_tre_ngay) VALUES(?,?,?,?,?,?,?)",
                    bookId,request.choThue(),request.giaThueNgay(),request.tienCoc(),request.soNgayMacDinh(),request.soLanGiaHan(),request.phiTreNgay());
        else jdbc.update("UPDATE cau_hinh_thue_sach SET cho_thue=?,gia_thue_ngay=?,tien_coc=?,so_ngay_mac_dinh=?,so_lan_gia_han=?,phi_tre_ngay=?,ngay_cap_nhat=CURRENT_TIMESTAMP WHERE sach_id=?",
                request.choThue(),request.giaThueNgay(),request.tienCoc(),request.soNgayMacDinh(),request.soLanGiaHan(),request.phiTreNgay(),bookId);
        return list().stream().filter(item->item.sachId()==bookId).findFirst().orElseThrow();
    }

    @Transactional(timeout=10)
    public RentalCatalogResponse addCopies(Authentication authentication,long bookId,CopyCreateRequest request){
        actors.require(authentication).requireStaff();
        if(jdbc.queryForObject("SELECT COUNT(*) FROM cau_hinh_thue_sach WHERE sach_id=?",Integer.class,bookId)==0)
            throw new ResponseStatusException(HttpStatus.CONFLICT,"Cần cấu hình thuê trước khi thêm bản sao");
        String note=request.ghiChu()==null||request.ghiChu().isBlank()?null:request.ghiChu().strip();
        for(int index=0;index<request.soLuong();index++)jdbc.update("""
                INSERT INTO ban_sao_sach(ma_ban_sao,sach_id,tinh_trang,trang_thai,ghi_chu)
                VALUES(?,?,?,'SAN_SANG',?)
                ""","BS"+UUID.randomUUID().toString().replace("-","").toUpperCase(Locale.ROOT),bookId,request.tinhTrang(),note);
        return list().stream().filter(item->item.sachId()==bookId).findFirst().orElseThrow();
    }
}
