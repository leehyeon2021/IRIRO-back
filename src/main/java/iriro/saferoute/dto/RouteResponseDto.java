package iriro.saferoute.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class RouteResponseDto {
    private Double start_latitude;
    private Double start_longitude;
    private Double end_latitude;
    private Double end_longitude;

    private Integer totalTime;
    private Integer totalDistance;

    private List<RoutePointDto> routePoints; // 경로가 들어가 있는 리스트

    public SaveLogDto toSaveLogDto(){
        return SaveLogDto.builder()
                .startLatitude(start_latitude)
                .startLongitude(start_longitude)
                .endLatitude(end_latitude)
                .endLongitude(end_longitude)
                .totalDistance(totalDistance)
                .totalTime(totalTime)
                .build();
    }
}
