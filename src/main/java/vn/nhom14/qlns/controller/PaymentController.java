package vn.nhom14.qlns.controller;

import java.util.List;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import vn.nhom14.qlns.dto.PaymentReconcileRequest;
import vn.nhom14.qlns.dto.PaymentResponse;
import vn.nhom14.qlns.service.PaymentService;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    private final PaymentService payments;
    public PaymentController(PaymentService payments){this.payments=payments;}
    @GetMapping public List<PaymentResponse> list(Authentication authentication,
            @RequestParam(defaultValue="") String status){return payments.list(authentication,status);}
    @PatchMapping("/{id}/reconcile") public PaymentResponse reconcile(Authentication authentication,@PathVariable long id,
            @Valid @RequestBody PaymentReconcileRequest request){return payments.reconcile(authentication,id,request);}
}
