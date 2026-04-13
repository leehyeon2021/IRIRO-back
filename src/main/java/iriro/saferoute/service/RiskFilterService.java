package iriro.saferoute.service;

import iriro.saferoute.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RiskFilterService {
    private static final double MERGE_DISTANCE_METER = 50; // 사이간격(상수)

    private final GeoFilterService geoFilterSvc;

    // 위험지역 필터링 함수(2,3차)
    public List<RiskPointDto> filterDangerPoints( List<RoutePointDto> routePoints, List<RiskPointDto> inDangerPoints ){
        // 해당 객체 내에 있는 시작점과 끝점의 위/경도를 가지고 위험구역 boundingbox를 만듬

        // [2차 필터] : 1차 필터링된 위험 위치 리스트를 경로 50m 안에 들어오는 위험 위치들만 필터링, 길의 타입에 따라 다르게 필터링.
        List<RiskPointDto> secondDangerPoints
                = inDangerPoints.stream().filter(point ->
                geoFilterSvc.getMinDistance(
                        routePoints, point.getLatitude(), point.getLongitude())
                        <= getDangerRadius(point.getRoadType())
                )
                .toList(); // 대로 15m, 로 30m, 길 50m 위험반경 생성

        // 2차 필터 적용 후 리스트가 비어 있으면 바로 리턴
        if(secondDangerPoints.isEmpty()) return new ArrayList<>();

        // [3차 필터] : 2차 필터링된 위험 위치 리스트들의 각 중심점의 위치 간의 거리가 50m 이내라면 뒤에 나온 위험구간 무시 후 해당 지역 위험수 +1
        List<RiskPointDto> thirdDangerPoints = new ArrayList<>();
        for (RiskPointDto secondPoint : secondDangerPoints) {

            double secondLat = secondPoint.getLatitude(); // 탐색할 위험위치의 위도
            double secondLng = secondPoint.getLongitude(); // 탐색할 위험위치의 경도

            boolean canAdd = true; // 추가할 수 있는지 체크하는 변수

            for (RiskPointDto point : thirdDangerPoints) {
                double distance = geoFilterSvc.distanceMeter(secondLat, secondLng, point.getLatitude(), point.getLongitude());
                if (distance < MERGE_DISTANCE_METER) { // 하나라도 50m 이하면 false
                    canAdd = false;
                    point.setRiskCount(point.getRiskCount() + secondPoint.getRiskCount()); // 위험 수 + 1
                    break;
                }
            }
            if (canAdd) thirdDangerPoints.add(secondPoint);
        } // for end

        return thirdDangerPoints;
    }

    // 위험 지역의 도로 타입에 따라 위험 반경 구하는 함수
    public double getDangerRadius(String roadType) {
        return switch (roadType) {
            case "대로" -> 15.0;
            case "로" -> 30.0;
            case "길" -> 50.0;
            default -> 35.0;
        };
    }
}
