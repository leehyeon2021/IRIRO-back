package iriro.saferoute.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor@NoArgsConstructor@Data@Builder
public class DetourWayPointDto {
    private BigDecimal latitude;
    private BigDecimal longitude;
}
