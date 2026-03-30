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
    private BigDecimal startLatitude;
    private BigDecimal startLongitude;
    private BigDecimal endLatitude;
    private BigDecimal endLongitude;
    private Integer safetyScore;
    private Integer rating;
    private String createdAt;

    public LocationlogEntity toEntity(){
        return LocationlogEntity.builder()
                .logId(logId)
                .startLatitude(startLatitude)
                .startLongitude(startLongitude)
                .endLatitude(endLatitude)
                .endLongitude(endLongitude)
                .safetyScore(safetyScore)
                .rating(rating) // 후기(별점)
                .build();
    }
}
