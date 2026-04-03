package iriro.saferoute.controller;

import iriro.saferoute.service.MapMarkerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/map/marker")
@CrossOrigin( value = "http://localhost:5173") //도메인 허용 규칙
public class MapMarkerController {

    private final MapMarkerService mapMarkerSvc;

    // 안전 마커 찍기
    @GetMapping("/safe")
    public ResponseEntity<?> getSafeMarker(@RequestParam Double latitude, @RequestParam Double longitude){
        return ResponseEntity.ok( mapMarkerSvc.getSafeMark(latitude, longitude));
    }

    // 위험 마커 찍기
    @GetMapping("/danger")
    public ResponseEntity<?> getDangerMarker(@RequestParam Double latitude, @RequestParam Double longitude){
        return ResponseEntity.ok( mapMarkerSvc.getDangerMark(latitude, longitude));
    }
}
