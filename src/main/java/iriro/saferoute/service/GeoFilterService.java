package iriro.saferoute.service;

import iriro.saferoute.dto.RiskPointDto;
import iriro.saferoute.dto.RoutePointDto;
import org.springframework.stereotype.Service;

import java.util.List;

// 역할: bbox생성, bbox포함 여부, 두 좌표 거리 계산, 경로와 점 사이 최소 거리 계산, sequence 찾기
@Service
public class GeoFilterService {
    private static final double EARTH_RADIUS = 6371000; // 지구 반지름(상수)

    // 좌표 1개당 경로 좌표들 중 최소거리를 구하는 함수
    public double getMinDistance(List<RoutePointDto> routePoints, double latitude, double longitude){
        double minDistance = Double.MAX_VALUE;

        for(RoutePointDto point : routePoints){
            double pointLat = point.getLatitude();
            double pointLng = point.getLongitude();

            double distance = distanceMeter(pointLat, pointLng, latitude, longitude);

            minDistance = Math.min(minDistance, distance);
        }

        return minDistance;
    }

    // 위험지역에 들어간 최단거리 경로 좌표의 순서를 구하는 함수
    public int getSequence(List<RoutePointDto> routePoints, RiskPointDto riskZone){
        double minDistance = Double.MAX_VALUE;
        int sequence = 0;

        double riskLat = riskZone.getLatitude();
        double riskLng = riskZone.getLongitude();

        for(RoutePointDto point : routePoints){
            double pointLat = point.getLatitude();
            double pointLng = point.getLongitude();

            double distance = distanceMeter(pointLat, pointLng, riskLat, riskLng);

            if(minDistance > distance){
                minDistance = distance;
                sequence = point.getSequence();
            }
        }

        return sequence;
    }

    // 거리를 구하는 함수 ( m단위로 반환 ) -> Haversine 공식
    public double distanceMeter(double lat1, double lng1, double lat2, double lng2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS * c;
    }

}
