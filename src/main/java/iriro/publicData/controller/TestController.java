package iriro.publicData.controller;

import iriro.publicData.service.CrimeRoadFetchService;
import iriro.publicData.service.FacilitySafeFetchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor @RestController
@RequestMapping("/api")
public class TestController {
    private final CrimeRoadFetchService cf;
    private final FacilitySafeFetchService ff;

    // 저장 기능을 테스트하기 위한 컨트롤러입니다.

    // 위험 도로
    @GetMapping("/test1/cri")
    public ResponseEntity<?> test1(){
        return ResponseEntity.ok(cf.fetchCrimeRoad());
    }

    // 안심지킴이집
    @GetMapping("/test2/safehouse")
    public ResponseEntity<?> test2(){
        return ResponseEntity.ok(ff.fetchSafeHouse());
    }

    // 경찰서(치안안전시설)
    @GetMapping("/test2/police")
    public ResponseEntity<?> test3(){
        return ResponseEntity.ok(ff.fetchPoliceStation());
    }

    // 안전시설물
    @GetMapping("/test2/safefac")
    public ResponseEntity<?> test4(){
        return ResponseEntity.ok(ff.fetchSafeFac());
    }
}
