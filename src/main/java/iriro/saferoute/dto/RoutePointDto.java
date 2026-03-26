package iriro.saferoute.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class RoutePointDto {
    private Double latitude; // 위도
    private Double longitude; // 경도
    private Integer sequence; // 경로 순서 -> 몇 번째 지점이 위험구역인지 판단
}
