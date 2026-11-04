package vn.nhom14.qlns.controller;

import java.util.List;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import vn.nhom14.qlns.dto.*;
import vn.nhom14.qlns.service.MembershipService;

@RestController
@RequestMapping("/api/memberships")
public class MembershipController {
    private final MembershipService memberships;
    public MembershipController(MembershipService memberships){this.memberships=memberships;}

    @GetMapping("/me") public MembershipResponse mine(Authentication authentication){return memberships.mine(authentication);}
    @GetMapping public List<MembershipResponse> list(Authentication authentication,
            @RequestParam(defaultValue="") String status){return memberships.list(authentication,status);}
    @PostMapping public ResponseEntity<MembershipResponse> register(Authentication authentication,
            @Valid @RequestBody MembershipRegistrationRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(memberships.register(authentication,null,request));
    }
    @PostMapping("/customers/{customerId}") public ResponseEntity<MembershipResponse> registerAtCounter(
            Authentication authentication,@PathVariable Long customerId,
            @Valid @RequestBody MembershipRegistrationRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(memberships.register(authentication,customerId,request));
    }
    @PatchMapping("/{id}/card") public MembershipResponse card(Authentication authentication,@PathVariable long id,
            @Valid @RequestBody CardStatusRequest request){return memberships.changeCardStatus(authentication,id,request);}
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(
            Authentication authentication,@PathVariable long id){memberships.delete(authentication,id);}
}
