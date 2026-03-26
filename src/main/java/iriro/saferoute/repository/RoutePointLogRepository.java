package iriro.saferoute.repository;

import iriro.saferoute.entity.RoutePointLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoutePointLogRepository extends JpaRepository<RoutePointLogEntity, Integer> {
}
