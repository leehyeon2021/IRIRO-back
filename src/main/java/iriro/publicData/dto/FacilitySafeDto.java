package iriro.publicData.dto;

import iriro.publicData.entity.FacilitySafeEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class FacilitySafeDto {
    private Integer facId;
    private String facType;
    private String facSgg;
    private String facName;
    private String facAdd;
    private Double facLat;
    private Double facLng;
    private Integer facCount;
    private String facUse;
    private String facTel;
}