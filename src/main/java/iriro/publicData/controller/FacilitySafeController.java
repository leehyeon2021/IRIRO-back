package iriro.publicData.controller;

import iriro.publicData.service.FacilitySafeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController @RequestMapping("/api")
public class FacilitySafeController {
    private FacilitySafeService fs;

    // 조회
    // 1. 경찰서 조회

    // 2. 안심지킴이집 조회
    // 3. 보안등 조회
    // 4. CCTV 조회
    // 5. 안전벨 조회
}
