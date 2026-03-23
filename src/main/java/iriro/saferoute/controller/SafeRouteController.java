package iriro.saferoute.controller;

import iriro.saferoute.service.TmapRouteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api") //공통 주소 입력
public class SafeRouteController {

    // 테스트용 일반 경로 반환 확인
    private final TmapRouteService tmapRouteService;

    // 안전 경로 반환

    // 안전 경로 로그 저장

    // 일반 경로 반환(test)
    @GetMapping("/test")
    public ResponseEntity<?> test(
            @RequestParam double startLat,
            @RequestParam double startLng,
            @RequestParam double endLat,
            @RequestParam double endLng
                                  ){
        return ResponseEntity.ok(tmapRouteService.getPedestrianRoute(startLat,startLng,endLat,endLng));
    }

}
