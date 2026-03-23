package iriro.saferoute.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class SafeRouteRequest {
    private double startLat;
    private double startLng;
    private double endLat;
    private double endLng;
}
