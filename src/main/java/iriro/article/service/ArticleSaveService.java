package iriro.article.service;

import iriro.article.entity.ArticleEntity;
import iriro.article.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArticleSaveService {
    private final ArticleRepository articleRepository;

    // 저장
    public void saveToDb(String title, String url, String content, String siteName, String district, String keyword, String date, String writer, String pic) {

        // 중복 체크 (1. URL , 2. keyword)
        // 1. URL 중복 체크
        if (articleRepository.existsByArticleUrl(url)) {
            System.out.println("이미 저장된 기사 건너뜀: " + title);
            return;
        }

        // 2. 키워드 유사도 중복 체크 (자카드 유사도)
        // 1) 키워드 추출
        String extractedKeyword = extractedKeywords(title, content);
        // 2) 최근 파일 가져와서
        List<ArticleEntity> recentArticles =
                articleRepository.findTop20ByArticleDistrictOrderByArticleCreatedAtDesc(district);
        // 3) 비교 후 중복되면 넘어가기
        for(ArticleEntity article : recentArticles){
            if(article.getArticleKeyword() != null && isSimilar(extractedKeyword, article.getArticleKeyword())){
                System.out.println("[키워드 중복 건너뜀] "+keyword);
                return;
            }
        }

        // DB 글자 수 제한 지키기 (본문 전체 저장은 저작권법위반)
        String safeTitle = title.length() > 95 ? title.substring(0, 95) + "..." : title;
        String safeConte = content.length() > 300 ? content.substring(0, 300) : content;
        String safeSite = siteName.length() > 10 ? siteName.substring(0, 10) : siteName;
        String safeDistrict = district.length() > 10 ? district.substring(0, 10) : district;
        String safeDate = date.length() > 10 ? date.substring(0, 10) : date;
        String safeWriter = writer.length() > 20 ? writer.substring(0, 20) : writer;
        String safePic = pic.length() > 250 ? pic.substring(0, 250) : pic;

        articleRepository.save(ArticleEntity.builder()
                .articleTitle(safeTitle)
                .articleUrl(url)
                .articleContent(safeConte)
                .articleSite(safeSite)
                .articleDistrict(safeDistrict)
                .articleKeyword(extractedKeyword)
                .articleDate(safeDate)
                .articleWriter(safeWriter)
                .articlePic(safePic)
                .build());

        System.out.println("저장 완료 [" + safeSite + "]: " + safeTitle);
    }

    // 중복 제거: 자카드 방식 사용 (단어 빈도수)
    // 키워드 추출
    private String extractedKeywords(String title, String content){
        // 500자만 검사
        String combined = title+" "+title+" "+title+" "+content;
        String shortened = combined.length() > 500 ? combined.substring(0 , 500) : combined;

        // 특수문자 제거, 공백 분리
        String[] words = shortened.replaceAll("[^가-힣a-zA-Z0-9\\s]", "").split("\\s+");

        // 단어 빈도수 계산 (두 자 이상)
        Map<String, Long> freq = Arrays.stream(words)
                .filter(w -> w.length() >= 2)
                .collect(Collectors.groupingBy(w -> w , Collectors.counting()));

        // 단어 빈도수 높은 순으로 상위 10개 (내림차순
        return freq.entrySet().stream()
                .sorted((a , b) -> Long.compare(b.getValue(), a.getValue()))
                .limit(10)
                .map(Map.Entry::getKey)
                .collect(Collectors.joining(","));

    }

    // 자카드 유사도: 교집합 나누기 합집합
    private boolean isSimilar(String keyword1 , String keyword2){
        // 원본 유지
        Set<String> set1 = new HashSet<>(Arrays.asList(keyword1.split(",")));
        Set<String> set2 = new HashSet<>(Arrays.asList(keyword2.split(",")));

        // 교집합
        Set<String> intersection = new HashSet<>(set1);
        intersection.retainAll(set2); // 겹치는 것

        // 합집합
        Set<String> union = new HashSet<>(set1);
        union.addAll(set2);
        if(union.isEmpty()){
            return false;
        }

        // 교집합 나누기 합집합
        double similarity = (double) intersection.size() / union.size();
        System.out.println("[유사도] "+keyword1+" , "+keyword2+" => "+similarity);

        // 50% 넘으면 중복 처리
        return similarity >= 0.5;
    }

}
