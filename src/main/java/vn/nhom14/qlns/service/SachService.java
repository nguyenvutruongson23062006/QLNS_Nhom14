package vn.nhom14.qlns.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import vn.nhom14.qlns.dto.*;
import vn.nhom14.qlns.entity.*;
import vn.nhom14.qlns.repository.*;

@Service
@Transactional(readOnly = true)
public class SachService {
    private final SachRepository repository;
    private final DanhMucRepository danhMucRepository;
    private final NhaXuatBanRepository nhaXuatBanRepository;

    public SachService(SachRepository repository, DanhMucRepository danhMucRepository,
            NhaXuatBanRepository nhaXuatBanRepository) {
        this.repository = repository;
        this.danhMucRepository = danhMucRepository;
        this.nhaXuatBanRepository = nhaXuatBanRepository;
    }

    public PageResponse<SachResponse> list(int page, int size) {
        if (page < 0 || size < 1 || size > 100) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "page >= 0, size từ 1 đến 100");
        }
        Page<SachResponse> result = repository.findAll(
                PageRequest.of(page, size, Sort.by("id"))).map(this::toResponse);
        return new PageResponse<>(result.getContent(), page, size,
                result.getTotalElements(), result.getTotalPages());
    }

    public SachResponse get(Long id) {
        return toResponse(find(id));
    }

    @Transactional
    public SachResponse create(SachRequest request) {
        Sach entity = new Sach();
        apply(entity, request);
        return toResponse(repository.saveAndFlush(entity));
    }

    @Transactional
    public SachResponse update(Long id, SachRequest request) {
        Sach entity = find(id);
        apply(entity, request);
        return toResponse(repository.saveAndFlush(entity));
    }

    @Transactional
    public void delete(Long id) {
        repository.delete(find(id));
        repository.flush();
    }

    private Sach find(Long id) {
        return repository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy sach có id " + id));
    }

    private void apply(Sach entity, SachRequest request) {
        entity.setMaSach(normalize(request.maSach()));
        entity.setTenSach(normalize(request.tenSach()));
        entity.setTacGia(normalize(request.tacGia()));
        entity.setIsbn(normalize(request.isbn()));
        entity.setNamXuatBan(request.namXuatBan());
        entity.setGiaBan(request.giaBan());
        entity.setSoLuongTon(request.soLuongTon());
        entity.setMoTa(normalize(request.moTa()));
        entity.setHinhAnh(normalize(request.hinhAnh()));
        entity.setTrangThai(request.trangThai());
        entity.setDanhMuc(danhMucRepository.findById(request.danhMucId()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy danh_muc")));
        entity.setNhaXuatBan(nhaXuatBanRepository.findById(request.nhaXuatBanId()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy nha_xuat_ban")));
    }

    private String normalize(String value) {
        return value == null || value.isBlank() ? null : value.strip();
    }

    private SachResponse toResponse(Sach entity) {
        return new SachResponse(
                entity.getId(),
                entity.getMaSach(),
                entity.getTenSach(),
                entity.getTacGia(),
                entity.getIsbn(),
                entity.getNamXuatBan(),
                entity.getGiaBan(),
                entity.getSoLuongTon(),
                entity.getMoTa(),
                entity.getHinhAnh(),
                entity.getTrangThai(),
                entity.getDanhMuc().getId(),
                entity.getNhaXuatBan().getId(),
                entity.getNgayTao());
    }
}
