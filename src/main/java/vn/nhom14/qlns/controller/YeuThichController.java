package vn.nhom14.qlns.controller;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import vn.nhom14.qlns.service.YeuThichService;
import vn.nhom14.qlns.dto.PageResponse;
import vn.nhom14.qlns.dto.SachResponse;

@RestController
@RequestMapping("/api/yeu-thich")
public class YeuThichController {
    private final YeuThichService service;
    public YeuThichController(YeuThichService service){this.service=service;}
    @GetMapping public PageResponse<SachResponse> list(Authentication auth,@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="20") int size){return service.list(auth.getName(),page,size);}
    @GetMapping("/ids") public List<Long> ids(Authentication auth){return service.ids(auth.getName());}
    @GetMapping("/the-loai") public YeuThichService.TongHop summary(Authentication auth){return service.summary(auth.getName());}
    @PutMapping("/{sachId}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void save(Authentication auth,@PathVariable long sachId){service.save(auth.getName(),sachId);}
    @DeleteMapping("/{sachId}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remove(Authentication auth,@PathVariable long sachId){service.remove(auth.getName(),sachId);}
}
