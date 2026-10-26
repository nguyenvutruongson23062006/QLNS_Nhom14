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
public class DanhMucService {
    private final DanhMucRepository repository;

    public DanhMucService(DanhMucRepository repository) {
        this.repository = repository;
    }

    public PageResponse<DanhMucResponse> list(int page, int size) {
        if (page < 0 || size < 1 || size > 100) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "page >= 0, size từ 1 đến 100");
        }
        Page<DanhMucResponse> result = repository.findAll(
                PageRequest.of(page, size, Sort.by("id"))).map(this::toResponse);
        return new PageResponse<>(result.getContent(), page, size,
                result.getTotalElements(), result.getTotalPages());
    }

    public DanhMucResponse get(Long id) {
        return toResponse(find(id));
    }

    @Transactional
    public DanhMucResponse create(DanhMucRequest request) {
        DanhMuc entity = new DanhMuc();
        apply(entity, request);
        return toResponse(repository.saveAndFlush(entity));
    }

    @Transactional
    public DanhMucResponse update(Long id, DanhMucRequest request) {
        DanhMuc entity = find(id);
        apply(entity, request);
        return toResponse(repository.saveAndFlush(entity));
    }

    @Transactional
    public void delete(Long id) {
        repository.delete(find(id));
        repository.flush();
    }

    private DanhMuc find(Long id) {
        return repository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy danh_muc có id " + id));
    }

    private void apply(DanhMuc entity, DanhMucRequest request) {
        entity.setTenDanhMuc(normalize(request.tenDanhMuc()));
        entity.setMoTa(normalize(request.moTa()));
        entity.setTrangThai(request.trangThai());
    }

    private String normalize(String value) {
        return value == null || value.isBlank() ? null : value.strip();
    }

    private DanhMucResponse toResponse(DanhMuc entity) {
        return new DanhMucResponse(
                entity.getId(),
                entity.getTenDanhMuc(),
                entity.getMoTa(),
                entity.getTrangThai());
    }
}
