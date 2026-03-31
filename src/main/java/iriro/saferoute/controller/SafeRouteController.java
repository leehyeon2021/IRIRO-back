package iriro.saferoute.controller;

import iriro.community.service.JWTService;
import iriro.saferoute.dto.RouteRequestDto;
import iriro.saferoute.dto.SafeRouteResponseDto;
import iriro.saferoute.service.RouteLogSaveService;
import iriro.saferoute.service.SafeRouteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api") //공통 주소 입력
public class SafeRouteController {

    private final SafeRouteService safeRouteSvc;
    private final RouteLogSaveService routeLogSaveSvc;
    private final JWTService jwtSvc;

    // 안전 경로 반환 // http://localhost:8080/api/saferoute
    // {
    //  "startLat" : 37.3895300,
    //  "startLng" : 126.959400,
    //  "endLat" : 37.3976478,
    //  "endLng" : 126.9312600
    //}
    @PostMapping("/saferoute")
    public ResponseEntity<?> test2(@RequestBody RouteRequestDto routeRequestDto,
                                   @RequestHeader(value = "Authorization", required = false)String token){
        String email = null;

        // 이메일 토큰 값에서 가져오기
        if(token != null && token.startsWith("Bearer")){
            String realToken = token.substring(7);
            email = jwtSvc.getClaim(realToken);
        }
        //안전 거리 함수 구하기
        SafeRouteResponseDto result = safeRouteSvc.getSafeRoute(routeRequestDto);
        if(result != null){
            Long logId = routeLogSaveSvc.saveRouteLog(result, email); // 로그 아이디 가져오기
            result.setLogId( logId ); // 로그 아이디까지 응답객체에 넣기
            return ResponseEntity.ok( result ); // 반환 성공
        }
        return ResponseEntity.ok( false ); // 반환 실패
    }

}
