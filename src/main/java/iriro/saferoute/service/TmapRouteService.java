package iriro.saferoute.service;

import iriro.saferoute.dto.DetourWayPointDto;
import iriro.saferoute.dto.RoutePointDto;
import iriro.saferoute.dto.RouteRequestDto;
import iriro.saferoute.dto.RouteResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.core.ParameterizedTypeReference;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TmapRouteService { //Tmap API 연결

    // application.properties에 저장된 tmap api app 키
    @Value("${tmap.app-key}")
    private String appKey;

    // application.properties에 저장된 서비스를 호출 할 url(기본 경로)
    @Value("${tmap.pedestrian-url}")
    private String pedestrianUrl;

    // 외부 API를 호출하기 위한 HTTP 클라이언트 객체 생성
    private final WebClient webClient;

    // 기본 경로 구하는 함수
    public RouteResponseDto getPedestrianRoute(RouteRequestDto routeRequestDto) {
        // 1. 요청 body 생성
        Map<String, Object> body = createBaseRequestBody(routeRequestDto);
        Map<String, Object> response = requestTmapRoute(body);
        return buildRouteResponse(response, routeRequestDto);
    }

    // 경유지 있는 경로 함수 만들기 (경유지 리스트 문자열 방식으로 변환)
    public RouteResponseDto getDetourRoute(RouteRequestDto routeRequestDto, List<DetourWayPointDto> detourList) {
        Map<String, Object> body = createBaseRequestBody(routeRequestDto);
        String passList = createPassList(detourList);
        if (passList != null && !passList.isBlank()) {
            body.put("passList", passList);
        }
        Map<String, Object> response = requestTmapRoute(body);
        return buildRouteResponse(response, routeRequestDto);
    }

    private String createPassList(List<DetourWayPointDto> detourList){
        return detourList.stream()
                .limit(5)
                .map(point -> point.getLongitude() + "," + point.getLatitude())
                .collect(Collectors.joining("_"));
    }

    // body 생성
    private Map<String, Object> createBaseRequestBody(RouteRequestDto routeRequestDto){
        Map<String, Object> body = new HashMap<>();
        body.put("startX", routeRequestDto.getStartLng()); // 출발지 경도
        body.put("startY", routeRequestDto.getStartLat()); // 출발지 위도
        body.put("endX", routeRequestDto.getEndLng()); // 도착지 경도
        body.put("endY", routeRequestDto.getEndLat()); // 도착지 위도
        body.put("reqCoordType", "WGS84GEO"); // WGS84GEO -> 기준 좌표계
        body.put("resCoordType", "WGS84GEO");
        body.put("startName", "출발지");
        body.put("endName", "도착지");

        return body;
    }

    // response 생성
    private Map<String, Object> requestTmapRoute(Map<String, Object> body){
        return webClient.post()
                .uri(pedestrianUrl)
                .header("appKey", appKey)
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();
    }

    // response에 대한 처리(원하는 값만 가져오기)
    private RouteResponseDto buildRouteResponse(Map<String, Object> response, RouteRequestDto routeRequestDto){
        if (response == null) {
            throw new RuntimeException(("TMAP 응답이 없습니다."));
        }

        // 경로 배열은 Feature -> geometry -> LineString -> coordinates에 있음
        // 3. 응답에서 features 추출
        Object featuresObj = response.get("features");
        if (!(featuresObj instanceof List<?> features)) {
            throw new RuntimeException("TMAP 응답에 features가 없습니다.");
        }

        List<RoutePointDto> routePoints = new ArrayList<>();
        Integer totalDistance = 0;
        Integer totalTime = 0;
        int sequence = 1;

        // 4. features 순회
        for (Object featureObj : features) {
            if (featureObj == null) continue;
            Map<?, ?> feature = (Map<?, ?>) featureObj;

            // geometry 추출
            Object geometryObj = feature.get("geometry");
            if (geometryObj == null) continue;
            Map<?, ?> geometry = (Map<?, ?>) geometryObj;

            Object typeObj = geometry.get("type");

            // 총 걸린 시간, 총 거리 추출
            Object propertyObj = feature.get("properties");
            if (propertyObj == null) continue;
            Map<?, ?> properties = (Map<?, ?>) propertyObj;

            if (properties.get("totalTime") != null) totalTime = ((Number) properties.get("totalTime")).intValue();
            if (properties.get("totalDistance") != null)
                totalDistance = ((Number) properties.get("totalDistance")).intValue();

            // 경로 좌표 추출
            if (!typeObj.equals("LineString")) continue;

            Object coordinateObj = geometry.get("coordinates");
            if (coordinateObj == null) continue;
            List<?> coordinates = (List<?>) coordinateObj;

            for (Object coordObj : coordinates) {
                if (coordObj == null) continue;
                List<?> point = (List<?>) coordObj;
                if (point.size() < 2) continue;

                Object lonObj = point.get(0);
                Object latObj = point.get(1);

                if (!(lonObj instanceof Number) || !(latObj instanceof Number)) {
                    continue;
                }

                double longitude = ((Number) lonObj).doubleValue();
                double latitude = ((Number) latObj).doubleValue();

                routePoints.add(new RoutePointDto(latitude, longitude, sequence++));
            }
        }
        List<RoutePointDto> deduplicateRoutePoints = deduplicateRoutePoints(routePoints);

        return RouteResponseDto.builder()
                .start_latitude(routeRequestDto.getStartLat())
                .start_longitude(routeRequestDto.getStartLng())
                .end_latitude(routeRequestDto.getEndLat())
                .end_longitude(routeRequestDto.getEndLng())
                .totalTime(totalTime)
                .totalDistance(totalDistance)
                .routePoints(deduplicateRoutePoints)
                .build();
    }


    // 불필요한 연속된 중복된 경로를 제거하고 순서 재정렬하는 함수.
    private List<RoutePointDto> deduplicateRoutePoints(List<RoutePointDto> routePoints) {
        List<RoutePointDto> DDRoutePoints = new ArrayList<>();
        RoutePointDto prev = null;

        for (RoutePointDto current : routePoints) {
            // 전 좌표가 null이거나 위,경도가 둘 중 하나라도 다르면 추가
            if (prev == null || current.getLatitude().compareTo(prev.getLatitude()) != 0 || current.getLongitude().compareTo(prev.getLongitude()) != 0) {
                DDRoutePoints.add(current);
            }
            prev = current;
        }
        resetSequence(DDRoutePoints); // 시퀀스 순서 재정렬
        return DDRoutePoints;
    }

    // 순서(인덱스) 재정렬하는 함수
    private List<RoutePointDto> resetSequence(List<RoutePointDto> routePoints) {
        int sequence = 1;

        for (RoutePointDto point : routePoints) {
            point.setSequence(sequence++);
        }

        return routePoints;
    }
}
