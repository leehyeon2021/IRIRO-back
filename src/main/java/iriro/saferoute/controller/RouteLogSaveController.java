package iriro.saferoute.controller;


import iriro.saferoute.service.RouteLogSaveService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class RouteLogSaveController {
    // 사용자가 후기를 남겨주면 로그를 저장하는 컨트롤러
    private final RouteLogSaveService routeLogSaveSvc;

    // 후기 저장
    @PostMapping("/savelog")
    public ResponseEntity<?> saveLog(){
        return ResponseEntity.ok( true );
    }
}
