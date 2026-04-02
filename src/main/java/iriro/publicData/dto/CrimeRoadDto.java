package iriro.publicData.dto;

import iriro.publicData.entity.CrimeRoadEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CrimeRoadDto {
    private Integer criId;
    private Integer criZip;
    private String criSgg;
    private String criRoad;
    private String criType;
    private Double criLat;
    private Double criLng;
    private Integer criCount;
}
