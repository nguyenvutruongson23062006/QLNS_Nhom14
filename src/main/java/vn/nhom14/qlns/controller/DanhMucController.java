package vn.nhom14.qlns.controller;

import java.net.URI;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.nhom14.qlns.dto.*;
import vn.nhom14.qlns.service.DanhMucService;

@RestController
@RequestMapping("/api/danh-muc")
public class DanhMucController {
    private final DanhMucService service;

    public DanhMucController(DanhMucService service) {
        this.service = service;
    }

    @GetMapping
    public PageResponse<DanhMucResponse> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return service.list(page, size);
    }

    @GetMapping("/{id}")
    public DanhMucResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    @PostMapping
    public ResponseEntity<DanhMucResponse> create(@Valid @RequestBody DanhMucRequest request) {
        DanhMucResponse result = service.create(request);
        return ResponseEntity.created(URI.create("/api/danh-muc/" + result.id())).body(result);
    }

    @PutMapping("/{id}")
    public DanhMucResponse update(@PathVariable Long id, @Valid @RequestBody DanhMucRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
