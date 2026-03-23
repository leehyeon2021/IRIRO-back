package iriro.saferoute.repository;

import iriro.saferoute.entity.LocationlogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationLogRepository extends JpaRepository<LocationlogEntity, Long> {
}
