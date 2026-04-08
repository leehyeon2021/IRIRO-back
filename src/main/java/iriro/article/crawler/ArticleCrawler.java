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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ArticleCrawler {

    private final ArticleRepository articleRepository;
    private final ArticleCrimeFilter filter;
    private final ArticleSaveService articleSaveService;

    // 1. 노컷뉴스 크롤러 (Selenium으로 목록 가져오기 -> Jsoup으로 본문 읽기)
    public void crawlNoCutNews(String keyword, String district) {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new", "--disable-gpu");
        WebDriver driver = new ChromeDriver(options);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            String searchUrl = "https://search.nocutnews.co.kr/list?query="
                                + URLEncoder.encode(keyword, "UTF-8");
            driver.get(searchUrl);

            // 검색 결과 렌더링 대기
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".content > #news > .newslist")));

            // 기사 목록 (1페이지)
            List<WebElement> articles = driver.findElements(By.cssSelector(".newslist > li"));

            // 기사 몇 개 가져왔는지 세기
            int count = 0;

            for (WebElement article : articles) {
                try{
                    // 대기하기
                    System.out.println("2.7초 대기");
                    Thread.sleep(2700);

                    // n개 다 채웠으면 반복문을 강제 종료
                    if (count >= 10) {
                        System.out.println(count+"개 수집. 노컷뉴스 크롤링 종료.");
                        break;
                    }

                    String title = article.findElement(By.cssSelector("a > strong")).getText().trim();
                    String url = article.findElement(By.cssSelector("a")).getAttribute("href");
                    String pic = article.findElement(By.cssSelector(".img > a > img")).getAttribute("src");
                    String date = article.findElement(By.cssSelector(".txt > span")).getText().replace(".","-").trim();

                    // URL 없거나 이미 저장된 기사 건너뜀
                    if (title.isEmpty() || url.isEmpty() || articleRepository.existsByArticleUrl(url)) {
                        continue;
                    }

                    // 상세 페이지 본문 정보
                    Map<String, String> details = fetchArticleDetails(url);
                    String content = details.get("content");
                    String writer = details.get("writer");

                    // 사회/문화 html 넘기기
                    if( writer.contains("메일보내기")){
                        System.out.println("다른 형식의 뉴스 건너뛰기: " + title);
                        continue;
                    }

                    // AI에게 묻고
                    boolean isCrimeNews = filter.isValid(title, content);
                    // 아니면 넘김
                    if(!isCrimeNews){continue;}
                    // 맞으면 저장
                    articleSaveService.saveToDb(title, url, content, "노컷뉴스", district, keyword, date, writer, pic);
                    // 저장 성공 count 1 증가
                    count++;

                } catch (Exception e) {
                    System.out.println("개별 기사 파싱 중 오류 (건너뜀): " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.out.println("노컷뉴스 오류: " + e.getMessage());
        } finally {
            driver.quit();
        }
    }

    // 2. 머니투데이 크롤러 (목록, 본문 전부 Jsoup)
    public void crawlMtNews(String keyword, String district) {
        try {
            String searchUrl = "https://www.mt.co.kr/search/news?filter=contents&order=latest&keyword="
                            + URLEncoder.encode(keyword, "UTF-8");

            Document doc = Jsoup.connect(searchUrl)
                    .userAgent("Mozilla/5.0")
                    .timeout(5000)
                    .get();

            Elements articles = doc.select(".article_item");

            // 기사 몇 개 가져왔는지 세기
            int count = 0;

            for (Element article : articles) {
                try{

                    // 대기하기
                    System.out.println("2.7초 대기");
                    Thread.sleep(2700);

                    // n개 다 채웠으면 반복문을 강제 종료
                    if (count >= 10) {
                        System.out.println(count+"개 수집. 머니투데이 크롤링 종료.");
                        break;
                    }

                    String title = article.select(".headline").text().trim();
                    String url = article.select("a").attr("abs:href");
                    String pic = article.select(".article_body > .thumb > img").attr("src");
                    String writer = article.select(".writer").text().replace(" 기자", "").trim();
                    String date = article.select(".article_date").text().replace(".","-").trim();

                    // URL 없거나 이미 저장된 기사 건너뜀
                    if (title.isEmpty() || url.isEmpty() || articleRepository.existsByArticleUrl(url)) {
                        continue;
                    }

                    // 상세 페이지 본문 정보
                    Map<String, String> details = fetchArticleDetails(url);
                    String content = details.get("content");

                    // AI에게 묻고
                    boolean isCrimeNews = filter.isValid(title, content);
                    // 아니면 넘김
                    if(!isCrimeNews){continue;}
                    // 맞으면 저장
                    articleSaveService.saveToDb(title, url, content, "머니투데이", district, keyword, date, writer, pic);
                    // 저장 성공 count 1 증가
                    count++;

                }catch(Exception e){
                    System.out.println("[개별 기사 파싱 중 오류 (건너뜀)] " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.out.println("머니투데이 오류: " + e.getMessage());
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
