package iriro.saferoute.controller;

import iriro.saferoute.dto.RouteRequestDto;
import iriro.saferoute.dto.SafeRouteResponseDto;
import iriro.saferoute.service.RouteLogSaveService;
import iriro.saferoute.service.SafeRouteService;
import iriro.saferoute.service.TmapRouteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api") //공통 주소 입력
public class SafeRouteController {

    private final SafeRouteService safeRouteSvc;
    private final RouteLogSaveService routeLogSaveSvc;

    // 안전 경로 반환 // http://localhost:8080/api/saferoute
    // {
    //  "startLat" : 37.3895300,
    //  "startLng" : 126.959400,
    //  "endLat" : 37.3976478,
    //  "endLng" : 126.9312600
    //}
    @PostMapping("/saferoute")
    public ResponseEntity<?> test2(@RequestBody RouteRequestDto routeRequestDto){
        SafeRouteResponseDto result = safeRouteSvc.getSafeRoute(routeRequestDto);
        if(result != null){
            Long logId = routeLogSaveSvc.createRouteLog(null);
            routeLogSaveSvc.saveLogRoute(logId, result.getDetourRoute().getRoutePoints());
            return ResponseEntity.ok( result );
        }
        return ResponseEntity.ok( false ); // 반환 실패
    }

}
