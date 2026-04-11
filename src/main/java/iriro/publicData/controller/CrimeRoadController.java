package iriro.publicData.controller;

import iriro.publicData.service.CrimeRoadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@CrossOrigin( value = "http://localhost:5173" , exposedHeaders = "Authorization")
public class CrimeRoadController {

    private final CrimeRoadService cs;

    // 범죄자도로명 조회
    // (너무 오래 걸린다면: CrimeRoadService에서 totalPages를 (for문에 직접)임의 값으로 변경)
    @GetMapping("/crimeroad")
    public ResponseEntity<?> getCrimeRoad(){
        return ResponseEntity.ok(cs.getCrimeRoad());
    }
}
