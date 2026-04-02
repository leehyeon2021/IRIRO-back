package iriro.article.service;

import iriro.article.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CrawlingService {
    private final ArticleRepository ar;

    // 1. Jsoup 이용 정보 수집
    public List<Map<String, Object>>
}
