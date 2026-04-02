package iriro.article.controller;

import iriro.article.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/articles")
public class ArticleController {
    private final ArticleService as;

//    @GetMapping("/find")
//    public ResponseEntity<?> getArticleFindAll(){
//        return ResponseEntity.ok(as.getArticleFindAll());
//    }
}
