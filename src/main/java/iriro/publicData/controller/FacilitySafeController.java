package iriro.publicData.controller;

import iriro.publicData.service.FacilitySafeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class FacilitySafeController {
    private final FacilitySafeService fs;

    // 조회
    // 1. 경찰서 조회
    @GetMapping("/policeStation")
    public ResponseEntity<?> getPoliceStation(){
        return ResponseEntity.ok(fs.getPoliceStation());
    }

    // 2. 안심지킴이집 조회
    @GetMapping("/safeHouse")
    public ResponseEntity<?> getSafeHouse(){return ResponseEntity.ok(fs.getSafeHouse());}

    // 3. 보안등 조회
    @GetMapping("/safeLight")
    public ResponseEntity<?> getSafeLight(){return ResponseEntity.ok(fs.getSafeLight());}

    // 4. CCTV 조회
    @GetMapping("/cctv")
    public ResponseEntity<?> getCctv(){return ResponseEntity.ok(fs.getCctv());}

    // 5. 안전벨 조회
    @GetMapping("/safeBell")
    public ResponseEntity<?> getSafeBell(){return ResponseEntity.ok(fs.getSafeBell());}

}
