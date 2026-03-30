package iriro.publicData.repository;

import iriro.publicData.entity.CrimeRoadEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface CrimeRoadRepository extends JpaRepository<CrimeRoadEntity,Integer>{

    // 위험 테이블 범위 기준 조회
    @Query( value = "select * from crime_road r where r.cri_lat between :minLat and :maxLat and r.cri_lng between :minLng and :maxLng",
            nativeQuery = true)
    List<CrimeRoadEntity> findAllInBoundingBox(
            @Param("minLat") BigDecimal minLat, @Param("maxLat") BigDecimal maxLat,
            @Param("minLng") BigDecimal minLng, @Param("maxLng") BigDecimal maxLng
    );

    // 저장 시 사용 (시설 이름, 주소로 중복 검사)
    Optional<CrimeRoadEntity> findByCriSggAndCriRoad(String criSgg, String criRoad);


}
