package iriro.community.service;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service

public class ReverseGeocodingService {

    // ㅇㅍㄹㅋㅇㅅㅍㄹㅍㅌㅅ에 적힌 변수명이랑 똑같이 ${} 안에 적으면 스프링이 실행될 때 알아서 카카오키값을 채워준다고 함!
    @Value("${kakao.api-key}")
    private String kakaoApikey;

    public String findAddress(String x , String y){
        String fullUrl = "https://dapi.kakao.com/v2/local/geo/coord2address.json" + "?x=" + x + "&y=" + y;


        // 헤더 객체 생성
        HttpHeaders headers = new HttpHeaders();

        // 신분증(API key) 붙이기
        headers.set("Authorization","KakaoAK " + kakaoApikey );

        HttpEntity<String> entity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate(); // RestTemplate : 외부 API 랑 통신할 때 쓰는 만능상자

        // "이 주소(fullUrl)로, GET 방식으로, 신분증(entity) 들고 가서, 글자(String)로 받아와!"
        ResponseEntity<String> response = restTemplate.exchange(fullUrl, HttpMethod.GET,entity, String.class);

        // 카카오가 보내준 답장 내용(Body)을 리턴
        return response.getBody();
    }
}
