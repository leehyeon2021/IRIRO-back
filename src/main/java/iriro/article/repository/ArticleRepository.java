package iriro.article.repository;

import iriro.article.entity.ArticleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<ArticleEntity, Integer> {

    // URL 중복 체크용
    boolean existsByArticleUrl(String articleUrl);
    // 키워드 중복 체크용
    List<ArticleEntity> findTop20ByArticleDistrictOrderByArticleCreatedAtDesc(String district);

    // 지역 선택 전체 조회
    List<ArticleEntity> findByArticleDistrict(String articleDistrict);
}