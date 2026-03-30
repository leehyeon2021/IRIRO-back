package iriro.saferoute.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor @AllArgsConstructor @Data @Builder
public class SafeRouteResponseDto {
    private RouteResponseDto detourRoute; //경유지 우회 경로
    private Integer safety_score;

//    public SaveLogDto toSaveLogDto(){
//        return SaveLogDto.builder()
//                .startLatitude()
//                .startLongitude()
//                .endLatitude()
//                .endLongitude()
//                .safetyScore()
//                .createdAt()
//                .build();
//    }
}
