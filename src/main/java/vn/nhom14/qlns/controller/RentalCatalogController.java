package vn.nhom14.qlns.controller;

import java.util.List;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import vn.nhom14.qlns.dto.*;
import vn.nhom14.qlns.service.RentalCatalogService;

@RestController
@RequestMapping("/api/rental-catalog")
public class RentalCatalogController {
    private final RentalCatalogService catalog;
    public RentalCatalogController(RentalCatalogService catalog){this.catalog=catalog;}
    @GetMapping public List<RentalCatalogResponse> list(){return catalog.list();}
    @PutMapping("/{bookId}") public RentalCatalogResponse configure(Authentication authentication,@PathVariable long bookId,
            @Valid @RequestBody RentalConfigRequest request){return catalog.configure(authentication,bookId,request);}
    @PostMapping("/{bookId}/copies") public RentalCatalogResponse copies(Authentication authentication,@PathVariable long bookId,
            @Valid @RequestBody CopyCreateRequest request){return catalog.addCopies(authentication,bookId,request);}
}
