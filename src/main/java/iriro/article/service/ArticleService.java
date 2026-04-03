package iriro.article.service;

import iriro.article.dto.ArticleDto;
import iriro.article.entity.ArticleEntity;
import iriro.article.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArticleService {
    private final ArticleRepository articleRepository;

    // 기사 전체 조회
    public List<ArticleDto> getArticleFindAll(){
        return articleRepository.findAll().stream()
                .map(ArticleEntity::toDto)
                .collect(Collectors.toList());
    }

    // 기사 지역 선택 전체 조회
    public List<ArticleDto> getArticleSearch(String articleDistrict){
        return articleRepository.findByArticleDistrict(articleDistrict).stream()
                .map(ArticleEntity::toDto).collect(Collectors.toList());
    }

    // 기사 개별 조회
    public ArticleDto getArticleFindOne(Integer articleId){
        return articleRepository.findById(articleId)
                .map(ArticleEntity::toDto)
                .orElse(null);
    }


}
