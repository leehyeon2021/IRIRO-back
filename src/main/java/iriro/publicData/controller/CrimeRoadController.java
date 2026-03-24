package iriro.publicData.controller;

import iriro.publicData.service.CrimeRoadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController @RequiredArgsConstructor @RequestMapping("/api")
public class CrimeRoadController {

    private final CrimeRoadService cs;

    // 범죄자도로명 조회
    @GetMapping("/crimeroad")
    public ResponseEntity<?> getCrimeRoad(){
        return ResponseEntity.ok(true);
    }
}
