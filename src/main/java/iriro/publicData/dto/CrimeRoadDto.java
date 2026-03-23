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

    public CrimeRoadEntity toEntity(){
        return CrimeRoadEntity.builder()
                .criId(this.criId).criZip(this.criZip).criSgg(this.criSgg).criRoad(this.criRoad)
                .build();
    }
}
