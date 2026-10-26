package vn.nhom14.qlns.controller;

import java.net.URI;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.nhom14.qlns.dto.*;
import vn.nhom14.qlns.service.KhachHangService;

@RestController
@RequestMapping("/api/khach-hang")
public class KhachHangController {
    private final KhachHangService service;

    public KhachHangController(KhachHangService service) {
        this.service = service;
    }

    @GetMapping
    public PageResponse<KhachHangResponse> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return service.list(page, size);
    }

    @GetMapping("/{id}")
    public KhachHangResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    @PostMapping
    public ResponseEntity<KhachHangResponse> create(@Valid @RequestBody KhachHangRequest request) {
        KhachHangResponse result = service.create(request);
        return ResponseEntity.created(URI.create("/api/khach-hang/" + result.id())).body(result);
    }

    @PutMapping("/{id}")
    public KhachHangResponse update(@PathVariable Long id, @Valid @RequestBody KhachHangRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
