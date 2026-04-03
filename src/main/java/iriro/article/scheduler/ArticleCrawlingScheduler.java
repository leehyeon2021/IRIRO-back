package iriro.article.scheduler;

import iriro.article.crawler.ArticleCrawler;
import iriro.article.util.RegionConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ArticleCrawlingScheduler {

    private final ArticleCrawler crawler;

    // 매일 오전 6시 자동 실행
    @Scheduled(cron = "0 0 6 * * *")
    public void crawlAll() {
        System.out.println("=== 서울 범죄 뉴스 크롤링 시작 ===");

        // 2. 지역구 하나씩 검색
        for (String district : RegionConstants.seoulDistricts) {

            String keyword = district + " 범죄";

            // 1. 노컷뉴스 크롤링 (Selenium 사용)
            System.out.println("[노컷뉴스] 크롤링 시작");
            crawler.crawlNoCutNews(keyword, district);

            // 2. 머니투데이 크롤링 (Jsoup 사용)
            System.out.println("[머니투데이] 크롤링 시작");
            crawler.crawlMtNews(keyword, district);

            // 3. 안전장치: 한 지역구 끝날 때마다 대기
            try {
                System.out.println("다음 지역 대기 (10초)");
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                System.out.println("대기 중 인터럽트: " + e.getMessage());
                Thread.currentThread().interrupt();
            }
        }
        System.out.println("=== 서울 전체 범죄 뉴스 크롤링 종료 ===");
    }
}
