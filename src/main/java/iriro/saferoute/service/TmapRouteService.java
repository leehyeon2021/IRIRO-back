package iriro.saferoute.service;

import iriro.saferoute.dto.RoutePointDto;
import iriro.saferoute.dto.SafeRouteResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TmapRouteService { //Tmap API 연결

    // application.properties에 저장된 tmap api app 키
    @Value("${tmap.app-key}")
    private String appKey;

    // application.properties에 저장된 서비스를 호출 할 url
    @Value("${tmap.pedestrian-url}")
    private String pedestrianUrl;

    // 외부 API를 호출하기 위한 HTTP 클라이언트 객체 생성
    private final WebClient webClient;

    public SafeRouteResponseDto getPedestrianRoute(
            double startLat, double startLng, double endLat, double endLng
    ){
        // 1. 요청 body 생성
        Map<String, Object> body = new HashMap<>();
        body.put( "startX", startLng ); // 출발지 경도
        body.put( "startY", startLat ); // 출발지 위도
        body.put( "endX", endLng ); // 도착지 경도
        body.put( "endY", endLat ); // 도착지 위도
        body.put( "reqCoordType", "WGS84GEO" ); // WGS84GEO -> 기준 좌표계
        body.put( "resCoordType", "WGS84GEO" );
        body.put( "startName", "출발지" );
        body.put( "endName", "도착지" );

        // 2. TMAP json호출
        Map<String, Object> response = webClient.post()
                .uri(pedestrianUrl)
                .header("appKey", appKey)
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if(response == null){
            throw new RuntimeException(("TMAP 응답이 없습니다."));
        }

        // 경로 배열은 Feature -> geometry -> LineString -> coordinates에 있음
        // 3. 응답에서 features 추출
        Object featuresObj = response.get("features");
        if( !(featuresObj instanceof List<?> features)){
            throw new RuntimeException("TMAP 응답에 features가 없습니다.");
        }

        List<RoutePointDto> routePoints = new ArrayList<>();
        int totalDistance = 0;
        int totalTime = 0;
        int sequence = 1;

        // 4. features 순회
        for(Object featureObj : features){
            if(featureObj == null) continue;
            Map<?, ?> feature = (Map<?, ?>) featureObj;

            // geometry 추출
            Object geometryObj = feature.get("geometry");
            if(geometryObj == null) continue;
            Map<?, ?> geometry = (Map<?, ?>) geometryObj;

            Object typeObj = geometry.get("type");
            if(!typeObj.equals("LineString")) continue;

            Object coordinateObj = geometry.get("coordinates");
            if( coordinateObj == null) continue;
            List<?> coordinates = (List<?>)coordinateObj;

            for(Object coordObj : coordinates){
                if(coordObj == null) continue;
                List<?> point = (List<?>)coordObj;
                if(point.size() < 2) continue;

                Object lonObj = point.get(0);
                Object latObj = point.get(1);

                if (!(lonObj instanceof Number) || !(latObj instanceof Number)) {
                    continue;
                }

                BigDecimal longitude = BigDecimal.valueOf(((Number) lonObj).doubleValue());
                BigDecimal latitude = BigDecimal.valueOf(((Number) latObj).doubleValue());

                routePoints.add(new RoutePointDto(latitude, longitude, sequence++));
            }
        }

        return SafeRouteResponseDto.builder()
                .start_latitude(BigDecimal.valueOf(startLat))
                .start_longitude(BigDecimal.valueOf(startLng))
                .end_latitude(BigDecimal.valueOf(endLat))
                .end_longitude(BigDecimal.valueOf(endLng))
                .safety_score(0) // 아직 계산 전 임시값
                .totalTime(totalTime)
                .totalDistance(totalDistance)
                .routePoints(routePoints)
                .build();
    }

}
