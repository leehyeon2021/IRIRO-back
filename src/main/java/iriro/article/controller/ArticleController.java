package iriro.article.controller;

import iriro.article.service.ArticleCrawlingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleCrawlingService crawlingService;

    @GetMapping("/crawl")
    public String startCrawlingTest() {
        System.out.println("크롤링 수동 시작");
        crawlingService.crawlAll();
        return "크롤링 테스트가 완료";
    }
}