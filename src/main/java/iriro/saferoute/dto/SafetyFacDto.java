package iriro.saferoute.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor @NoArgsConstructor @Data @Builder
public class SafetyFacDto {

    private String facType; // 안전시설물 타입
    private BigDecimal latitude; // 위도
    private BigDecimal longitude; // 경도

}
