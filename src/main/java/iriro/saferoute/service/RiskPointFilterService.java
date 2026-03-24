package iriro.saferoute.service;

import iriro.saferoute.dto.BboxDto;
import iriro.saferoute.dto.DetourWayPointDto;
import iriro.saferoute.dto.RoutePointDto;
import iriro.saferoute.test.TestSampleCode;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RiskPointFilterService {
    private static final double EARTH_RADIUS = 6371000; // 지구 반지름(상수)

    public List<DetourWayPointDto> getDetourWayPoint(List<RoutePointDto> routePoints, BboxDto bbox){

        // 위험 지역을 DB에서 조사해 1차 필터링을 거친 값들만 리스트로 변환하여 가져옴. --> 추후 JPA활용하여 처리
        List<RoutePointDto> dangerPoints = TestSampleCode.dangerRoutePoints;

        List<RoutePointDto> firstInDangerPoints = dangerPoints.stream().filter(point ->
                        isInsideBbox(point.getLat().doubleValue(), point.getLng().doubleValue(),bbox))
                .toList();

        // 필터링된 위험구역 지점들을 2차 필터(경로 마다 50m 안에 들어오는 위험 지점들만 리스트화)
        List<RoutePointDto> secondInDangerPoints = firstInDangerPoints.stream().filter(point ->
                getMinDistance(routePoints, point) <= 50 ).toList(); // 50m 이내의 점들만 리스트에 추가


        // 우회 경유지 리스트 생성
        List<RoutePointDto> detourWaypoints = new ArrayList<>();
        // 경로상 위험지역 포함 유무에 따라 다르게 처리
        if(!secondInDangerPoints.isEmpty()){ // 경로 상에 위험지역이 있으면...
            for(RoutePointDto riskPoint : secondInDangerPoints){ // 경로에 걸치는 위험지역마다 우회경로 포인트 생성
                int sequence = getSequence(routePoints, riskPoint); // 순서가져오기
                // 위험지역이 겹치면? 위험지역이 처음 출발지라면? 너무나도 if 조건이 많다;
            }
        }else{ // 경로 상에 위험지역이 없다면 기존 경로 안전점수 계산하여 반환

        }
        return null;
    }

    // Bbox안에 있는지 체크하는 함수
    public boolean isInsideBbox(double lat, double lng, BboxDto bbox){
        return lat >= bbox.getMinLat()
                && lat <= bbox.getMaxLat()
                && lng >= bbox.getMinLng()
                && lng <= bbox.getMaxLng();
    }

    // 위험 좌표 1개당 경로 좌표들 중 최소거리와 순서를 구하는 함수
    public double getMinDistance(List<RoutePointDto> routePoints, RoutePointDto riskZone){
        double minDistance = Double.MAX_VALUE;

        double riskLat = riskZone.getLat().doubleValue();
        double riskLng = riskZone.getLng().doubleValue();

        for(RoutePointDto point : routePoints){
            double pointLat = point.getLat().doubleValue();
            double pointLng = point.getLng().doubleValue();

            double distance = distanceMeter(pointLat, pointLng, riskLat, riskLng);

            minDistance = Math.min(minDistance, distance);
        }

        return minDistance;
    }

    public int getSequence(List<RoutePointDto> routePoints, RoutePointDto riskZone){
        double minDistance = Double.MAX_VALUE;
        int sequence = 0;

        double riskLat = riskZone.getLat().doubleValue();
        double riskLng = riskZone.getLng().doubleValue();

        for(RoutePointDto point : routePoints){
            double pointLat = point.getLat().doubleValue();
            double pointLng = point.getLng().doubleValue();

            double distance = distanceMeter(pointLat, pointLng, riskLat, riskLng);

            if(minDistance > distance){
                sequence = point.getSequence();
            }
        }

        return sequence;
    }

    // 거리를 구하는 함수 ( m단위로 반환 ) -> Haversine 공식
    public static double distanceMeter(double lat1, double lng1, double lat2, double lng2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS * c;
    }
}
