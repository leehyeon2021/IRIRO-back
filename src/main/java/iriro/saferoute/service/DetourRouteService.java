package iriro.saferoute.service;

import iriro.saferoute.dto.DetourWayPointDto;
import iriro.saferoute.dto.RiskPointDto;
import iriro.saferoute.dto.RoutePointDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DetourRouteService {
    private static final double DETOUR_EXTRA_DISTANCE_METER = 50; // 얼만큼 우회할 지 정하는 값(상수)

    private final RiskFilterService riskFilterSvc;
    private final GeoFilterService geoFilterSvc;

    // 우회 경유지 리스트 반환하는 함수
    public List<DetourWayPointDto> getDetourWayPoints(List<RoutePointDto> routePoints, List<RiskPointDto> filterDangerPoints){
        // 우회 경유지 리스트 생성
        List<DetourWayPointDto> detourWaypoints = new ArrayList<>();

        // 경로상 위험지역 포함 유무에 따라 다르게 처리
        for (RiskPointDto riskPoint : filterDangerPoints) { // 경로에 걸치는 위험지역마다 우회경로 포인트 생성
            DetourWayPointDto detourWayPoint = createDetourWayPoint(routePoints, riskPoint); // 우회 경유지 생성 함수
            if (detourWayPoint != null) detourWaypoints.add(detourWayPoint); //비어 있지 않으면 리스트에 추가
        }
        return detourWaypoints; // 리스트 반환
    }

    // 우회 경로 > 기존 경로 + 300m 일 때 사용하는 함수 --> 처음 나오는 위험구간만 우회하는 함수
    public DetourWayPointDto createSingleDetourWayPoint(List<RoutePointDto> routePoints, RiskPointDto riskPoint){
        return createDetourWayPoint(routePoints, riskPoint); // 우회 경유지 생성 함수를 통한 반환
    }

    // 우회 경유점 생성하는 함수
    private DetourWayPointDto createDetourWayPoint(List<RoutePointDto> routePoints, RiskPointDto riskPoint){
        int index = geoFilterSvc.getSequence(routePoints, riskPoint) - 1; // 위험지역에 걸친 인덱스 가져오기
        if(index <= 0 || index >= routePoints.size()) return null; // 첫번째 점이거나 범위를 벗어나면 null 반환(실패)

        RoutePointDto currPoint = routePoints.get(index);
        RoutePointDto prevPoint = routePoints.get(index-1);

        double riskPointLat = riskPoint.getLatitude();
        double riskPointLng = riskPoint.getLongitude();

        double currLat = currPoint.getLatitude();
        double currLng = currPoint.getLongitude();
        double prevLat = prevPoint.getLatitude();
        double prevLng = prevPoint.getLongitude();

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
        double detourMeter = riskFilterSvc.getDangerRadius(riskPoint.getRoadType()) + DETOUR_EXTRA_DISTANCE_METER; // 50m 우회

        // 미터를 위 경도로 변환하기 위한 비율 게산
        double meterToLat = 1.0 / 111000.0;
        double meterToLng = 1.0 / (111000.0 * Math.cos(Math.toRadians(currLat)));

        // 후보 경유지 계산(좌,우)
        double leftLat = currLat + (leftY * detourMeter * meterToLat);
        double leftLng = currLng + (leftX * detourMeter * meterToLng);
        double rightLat = currLat + (rightY * detourMeter * meterToLat);
        double rightLng = currLng + (rightX * detourMeter * meterToLng);

        // 좌,우 후보 경유지가 각각 위험지점으로 부터 얼마나 떨어져 있는지 계산
        double leftDistance = geoFilterSvc.distanceMeter( leftLat, leftLng, riskPointLat, riskPointLng);
        double rightDistance = geoFilterSvc.distanceMeter( rightLat, rightLng, riskPointLat, riskPointLng);

        if( leftDistance >= rightDistance){ // 왼쪽 후보 경유지가 오른쪽 후보 경유지보다 멀리 떨어져 있을 경우
            return new DetourWayPointDto(
                    leftLat,
                    leftLng
            );
        }else{
            return new DetourWayPointDto(
                    rightLat,
                    rightLng
            );
        }
    }
}
