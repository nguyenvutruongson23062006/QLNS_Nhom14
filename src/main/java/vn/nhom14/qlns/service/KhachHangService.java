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
public class KhachHangService {
    private final KhachHangRepository repository;

    public KhachHangService(KhachHangRepository repository) {
        this.repository = repository;
    }

    public PageResponse<KhachHangResponse> list(int page, int size) {
        if (page < 0 || size < 1 || size > 100) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "page >= 0, size từ 1 đến 100");
        }
        Page<KhachHangResponse> result = repository.findAll(
                PageRequest.of(page, size, Sort.by("id"))).map(this::toResponse);
        return new PageResponse<>(result.getContent(), page, size,
                result.getTotalElements(), result.getTotalPages());
    }

    public KhachHangResponse get(Long id) {
        return toResponse(find(id));
    }

    @Transactional
    public KhachHangResponse create(KhachHangRequest request) {
        KhachHang entity = new KhachHang();
        apply(entity, request);
        return toResponse(repository.saveAndFlush(entity));
    }

    @Transactional
    public KhachHangResponse update(Long id, KhachHangRequest request) {
        KhachHang entity = find(id);
        apply(entity, request);
        return toResponse(repository.saveAndFlush(entity));
    }

    @Transactional
    public void delete(Long id) {
        repository.delete(find(id));
        repository.flush();
    }

    private KhachHang find(Long id) {
        return repository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy khach_hang có id " + id));
    }

    private void apply(KhachHang entity, KhachHangRequest request) {
        entity.setHoTen(normalize(request.hoTen()));
        entity.setSoDienThoai(normalize(request.soDienThoai()));
        entity.setEmail(normalize(request.email()));
        entity.setDiaChi(normalize(request.diaChi()));
    }

    private String normalize(String value) {
        return value == null || value.isBlank() ? null : value.strip();
    }

    private KhachHangResponse toResponse(KhachHang entity) {
        return new KhachHangResponse(
                entity.getId(),
                entity.getHoTen(),
                entity.getSoDienThoai(),
                entity.getEmail(),
                entity.getDiaChi(),
                entity.getNgayTao(),
                entity.getTaiKhoanId());
    }
}
