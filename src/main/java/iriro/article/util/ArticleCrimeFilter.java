package iriro.article.util;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

@Component
public class ArticleCrimeFilter {

    // 스프링AI: 제미나이 대화 클라이언트
    private final ChatClient chatClient;
    // ChatClient 직접 빌드
    public ArticleCrimeFilter(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    // 저장할 가치 있는 기사인지 판별
    public boolean isValid(String title, String content) {

        // 필수값 체크
        if (title == null || content == null || title.isBlank() || content.isBlank()) {
            return false;
        }

        // 본문 요약(토큰 절약)
        String shortContent = content.length() > 500 ? content.substring(0, 500) : content;

        // AI 프롬프트
        String prompt = """
                너는 서울 지역 시민 안전 뉴스 분류 AI야.
                아래 기사가 '서울 시민의 안전과 직접 관련된 범죄/치안 뉴스'인지 판단해.
                반드시 'TRUE' 또는 'FALSE' 한 단어만 대답해. 다른 말은 절대 하지 마.

                [TRUE 조건 - 하나라도 해당하면 TRUE]
                - 서울에서 발생한 폭행, 흉기, 살인, 강도, 절도, 성범죄, 스토킹, 묻지마 범죄
                - 서울 치안 강화, 순찰 확대, CCTV 설치 등 시민 안전 정책
                - 서울 우범지역, 귀갓길 위험 관련 뉴스

                [FALSE 조건 - 하나라도 해당하면 FALSE]
                - 정치인 비리, 기업 횡령, 선거 관련 뉴스
                - 연예인, 아이돌 관련 뉴스
                - 서울 외 지역 범죄 뉴스
                - 드라마/영화/웹툰 내용 소개
                - 주식, 부동산, 날씨, 스포츠

                제목: %s
                본문 요약: %s
                """.formatted(title, shortContent);

        // AI 답변 받기
        try {
            String aiResponse = chatClient.prompt( prompt )
                    .call()
                    .content()
                    .trim();

            System.out.println("[AI 판별 결과] " + title + " -> " + aiResponse);

            // 답변 "TRUE"면 통과
            return aiResponse.contains("TRUE");

        } catch (Exception e) {
            // 통신 오류 (무료 한도 제한: 1분 15회)
            System.out.println("AI 판별 오류(건너뜀): " + e.getMessage());
            return false;
        }
    }
}