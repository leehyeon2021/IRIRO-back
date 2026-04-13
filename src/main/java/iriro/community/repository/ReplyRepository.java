package iriro.community.repository;

import iriro.community.entity.ReplyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ReplyRepository extends JpaRepository<ReplyEntity,Integer> {
    List<ReplyEntity> findByBoardEntity_BoardId(Integer boardId);
}
