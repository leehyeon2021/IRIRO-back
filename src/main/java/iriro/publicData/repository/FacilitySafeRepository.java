package iriro.publicData.repository;

import iriro.publicData.entity.FacilitySafeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface FacilitySafeRepository extends JpaRepository<FacilitySafeEntity,Integer>{

    // 안전 테이블 범위 기준 조회
    @Query( value = "select * from facility_safe r where r.fac_lat between :minLat and :maxLat and r.fac_lng between :minLng and :maxLng",
            nativeQuery = true)
    List<FacilitySafeEntity> findAllInBoundingBox(
            @Param("minLat") BigDecimal minLat, @Param("maxLat") BigDecimal maxLat,
            @Param("minLng") BigDecimal minLng, @Param("maxLng") BigDecimal maxLng
    );

    // 저장 사용 (시설 이름, 주소로 중복 검사)
    Optional<FacilitySafeEntity> findByFacNameAndFacAdd(String facName , String facAdd);
    // 저장 사용 (안전시설 타입 확인)
    List<FacilitySafeEntity> findByFacType(String facType);
    // 저장 사용 (안전시설-보안등,CCTV,안전벨) 사용 (IN 연산자)
    List<FacilitySafeEntity> findByFacTypeIn(List<String> type);

}
