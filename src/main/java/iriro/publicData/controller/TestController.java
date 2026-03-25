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

    // 범죄자거주도로명주소
    @GetMapping("/test1")
    public ResponseEntity<?> test1(){
        return ResponseEntity.ok(cf.fetchCrimeRoad());
//        return ResponseEntity.ok(cf.fetchCrimeRoad());
    }
}
