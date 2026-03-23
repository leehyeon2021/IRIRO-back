package iriro.saferoute.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@NoArgsConstructor @AllArgsConstructor @Data @Builder
public class SafeRouteResponseDto {
    private Long log_id;
    private Integer user_id;

    private BigDecimal start_latitude;
    private BigDecimal start_longitude;
    private BigDecimal end_latitude;
    private BigDecimal end_longitude;

    private Integer safety_score;
    private Integer totalTime; // 초 단위
    private Integer totalDistance; // m 단위로 저장

    private Integer rating;
    private String createdAt;

    private List<RoutePointDto> routePoints; // 경로가 들어가 있는 리스트
}
