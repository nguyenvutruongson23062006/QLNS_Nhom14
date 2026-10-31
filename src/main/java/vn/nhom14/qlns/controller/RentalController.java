package vn.nhom14.qlns.controller;

import java.util.List;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import vn.nhom14.qlns.dto.*;
import vn.nhom14.qlns.service.RentalService;

@RestController
@RequestMapping("/api/rentals")
public class RentalController {
    private final RentalService rentals;
    public RentalController(RentalService rentals){this.rentals=rentals;}
    @GetMapping public List<RentalResponse> list(Authentication authentication,@RequestParam(defaultValue="") String status){return rentals.list(authentication,status);}
    @GetMapping("/overdue") public List<RentalResponse> overdue(Authentication authentication){return rentals.overdue(authentication);}
    @GetMapping("/{id}") public RentalResponse get(Authentication authentication,@PathVariable long id){return rentals.get(authentication,id);}
    @PostMapping public ResponseEntity<RentalResponse> create(Authentication authentication,@Valid @RequestBody RentalRequest request){return ResponseEntity.status(HttpStatus.CREATED).body(rentals.create(authentication,null,request));}
    @PostMapping("/customers/{customerId}") public ResponseEntity<RentalResponse> createAtCounter(Authentication authentication,@PathVariable long customerId,@Valid @RequestBody RentalRequest request){return ResponseEntity.status(HttpStatus.CREATED).body(rentals.create(authentication,customerId,request));}
    @PatchMapping("/{id}/approve") public RentalResponse approve(Authentication authentication,@PathVariable long id){return rentals.approve(authentication,id);}
    @PatchMapping("/{id}/handover") public RentalResponse handover(Authentication authentication,@PathVariable long id){return rentals.handover(authentication,id);}
    @PatchMapping("/{id}/cancel") public RentalResponse cancel(Authentication authentication,@PathVariable long id){return rentals.cancel(authentication,id);}
    @PostMapping("/{id}/renew") public RentalResponse renew(Authentication authentication,@PathVariable long id,@Valid @RequestBody RentalRenewRequest request){return rentals.renew(authentication,id,request);}
    @PostMapping("/{id}/return") public RentalResponse returnBooks(Authentication authentication,@PathVariable long id,@Valid @RequestBody RentalReturnRequest request){return rentals.returnBooks(authentication,id,request);}
}
