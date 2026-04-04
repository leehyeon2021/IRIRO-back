package iriro.article.util;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Component
public class ArticleCrimeFilter {

    @Value("${cloudflare.account-id}") private String accountId;
    @Value("${cloudflare.api-token}") private String apiToken;
    private String url;
    private final RestTemplate restTemplate = new RestTemplate();

    // 서버가 켜질 때 API KEY가 포함된 URL주소 생성
    @PostConstruct
    public void init() {
        this.url = "https://api.cloudflare.com/client/v4/accounts/" + accountId
                + "/ai/run/@cf/meta/llama-3.1-8b-instruct";
    }

    private static final String[] blackList = {
            "드라마", "출연", "방송", "연예", "화보", "영화", "공개", "웹툰", "매매가격", "비트코인",
            "주가", "주식", "코스피", "증권", "부동산", "날씨", "스포츠", "박재홍의 한판승부",
            "의원", "공천", "특혜", "청탁", "압수수색", "비자금", "선거", "정치", "국회", "기업",
            "노조", "내사", "언론 탄압", "아이돌", "BTS", "NCT"
    };
    private static final String[] crimeKeywords = {
            "강도", "절도", "폭행", "성범죄", "흉기", "묻지마", "살인", "상해",
            "칼부림", "스토킹", "침입", "치안", "순찰", "우범", "안전"
    };


    // 저장할 가치 있는 기사인지 판별
    public boolean isValid(String title, String content) {

        // === AI 호출 전 키워드 체크 ===

        // 1. 필수값 체크
        if (title == null || content == null || title.isBlank() || content.isBlank()) return false;

        // 2. 블랙리스트 체크
        for (String word : blackList) {
            if (title.contains(word) || content.contains(word)) {
                System.out.println("[블랙리스트 탈락] " + title);
                return false;
            }
        }

        // 3. 서울 체크
        if (!content.contains("서울") && !title.contains("서울")) {
            System.out.println("[서울 아님 탈락] " + title);
            return false;
        }

        // 4. 범죄 키워드 체크
        boolean hasCrime = false;
        for (String keyword : crimeKeywords) {
            if (content.contains(keyword) || title.contains(keyword)) { hasCrime = true; break; }
        }
        if (!hasCrime) {
            System.out.println("[범죄 키워드 없음 탈락] " + title);
            return false;
        }

        // === AI 호출 ===

        // 5. AI 호출
        try {
            String shortContent = content.length() > 300 ? content.substring(0, 300) : content;
            String prompt = """
                    You are a Seoul citizen safety news classifier AI.
                    Determine whether the article below is directly related to 'crime or public safety news for Seoul citizens'.
                    Answer ONLY with 'TRUE' or 'FALSE'. Do not say anything else.
            
                    [TRUE conditions - answer TRUE if ANY of these apply]
                    - Assault, weapons, murder, robbery, theft, sexual crime, stalking, random attacks in Seoul
                    - Seoul public safety policies: increased patrols, CCTV installation, etc.
                    - News about high-crime areas or dangerous commute routes in Seoul
            
                    [FALSE conditions - answer FALSE if ANY of these apply]
                    - Politician corruption, corporate embezzlement, election-related news
                    - Celebrity or idol-related news
                    - Crime news outside of Seoul
                    - Drama/movie/webtoon content
                    - Stocks, real estate, weather, sports
            
                    Title: %s
                    Article summary: %s
                    """.formatted(title, shortContent);

            Map<String, Object> body = Map.of(
                    "messages", List.of(
                            Map.of("role", "system", "content", "뉴스 분류기. TRUE 또는 FALSE 한 단어만 답해."),
                            Map.of("role", "user", "content", prompt)
                    )
            );

            // AI 답변 받기
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiToken);

            ResponseEntity<Map> response = restTemplate.postForEntity(
                    url, new HttpEntity<>(body, headers), Map.class
            );

            if (response.getBody() != null) {
                Map result = (Map) response.getBody().get("result");
                String aiAnswer = result.get("response").toString().toUpperCase().trim();
                System.out.println("[AI 답변] " + title + " → " + aiAnswer);
                return aiAnswer.contains("TRUE");
            }

        } catch (Exception e) {
            System.err.println("[AI 오류]: " + e.getMessage());
        }
        return false;
    }
}