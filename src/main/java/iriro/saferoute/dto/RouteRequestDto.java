package iriro.saferoute.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
// Tmap API 사용
public class RouteRequestDto {
    private double startLat;
    private double startLng;
    private double endLat;
    private double endLng;
}
