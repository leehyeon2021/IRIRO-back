package iriro.saferoute.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor @NoArgsConstructor @Data @Builder
public class SafetyFacPointDto {
    private String facType; // 안전시설물 타입
    private int safeCount; // 안전 시설물 개수
    private Double latitude; // 위도
    private Double longitude; // 경도
}
