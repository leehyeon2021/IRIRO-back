package iriro.saferoute.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor @AllArgsConstructor @Data @Builder
public class SafeRouteResponseDto {
    private RouteResponseDto detourRoute; //경유지 우회 경로
    private Integer safety_score; //안전 점수
    private Long logId; // 저장된 로그 id 반환

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
