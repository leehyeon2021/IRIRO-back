package iriro.article.service;

import io.github.bonigarcia.wdm.WebDriverManager;
import iriro.article.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CrawlingService {
    private final ArticleRepository ar;

    // 1. 노컷뉴스 크롤링
    public void crawlNN( String keyword ){
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments( "--headless=new" , "--disable-gpu" );
        WebDriver driver = new ChromeDriver(options);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            String searchUrl = "https://search.nocutnews.co.kr/list?query=" + keyword;
            driver.get(searchUrl);

            // 검색 결과 나올 때까지 대기
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".newslist > li > di")));

            // 기사 목록 가져오기
            List<WebElement> articles = driver.findElements(By.cssSelector(""));

            //

        } catch (Exception e) {
            System.out.println("노컷뉴스 오류: " + e.getMessage());
        } finally {
            driver.quit(); // 브라우저 종료
        }

    }

    // 2. 머니투데이 크롤링

}
