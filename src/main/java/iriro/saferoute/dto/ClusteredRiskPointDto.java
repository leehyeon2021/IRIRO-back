package iriro.saferoute.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor@NoArgsConstructor@Data@Builder
public class ClusteredRiskPointDto {
    private BigDecimal lat;
    private BigDecimal lng;
    private int count;
    private List<RoutePointDto> members;
}
