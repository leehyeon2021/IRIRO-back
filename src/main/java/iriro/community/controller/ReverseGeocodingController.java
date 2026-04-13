package iriro.community.controller;

import iriro.community.service.ReverseGeocodingService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
public class ReverseGeocodingController {
    private final ReverseGeocodingService reverseGeocodingService;

    // 생성자로 서비스 주입받기
    public ReverseGeocodingController(ReverseGeocodingService reverseGeocodingService){
        this.reverseGeocodingService = reverseGeocodingService;
    }

    @GetMapping("/api/test-address")
    public Map<String, Object> testAddress(@RequestParam String x , @RequestParam String y){
        return reverseGeocodingService.findAddress(x,y);
    }
}
