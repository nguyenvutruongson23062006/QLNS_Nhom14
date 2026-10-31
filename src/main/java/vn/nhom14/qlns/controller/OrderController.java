package vn.nhom14.qlns.controller;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import vn.nhom14.qlns.dto.*;
import vn.nhom14.qlns.service.OrderService;
@RestController @RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orders;
    public OrderController(OrderService orders){this.orders=orders;}
    @GetMapping public PageResponse<OrderResponse> list(Authentication auth,@RequestParam(defaultValue="0") int page,
        @RequestParam(defaultValue="12") int size,@RequestParam(defaultValue="") String q,
        @RequestParam(defaultValue="") String status){return orders.list(auth,page,size,q,status);}
    @GetMapping("/{id}") public OrderResponse get(Authentication auth,@PathVariable Long id){return orders.get(auth,id);}
    @GetMapping("/requests/{clientRequestId}") public OrderResponse recover(Authentication auth,@PathVariable String clientRequestId){return orders.recover(auth,clientRequestId);}
    @PostMapping public ResponseEntity<OrderResponse> checkout(Authentication auth,@Valid @RequestBody OrderRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(orders.checkout(auth,request));}
    public record StatusRequest(@NotBlank String trangThai){}
    @PatchMapping("/{id}/status") public OrderResponse status(Authentication auth,@PathVariable Long id,@Valid @RequestBody StatusRequest request){
        return orders.changeStatus(auth,id,request.trangThai());}
}
