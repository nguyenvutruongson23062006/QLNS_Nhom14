package vn.nhom14.qlns.controller;

import java.net.URI;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.nhom14.qlns.dto.*;
import vn.nhom14.qlns.service.NhaXuatBanService;

@RestController
@RequestMapping("/api/nha-xuat-ban")
public class NhaXuatBanController {
    private final NhaXuatBanService service;

    public NhaXuatBanController(NhaXuatBanService service) {
        this.service = service;
    }

    @GetMapping
    public PageResponse<NhaXuatBanResponse> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return service.list(page, size);
    }

    @GetMapping("/{id}")
    public NhaXuatBanResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    @PostMapping
    public ResponseEntity<NhaXuatBanResponse> create(@Valid @RequestBody NhaXuatBanRequest request) {
        NhaXuatBanResponse result = service.create(request);
        return ResponseEntity.created(URI.create("/api/nha-xuat-ban/" + result.id())).body(result);
    }

    @PutMapping("/{id}")
    public NhaXuatBanResponse update(@PathVariable Long id, @Valid @RequestBody NhaXuatBanRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
