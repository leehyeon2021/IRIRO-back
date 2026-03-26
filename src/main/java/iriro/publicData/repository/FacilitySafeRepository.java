package iriro.publicData.repository;

import iriro.publicData.entity.FacilitySafeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FacilitySafeRepository extends JpaRepository<FacilitySafeEntity,Integer>{

    // 저장(=업데이트) 사용 (시설 이름, 주소)
    Optional<FacilitySafeEntity> findByFacNameAndFacAdd(String facName , String facAdd);
    // 삭제(=업데이트) 사용
    List<FacilitySafeEntity> findByFacType(String facType);

}
