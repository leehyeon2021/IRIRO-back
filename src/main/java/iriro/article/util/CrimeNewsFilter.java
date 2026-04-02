package iriro.article.util;

import org.springframework.stereotype.Component;

@Component
public class CrimeNewsFilter {

    private static final String[] blackList = {
            "드라마", "출연", "방송", "연예", "화보", "영화", "공개", "웹툰", "매매가격", "비트코인",
            "주가", "주식", "코스피", "증권", "부동산", "날씨", "스포츠", "박재홍의 한판승부",
            "의원", "공천", "특혜", "청탁", "압수수색", "비자금", "선거", "정치", "국회", "기업",
            "노조", "내사", "언론 탄압"
    };

    private static final String[] crimeKeywords = {
            "강도", "절도", "폭행", "성범죄", "흉기", "묻지마", "살인", "상해",
            "칼부림", "스토킹", "침입", "치안", "순찰", "우범", "안전"
    };

    // 저장할 가치 있는 기사인지 판별
    public boolean isValid(String title, String content) {

        // 1. 필수값 체크
        if (title == null || content == null || title.isBlank() || content.isBlank()) {
            return false;
        }

        // 2. 블랙리스트 체크 (제목에 가십 키워드 있으면 탈락)
        for (String word : blackList) {
            if (title.contains(word)) return false;
        }

        // 3. 서울 관련 기사인지 체크
        boolean hasSeoul = content.contains("서울") || title.contains("서울");

        // 4. 범죄 관련 기사인지 체크
        boolean isCrime = false;
        for (String keyword : crimeKeywords) {
            if (content.contains(keyword) || title.contains(keyword)) {
                isCrime = true;
                break;
            }
        }

        // 서울 관련이면서 범죄 관련이어야 통과
        return hasSeoul && isCrime;
    }
}