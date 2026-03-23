package iriro.saferoute.entity;

import iriro.community.entity.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
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
public class LocationlogEntity{

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
    private Integer safetyScore;

    @Column(nullable = false)
    private Integer rating;

    // 생성 일자만 엔티티 생성
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

}
