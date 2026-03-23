package iriro.saferoute.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class RoutePointDto {
    private BigDecimal lat; // 위도
    private BigDecimal lng; // 경도
    private Integer sequence; // 경로 순서 -> 몇 번째 지점이 위험구역인지 판단
}
