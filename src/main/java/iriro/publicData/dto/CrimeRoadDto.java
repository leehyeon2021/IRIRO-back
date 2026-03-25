package iriro.publicData.dto;

import iriro.publicData.entity.CrimeRoadEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor @NoArgsConstructor @Data @Builder
public class CrimeRoadDto {
    private Integer criId;
    private Integer criZip;
    private String criSgg;
    private String criRoad;
    private String criType;
    private Double criLat;
    private Double criLng;

    public CrimeRoadEntity toEntity(){ // 저장 없어서 쓸 일 없을 수도
        return CrimeRoadEntity.builder()
                .criId(this.criId).criZip(this.criZip).criSgg(this.criSgg).criRoad(this.criRoad)
                .criLat(this.criLat).criLng(this.criLng)
                .build();
    }
}
