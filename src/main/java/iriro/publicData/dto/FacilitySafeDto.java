package iriro.publicData.dto;

import iriro.publicData.entity.FacilitySafeEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data@NoArgsConstructor@AllArgsConstructor@Builder
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

    public FacilitySafeEntity toEntity(){
        return FacilitySafeEntity.builder()
                .facId(this.facId).facType(this.facType).facSgg(this.facSgg).facName(this.facName).facAdd(this.facAdd).facLat(this.facLat).facLng(this.facLng).facCount(this.facCount).facUse(this.facUse).facTel(this.facTel)
                .build();
    }
}