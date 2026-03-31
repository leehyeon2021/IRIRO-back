package iriro.saferoute.controller;


import iriro.saferoute.dto.RouteRatingRequestDto;
import iriro.saferoute.service.RouteLogSaveService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class RouteLogSaveController {
    // 사용자가 후기를 남겨주면 로그를 저장하는 컨트롤러
    private final RouteLogSaveService routeLogSaveSvc;

    // 후기 저장
    @PatchMapping("/saverating")
    public ResponseEntity<?> saveRating(@Valid @RequestBody RouteRatingRequestDto routeRatingRequest){
        routeLogSaveSvc.updateLogRating(routeRatingRequest);
        return ResponseEntity.ok( true );
    }
}
