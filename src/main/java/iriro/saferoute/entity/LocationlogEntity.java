package iriro.saferoute.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

@Entity
@Table(name = "location_log")
@Data
@Builder
public class LocationlogEntity {

    @Id
    @GeneratedValue( strategy = GenerationType.AUTO)
    private Long log_id;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id")
//    private UserEntity userEntity;  추후에 유저엔티티가져오기

    @Column(nullable = false)
    private double start_latitude;

    @Column(nullable = false)
    private double start_longitude;

    @Column(nullable = false)
    private double end_latitude;

    @Column(nullable = false)
    private double end_longitude;

    @Column(nullable = false)
    private Integer safety_score;

    @Column(nullable = false)
    private Integer rating;

}
