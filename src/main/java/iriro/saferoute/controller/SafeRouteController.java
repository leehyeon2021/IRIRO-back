package iriro.saferoute.controller;

import iriro.saferoute.dto.RouteRequestDto;
import iriro.saferoute.service.SafeRouteService;
import iriro.saferoute.service.TmapRouteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api") //공통 주소 입력
public class SafeRouteController {

    // 테스트용 일반 경로 반환 확인
    private final TmapRouteService tmapRouteService;
    private final SafeRouteService safeRouteService;

    // 안전 경로 반환

    // 안전 경로 로그 저장

    // 일반 경로 반환(test)
    @PostMapping("/tmaptest")
    public ResponseEntity<?> test(@RequestBody RouteRequestDto routeRequestDto){
        return ResponseEntity.ok(tmapRouteService.getPedestrianRoute(routeRequestDto));
    }

    // 경유지 경로 반환(test2)
    @PostMapping("/tmaptest2")
    public ResponseEntity<?> test2(@RequestBody RouteRequestDto routeRequestDto){
        return ResponseEntity.ok( safeRouteService.getSafeRoute(routeRequestDto) );
    }

}
