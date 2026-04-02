package iriro.saferoute.controller;

import iriro.saferoute.service.MapMarkerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/map/markers")
public class MapMarkerController {

   private final MapMarkerService mapMarkerSvc;

    @GetMapping("/safe")
    public ResponseEntity<?> getSafeMark(@RequestParam Double latitude, @RequestParam Double longitude){
     return ResponseEntity.ok( mapMarkerSvc.getSafeMark(latitude, longitude));
    }

    @GetMapping("/danger")
    public ResponseEntity<?> getDangerMark(@RequestParam Double latitude, @RequestParam Double longitude){
     return ResponseEntity.ok( mapMarkerSvc.getDangerMark(latitude, longitude));
    }
}
