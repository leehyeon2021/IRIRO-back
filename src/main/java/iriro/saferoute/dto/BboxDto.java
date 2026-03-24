package iriro.saferoute.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BboxDto {
    private double minLat;
    private double maxLat;
    private double minLng;
    private double maxLng;
}
