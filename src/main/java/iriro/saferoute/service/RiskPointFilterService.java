package iriro.saferoute.service;

import iriro.saferoute.dto.BboxDto;
import iriro.saferoute.dto.DetourWayPointDto;
import iriro.saferoute.dto.RiskPointDto;
import iriro.saferoute.dto.RoutePointDto;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class RiskPointFilterService {
    private static final double EARTH_RADIUS = 6371000; // 지구 반지름(상수)
    private static final double MERGE_DISTANCE_METER = 200; // 사이간격(상수)
    private static final double DETOUR_EXTRA_DISTANCE_METER = 50; // 얼만큼 우회할 지 정하는 값(상수)

    // 우회 경유지 리스트 반환하는 함수
    public List<DetourWayPointDto> getDetourWayPoints(List<RoutePointDto> routePoints,List<RiskPointDto> filterDangerPoints){
        // 우회 경유지 리스트 생성
        List<DetourWayPointDto> detourWaypoints = new ArrayList<>();

        // 경로상 위험지역 포함 유무에 따라 다르게 처리
        for (RiskPointDto riskPoint : filterDangerPoints) { // 경로에 걸치는 위험지역마다 우회경로 포인트 생성
            DetourWayPointDto detourWayPoint = createDetourWayPoint(routePoints, riskPoint); // 우회 경유지 생성 함수
            if (detourWayPoint != null) detourWaypoints.add(detourWayPoint); //비어 있지 않으면 리스트에 추가
        }
        return detourWaypoints; // 리스트 반환
    }

    // 위험지역 필터링 함수
    public List<RiskPointDto> filterDangerPoints( List<RoutePointDto> routePoints, List<RiskPointDto> dangerPoints ){

        // [2차 필터] : 1차 필터링된 위험 위치 리스트를 경로 50m 안에 들어오는 위험 위치들만 필터링, 길의 타입에 따라 다르게 필터링.
        List<RiskPointDto> secondDangerPoints = dangerPoints.stream().filter(point ->
                getMinDistance(routePoints, point) <= getDangerRadius(point.getRoadType()) ).toList(); // 대로 15m, 로 30m, 길 50m 위험반경 생성

        // 2차 필터 적용 후 리스트가 비어 있으면 바로 리턴
        if(secondDangerPoints.isEmpty()) return new ArrayList<>();

        // [3차 필터] : 2차 필터링된 위험 위치 리스트들의 각 중심점의 위치 간의 거리가 200m 이내라면 뒤에 나온 위험구간 무시.
        List<RiskPointDto> thirdDangerPoints = new ArrayList<>();
        for (RiskPointDto secondPoint : secondDangerPoints) {

            double secondLat = secondPoint.getLatitude().doubleValue(); // 탐색할 위험위치의 위도
            double secondLng = secondPoint.getLongitude().doubleValue(); // 탐색할 위험위치의 경도

            boolean canAdd = true; // 추가할 수 있는지 체크하는 변수

            for (RiskPointDto point : thirdDangerPoints) {
                double distance = distanceMeter(secondLat, secondLng, point.getLatitude().doubleValue(), point.getLongitude().doubleValue());
                if (distance < MERGE_DISTANCE_METER) { // 하나라도 200m 이하면 false
                    canAdd = false;
                    break;
                }
            }
            if (canAdd) thirdDangerPoints.add(secondPoint);
        } // for end

        return thirdDangerPoints;
    }

    // 우회 경로 > 기존 경로 + 300m 일 때 사용하는 함수 --> 처음 나오는 위험구간만 우회함.
    public DetourWayPointDto createSingleDetourWayPoint(List<RoutePointDto> routePoints, RiskPointDto riskPoint){
        return createDetourWayPoint(routePoints, riskPoint); // 우회 경유지 생성 함수를 통한 반환
    }

    // 우회 경유점 생성하는 함수
    private DetourWayPointDto createDetourWayPoint(List<RoutePointDto> routePoints, RiskPointDto riskPoint){
        int index = getSequence(routePoints, riskPoint) - 1; // 위험지역에 걸친 인덱스 가져오기
        if(index <= 0 || index >= routePoints.size()) return null; // 첫번째 점이거나 범위를 벗어나면 null 반환(실패)

        RoutePointDto currPoint = routePoints.get(index);
        RoutePointDto prevPoint = routePoints.get(index-1);

        double riskPointLat = riskPoint.getLatitude().doubleValue();
        double riskPointLng = riskPoint.getLongitude().doubleValue();

        double currLat = currPoint.getLatitude().doubleValue();
        double currLng = currPoint.getLongitude().doubleValue();
        double prevLat = prevPoint.getLatitude().doubleValue();
        double prevLng = prevPoint.getLongitude().doubleValue();

        // 이전 위치와 현재 위치간의 변화량 구하기
        double dx = currLng - prevLng; // 경도 = Lng = X
        double dy = currLat - prevLat; // 위도 = Lat = Y

        // 피타고라스로 길이 구하기
        double length = Math.sqrt( dx * dx + dy * dy);
        if(length == 0) return null; // 길이가 0이면 실패

        // 단위벡터 구하기
        double ux = dx / length;
        double uy = dy / length;

        // 수직 벡터 구하기
        double leftX = -uy;
        double leftY = ux;
        double rightX = uy;
        double rightY = -ux;

        // 도로타입에 따른 위험반경 생성( 대로: 15m, 로: 30m, 길: 50m) 후 여유거리를 더해 이동값 생성
        double detourMeter = getDangerRadius(riskPoint.getRoadType()) + DETOUR_EXTRA_DISTANCE_METER; // 50m 우회

        // 미터를 위 경도로 변환하기 위한 비율 게산
        double meterToLat = 1.0 / 111000.0;
        double meterToLng = 1.0 / (111000.0 * Math.cos(Math.toRadians(currLat)));

        // 후보 경유지 계산(좌,우)
        double leftLat = currLat + (leftY * detourMeter * meterToLat);
        double leftLng = currLng + (leftX * detourMeter * meterToLng);
        double rightLat = currLat + (rightY * detourMeter * meterToLat);
        double rightLng = currLng + (rightX * detourMeter * meterToLng);

        // 좌,우 후보 경유지가 각각 위험지점으로 부터 얼마나 떨어져 있는지 계산
        double leftDistance = distanceMeter( leftLat, leftLng, riskPointLat, riskPointLng);
        double rightDistance = distanceMeter( rightLat, rightLng, riskPointLat, riskPointLng);

        if( leftDistance >= rightDistance){ // 왼쪽 후보 경유지가 오른쪽 후보 경유지보다 멀리 떨어져 있을 경우
            return new DetourWayPointDto(
                    BigDecimal.valueOf(leftLat),
                    BigDecimal.valueOf(leftLng)
            );
        }else{
            return new DetourWayPointDto(
              BigDecimal.valueOf(rightLat),
              BigDecimal.valueOf(rightLng)
            );
        }
    }

    // 위험 좌표 1개당 경로 좌표들 중 최소거리를 구하는 함수
    private double getMinDistance(List<RoutePointDto> routePoints, RiskPointDto riskZone){
        double minDistance = Double.MAX_VALUE;

        double riskLat = riskZone.getLatitude().doubleValue();
        double riskLng = riskZone.getLongitude().doubleValue();

        for(RoutePointDto point : routePoints){
            double pointLat = point.getLatitude().doubleValue();
            double pointLng = point.getLongitude().doubleValue();

            double distance = distanceMeter(pointLat, pointLng, riskLat, riskLng);

            minDistance = Math.min(minDistance, distance);
        }

        return minDistance;
    }

    // 위험지역에 들어간 최단거리 경로 좌표의 순서를 구하는 함수
    private int getSequence(List<RoutePointDto> routePoints, RiskPointDto riskZone){
        double minDistance = Double.MAX_VALUE;
        int sequence = 0;

        double riskLat = riskZone.getLatitude().doubleValue();
        double riskLng = riskZone.getLongitude().doubleValue();

        for(RoutePointDto point : routePoints){
            double pointLat = point.getLatitude().doubleValue();
            double pointLng = point.getLongitude().doubleValue();

            double distance = distanceMeter(pointLat, pointLng, riskLat, riskLng);

            if(minDistance > distance){
                minDistance = distance;
                sequence = point.getSequence();
            }
        }

        return sequence;
    }

    // 거리를 구하는 함수 ( m단위로 반환 ) -> Haversine 공식
    private static double distanceMeter(double lat1, double lng1, double lat2, double lng2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS * c;
    }

    // 위험 지역의 도로 타입에 따라 위험 반경 구하는 함수
    private double getDangerRadius(String roadType) {
        return switch (roadType) {
            case "대로" -> 15.0;
            case "로" -> 30.0;
            case "길" -> 50.0;
            default -> 35.0;
        };
    }

}
