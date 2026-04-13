package iriro.saferoute.entity;

import iriro.common.entity.BaseTime;
import iriro.community.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// 회원번호1번 -> 비회원

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "location_log")
public class LocationlogEntity extends BaseTime {

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    private Long logId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;

    @Column(nullable = false, precision = 10, scale = 7) // 총 10자리 중에 소수점 이하 자리는 7자리 == decimal(10,7)
    private BigDecimal startLatitude;

    @Column(nullable = false, precision = 10, scale = 7)
    private BigDecimal startLongitude;

    @Column(nullable = false, precision = 10, scale = 7)
    private BigDecimal endLatitude;

    @Column(nullable = false, precision = 10, scale = 7)
    private BigDecimal endLongitude;

    @Column(nullable = false)
    private Integer total_time;

    @Column(nullable = false)
    private Integer total_distance;

    @Column(nullable = false)
    private Integer safetyScore;

    private Integer rating; // (처음엔 null 그 다음엔 업데이트)

}
