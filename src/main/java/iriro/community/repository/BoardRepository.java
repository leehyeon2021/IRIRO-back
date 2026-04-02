package iriro.community.repository;

import iriro.community.entity.BoardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface BoardRepository extends JpaRepository<BoardEntity,Integer> {
//    Optional<BoardEntity> findByIdAndEmail(Integer boardId,String email);
}
