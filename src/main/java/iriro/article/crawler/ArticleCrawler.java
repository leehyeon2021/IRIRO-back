package iriro.article.crawler;

import io.github.bonigarcia.wdm.WebDriverManager;
import iriro.article.repository.ArticleRepository;
import iriro.article.service.ArticleSaveService;
import iriro.article.util.ArticleCrimeFilter;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ArticleCrawler {

    private final ArticleRepository articleRepository;
    private final ArticleCrimeFilter filter;
    private final ArticleSaveService articleSaveService;

    // 최대 개수 조절
    private static final int maxCount = 1;
    private static final int maxPage = 1;
    private static final String[] thisYear = {"2026", "2025"};

    // 1. 노컷뉴스 크롤러 (Selenium으로 목록 가져오기 -> Jsoup으로 본문 읽기 -> Selenium으로 다음페이지 클릭)
    public void crawlNoCutNews(String keyword, String district) {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new", "--disable-gpu");
        WebDriver driver = new ChromeDriver(options);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        int totalCount = 0;

        try {
            String searchUrl = "https://search.nocutnews.co.kr/list?query="
                                + URLEncoder.encode(keyword, "UTF-8");
            driver.get(searchUrl);

            // 페이지 넘기기
            for(int page = 1; page <= maxPage; page++) {

                // maxCount 달성 시 종료
                if(totalCount >= maxCount) {
                    System.out.println("[노컷뉴스] "+totalCount+"개 수집 완료: 종료");
                    break;
                }

                try {
                    // 검색 결과 렌더링 대기
                    wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".content > #news > .newslist")));
                }catch (Exception e){
                    System.out.println("[노컷뉴스] "+page+"페이지 로딩 실패: 종료");
                    break;
                }

                // 대기
                try {
                    System.out.println("[다음 페이지 이동] 3초");
                    Thread.sleep(3000);
                }catch (InterruptedException e){
                    System.out.println("[다음 페이지 이동 중 인터럽트 발생: 종료] " + e.getMessage());
                    Thread.currentThread().interrupt();
                    return;
                }

                // 현재 페이지 목록
                List<WebElement> articles = driver.findElements(By.cssSelector(".newslist > li"));
                if( articles.isEmpty() ){
                    System.out.println("[노컷뉴스] 기사 없음: 종료");
                    break;
                }

                // 기사 하나
                for (WebElement article : articles) {
                    try {
                        if(totalCount >= maxCount){ break; }

                        try {
                            System.out.println("[다음 기사 이동] 2.7초");
                            Thread.sleep(2700);
                        }catch (InterruptedException e){
                            System.out.println("[다음 기사 이동 중 인터럽트 발생: 종료] " + e.getMessage());
                            Thread.currentThread().interrupt();
                            return;
                        }

                        String date = article.findElement(By.cssSelector(".txt > span")).getText().replace(".", "-").trim();
                        boolean isTargetYear = Arrays.stream(thisYear).anyMatch(date::contains);
                        if(!isTargetYear){
                            System.out.println("[날짜 부적절] "+date+" (건너뜀)");
                            continue;
                        }
                        String title = article.findElement(By.cssSelector("a > strong")).getText().trim();
                        String url = article.findElement(By.cssSelector("a")).getAttribute("href");
                        String pic = article.findElement(By.cssSelector(".img > a > img")).getAttribute("src");

                        // URL 없거나 이미 저장된 기사 건너뜀
                        if (title.isEmpty() || url.isEmpty() || articleRepository.existsByArticleUrl(url)) {
                            continue;
                        }

                        // 상세 페이지 본문 정보
                        Map<String, String> details = fetchArticleDetails(url);
                        String content = details.get("content");
                        String writer = details.get("writer");

                        // 사회/문화 html 넘기기
                        if (writer.contains("메일보내기")) {
                            System.out.println("[다른 형식의 뉴스 (건너뜀)] " + title);
                            continue;
                        }

                        // AI 묻고 아니면 넘김
                        if (!filter.isValid(title, content)) {
                            continue;
                        }
                        // 맞으면 저장
                        articleSaveService.saveToDb(title, url, content, "노컷뉴스", district, keyword, date, writer, pic);
                        // 저장 성공 count 1 증가
                        totalCount++;

                    } catch (Exception e) {
                        System.out.println("[개별 기사 파싱 중 오류 (건너뜀)] " + e.getMessage());
                    }
                } // 기사 하나 end
                if(totalCount >= maxCount){ break; }

                // 다음 페이지 넘기기 (Selenium 직접클릭)
                try{
                    int nextPage = page + 1;
                    WebElement nextBtn = driver.findElement(By.cssSelector("#cphBody_cphBody_pcPager a[title='"+nextPage+"']"));
                    nextBtn.click();

                    // 대기
                    try {
                        System.out.println("[다음 페이지 이동] 3초");
                        Thread.sleep(3000);
                    }catch (InterruptedException e){
                        System.out.println("[다음 페이지 이동 중 인터럽트 발생: 종료] " + e.getMessage());
                        Thread.currentThread().interrupt();
                        return;
                    }

                }catch (Exception e){
                    System.out.println("[노컷뉴스] 다음 페이지 없음: 종료");
                    break;
                } // 페이지 이동 end
            } // 페이지 넘기기 end
        } catch (Exception e) {
            System.out.println("[노컷뉴스 전체 오류] " + e.getMessage());
        } finally {
            driver.quit();
        }
    }

    // 2. 머니투데이 크롤러 (목록, 본문 전부 Jsoup -> &page=페이지 넘기기)
    public void crawlMtNews(String keyword, String district) {

        int totalCount = 0;

        try {
            // 페이지 넘기기
            for(int page = 1; page <= maxPage; page++) {

                if(totalCount >= maxCount) {
                    System.out.println("[머니투데이] "+totalCount+"개 수집 완료");
                    break;
                }

                // 대기
                try {
                    System.out.println("[다음 페이지 이동] 3초");
                    Thread.sleep(3000);
                }catch (InterruptedException e){
                    System.out.println("[다음 페이지 이동 중 인터럽트 발생: 종료] " + e.getMessage());
                    Thread.currentThread().interrupt();
                    return;
                }

                String searchUrl = "https://www.mt.co.kr/search/news"
                        + "?filter=contents"
                        + "&order=latest"
                        + "&keyword=" + URLEncoder.encode(keyword, "UTF-8")
                        + "&page=" + page;

                System.out.println("[머니투데이] "+page+" 페이지");

                Document doc = Jsoup.connect(searchUrl)
                        .userAgent("Mozilla/5.0")
                        .timeout(5000)
                        .get();

                Elements articles = doc.select(".article_item");

                if(articles.isEmpty()){
                    System.out.println("[머니투데이] 기사 없음: 종료");
                    break;
                }

                // 기사 하나
                for (Element article : articles) {
                    try {
                        if(totalCount >= maxCount){ break; }

                        // 대기
                        try {
                            System.out.println("[다음 기사 이동] 2.7초");
                            Thread.sleep(2700);
                        }catch (InterruptedException e){
                            System.out.println("[다음 기사 이동 중 인터럽트 발생: 종료] " + e.getMessage());
                            Thread.currentThread().interrupt();
                            return;
                        }

                        String date = article.select(".article_date").text().replace(".", "-").trim();
                        boolean isTargetYear = Arrays.stream(thisYear).anyMatch(date::contains);
                        if(!isTargetYear){
                            System.out.println("[날짜 부적절] "+date+" (건너뜀)");
                            continue;
                        }
                        String title = article.select(".headline").text().trim();
                        String url = article.select("a").attr("abs:href");
                        String pic = article.select(".article_body > .thumb > img").attr("src");
                        String writer = article.select(".writer").text().replace(" 기자", "").trim();

                        // URL 없거나 이미 저장된 기사 건너뜀
                        if (title.isEmpty() || url.isEmpty() || articleRepository.existsByArticleUrl(url)) {
                            continue;
                        }

                        // 상세 페이지 본문 정보
                        Map<String, String> details = fetchArticleDetails(url);
                        String content = details.get("content");

                        // AI 묻고 아니면 넘김
                        if (!filter.isValid(title, content)) {
                            continue;
                        }
                        // 맞으면 저장
                        articleSaveService.saveToDb(title, url, content, "머니투데이", district, keyword, date, writer, pic);
                        // 저장 성공 count 1 증가
                        totalCount++;

                    } catch (Exception e) {
                        System.out.println("[개별 기사 파싱 중 오류 (건너뜀)] " + e.getMessage());
                    }
                } // 기사 하나 end
            } // 페이지 넘기기 end
        } catch (Exception e) {
            System.out.println("[머니투데이 전체 오류] " + e.getMessage());
        }
    }


    // * 기사 본문 상세 (본문, 기자)
    private Map<String, String> fetchArticleDetails(String articleUrl){
        Map<String, String> result = new HashMap<>();
        result.put("content", "");
        result.put("writer", "");

        try{
            Document doc = Jsoup.connect(articleUrl)
                    .userAgent("Mozilla/5.0")
                    .timeout(5000)
                    .get();

            // 본문 가져오기
            Element body = doc.selectFirst("div#textBody, div#pnlContent, article, div.article-body, div.news-body, div#articleBody");
            if(body != null){
                result.put("content", body.text());
            }

            // 기자 이름 가져오기
            Element writer = doc.selectFirst("li.email > a, a.a_reporter > strong");
            if (writer != null) {
                String cleanWriter = writer.text().replace("CBS노컷뉴스", "").replace("기자", "").trim();
                result.put("writer", cleanWriter);
            }

        } catch (Exception e) {
            System.out.println("[상세 페이지 파싱 오류] " + e.getMessage());
        }

        return result;
    }
}
