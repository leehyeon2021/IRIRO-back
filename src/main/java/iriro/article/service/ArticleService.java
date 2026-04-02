package iriro.article.service;

import iriro.article.dto.ArticleDto;
import iriro.article.entity.ArticleEntity;
import iriro.article.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ArticleService {
    private final ArticleRepository ar;

//    public List<ArticleDto> getArticleFindAll(){
//        return
//    }

}
