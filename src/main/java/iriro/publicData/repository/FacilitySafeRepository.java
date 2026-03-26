package iriro.publicData.repository;

import iriro.publicData.entity.CrimeRoadEntity;
import iriro.publicData.entity.FacilitySafeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface FacilitySafeRepository extends JpaRepository<FacilitySafeEntity,Integer>{
    List<FacilitySafeEntity> findByFacType( String facType );

    // 안전 테이블 범위 기준 조회
    @Query( value = "select * from facility_safe r where r.latitude between :minLat and :maxLat and r.longitude between :minLng and :maxLng",
            nativeQuery = true)
    List<FacilitySafeEntity> findAllInBoundingBox(
            @Param("minLat") BigDecimal minLat, @Param("maxLat") BigDecimal maxLat,
            @Param("minLng") BigDecimal minLng, @Param("maxLng") BigDecimal maxLng
    );
}
