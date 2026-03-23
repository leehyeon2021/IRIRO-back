package iriro.publicData.entity;

import iriro.publicData.dto.CrimeRoadDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity @Table(name = "crime_road")
@NoArgsConstructor @AllArgsConstructor
@Data @Builder
public class CrimeRoadEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cri_id")
    private Integer criId;
    @Column(name = "cri_zip")
    private Integer criZip;
    @Column(name = "cri_sgg", length = 50)
    private String criSgg;
    @Column(name = "cri_road", length = 300)
    private String criRoad;

    public CrimeRoadDto toDto(){
        return CrimeRoadDto.builder()
                .criId(this.criId).criZip(this.criZip).criSgg(this.criSgg).criRoad(this.criRoad)
                .build();
    }


}
