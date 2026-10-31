package vn.nhom14.qlns.controller;
import java.net.URI;
import java.util.Map;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.nhom14.qlns.dto.*;
import vn.nhom14.qlns.service.StaffAccountService;
@RestController @RequestMapping("/api/accounts")
public class StaffAccountController {
    private final StaffAccountService accounts;
    public StaffAccountController(StaffAccountService accounts){this.accounts=accounts;}
    @GetMapping public PageResponse<StaffAccountResponse> list(@RequestParam(defaultValue="0") int page,
        @RequestParam(defaultValue="20") int size,@RequestParam(defaultValue="") String q){return accounts.list(page,size,q);}
    @GetMapping("/{id}") public StaffAccountResponse get(@PathVariable Long id){return accounts.get(id);}
    @PostMapping public ResponseEntity<StaffAccountResponse> create(@Valid @RequestBody StaffAccountCreateRequest request){
        var result=accounts.create(request);return ResponseEntity.created(URI.create("/api/accounts/"+result.id())).body(result);}
    @PutMapping("/{id}") public StaffAccountResponse update(@PathVariable Long id,@Valid @RequestBody StaffAccountUpdateRequest request){return accounts.update(id,request);}
    @PutMapping("/{id}/password") public Map<String,String> resetPassword(@PathVariable Long id,@Valid @RequestBody StaffPasswordRequest request){
        accounts.resetPassword(id,request);return Map.of("message","Đã đặt lại mật khẩu. Nhân viên cần đăng nhập lại.");}
}
