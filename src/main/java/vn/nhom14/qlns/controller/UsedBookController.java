package vn.nhom14.qlns.controller;

import java.util.List;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import vn.nhom14.qlns.dto.*;
import vn.nhom14.qlns.service.UsedBookService;

@RestController
@RequestMapping("/api/used-books")
public class UsedBookController {
    private final UsedBookService usedBooks;
    public UsedBookController(UsedBookService usedBooks){this.usedBooks=usedBooks;}
    @GetMapping public List<UsedBookResponse> list(){return usedBooks.listForSale();}
    @GetMapping("/manage") public List<UsedBookResponse> manage(Authentication auth){return usedBooks.manage(auth);}
    @GetMapping("/candidates") public List<UsedBookResponse> candidates(Authentication auth){return usedBooks.candidates(auth);}
    @PostMapping public ResponseEntity<UsedBookResponse> create(Authentication auth,@Valid @RequestBody UsedBookCreateRequest request){return ResponseEntity.status(HttpStatus.CREATED).body(usedBooks.create(auth,request));}
    @PatchMapping("/{id}/approve") public UsedBookResponse approve(Authentication auth,@PathVariable long id,@Valid @RequestBody UsedBookApproveRequest request){return usedBooks.approve(auth,id,request);}
    @PatchMapping("/{id}/stop") public UsedBookResponse stop(Authentication auth,@PathVariable long id){return usedBooks.stop(auth,id);}
    @PostMapping("/orders") public ResponseEntity<OrderResponse> checkout(Authentication auth,@Valid @RequestBody UsedBookOrderRequest request){return ResponseEntity.status(HttpStatus.CREATED).body(usedBooks.checkout(auth,request));}
}
