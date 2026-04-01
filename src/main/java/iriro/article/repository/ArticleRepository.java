package iriro.article.repository;

import iriro.article.entity.ArticleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleRepository extends JpaRepository<ArticleEntity, Integer> {
    // URL 중복 체크용
    boolean existsByArticleUrl(String articleUrl);
}