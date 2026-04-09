package iriro.article.controller;

import iriro.article.scheduler.ArticleCrawlingScheduler;
import iriro.article.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
@CrossOrigin( value = "http://localhost:5173")
public class ArticleController {

    private final ArticleService articleService;

    // 기사 전체 조회
    @GetMapping("/list")
    public ResponseEntity<?> getArticleFindAll(){
        return ResponseEntity.ok(articleService.getArticleFindAll());
    }

    // 기사 지역 선택 전체 조회
    @GetMapping("/search")
    public ResponseEntity<?> getArticleSearch(@RequestParam String articleDistrict){
        return ResponseEntity.ok(articleService.getArticleSearch(articleDistrict));
    }
    // 기사 개별 조회
    @GetMapping("/find")
    public ResponseEntity<?> getArticleFindOne(@RequestParam Integer articleId ){
        return  ResponseEntity.ok(articleService.getArticleFindOne(articleId));
    }



    // +) 크롤링 테스트
    private final ArticleCrawlingScheduler crawler;
    @GetMapping("/crawl")
    public String startCrawlingTest() {
        System.out.println("크롤링 수동 시작");
        crawler.crawlAll();
        return "크롤링 테스트 완료";
    }
}