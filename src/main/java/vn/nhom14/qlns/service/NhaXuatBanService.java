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
public class NhaXuatBanService {
    private final NhaXuatBanRepository repository;

    public NhaXuatBanService(NhaXuatBanRepository repository) {
        this.repository = repository;
    }

    public PageResponse<NhaXuatBanResponse> list(int page, int size) {
        if (page < 0 || size < 1 || size > 100) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "page >= 0, size từ 1 đến 100");
        }
        Page<NhaXuatBanResponse> result = repository.findAll(
                PageRequest.of(page, size, Sort.by("id"))).map(this::toResponse);
        return new PageResponse<>(result.getContent(), page, size,
                result.getTotalElements(), result.getTotalPages());
    }

    public NhaXuatBanResponse get(Long id) {
        return toResponse(find(id));
    }

    @Transactional
    public NhaXuatBanResponse create(NhaXuatBanRequest request) {
        NhaXuatBan entity = new NhaXuatBan();
        apply(entity, request);
        return toResponse(repository.saveAndFlush(entity));
    }

    @Transactional
    public NhaXuatBanResponse update(Long id, NhaXuatBanRequest request) {
        NhaXuatBan entity = find(id);
        apply(entity, request);
        return toResponse(repository.saveAndFlush(entity));
    }

    @Transactional
    public void delete(Long id) {
        repository.delete(find(id));
        repository.flush();
    }

    private NhaXuatBan find(Long id) {
        return repository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy nha_xuat_ban có id " + id));
    }

    private void apply(NhaXuatBan entity, NhaXuatBanRequest request) {
        entity.setTenNhaXuatBan(normalize(request.tenNhaXuatBan()));
        entity.setDiaChi(normalize(request.diaChi()));
        entity.setSoDienThoai(normalize(request.soDienThoai()));
        entity.setEmail(normalize(request.email()));
        entity.setTrangThai(request.trangThai());
    }

    private String normalize(String value) {
        return value == null || value.isBlank() ? null : value.strip();
    }

    private NhaXuatBanResponse toResponse(NhaXuatBan entity) {
        return new NhaXuatBanResponse(
                entity.getId(),
                entity.getTenNhaXuatBan(),
                entity.getDiaChi(),
                entity.getSoDienThoai(),
                entity.getEmail(),
                entity.getTrangThai());
    }
}
