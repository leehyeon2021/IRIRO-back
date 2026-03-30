package iriro.saferoute.dto;

import iriro.saferoute.entity.LocationlogEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class SaveLogDto {

    private Long logId;
    private Integer userId;
    private Double startLatitude;
    private Double startLongitude;
    private Double endLatitude;
    private Double endLongitude;
    private Integer totalTime;
    private Integer totalDistance;
    private Integer safetyScore;
    private Integer rating;
    private String createdAt;

    public LocationlogEntity toEntity(){
        return LocationlogEntity.builder()
                .logId(logId)
                .startLatitude(BigDecimal.valueOf(startLatitude) )
                .startLongitude(BigDecimal.valueOf(startLongitude) )
                .endLatitude(BigDecimal.valueOf(endLatitude) )
                .endLongitude(BigDecimal.valueOf(endLongitude) )
                .total_distance(totalDistance)
                .total_time(totalTime)
                .safetyScore(safetyScore)
                .rating(rating) // 후기(별점)
                .build();
    }
}
