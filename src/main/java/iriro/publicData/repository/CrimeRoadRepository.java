package iriro.publicData.repository;

import iriro.publicData.entity.CrimeRoadEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CrimeRoadRepository extends JpaRepository<CrimeRoadEntity,Integer>{}
