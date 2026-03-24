package iriro.saferoute.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class SafeRouteRequestDto {
    private double startLat;
    private double startLng;
    private double endLat;
    private double endLng;
}
