package vn.nhom14.qlns.controller;

import java.net.URI;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.nhom14.qlns.dto.*;
import vn.nhom14.qlns.service.SachService;

@RestController
@RequestMapping("/api/sach")
public class SachController {
    private final SachService service;

    public SachController(SachService service) {
        this.service = service;
    }

    @GetMapping
    public PageResponse<SachResponse> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return service.list(page, size);
    }

    @GetMapping("/{id}")
    public SachResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    @PostMapping
    public ResponseEntity<SachResponse> create(@Valid @RequestBody SachRequest request) {
        SachResponse result = service.create(request);
        return ResponseEntity.created(URI.create("/api/sach/" + result.id())).body(result);
    }

    @PutMapping("/{id}")
    public SachResponse update(@PathVariable Long id, @Valid @RequestBody SachRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
