package iriro.publicData.repository;

import iriro.publicData.entity.FacilitySafeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FacilitySafeRepository extends JpaRepository<FacilitySafeEntity,Integer>{
    List<FacilitySafeEntity> findByFacType( String facType );
}
