package iriro.saferoute.service;

import iriro.saferoute.dto.*;
import iriro.saferoute.test.TestSampleCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SafeRouteService {
    private final TmapRouteService tmapRouteService;
    private final RiskPointFilterService riskPointFilterService;

    //지금은 위험지역 샘플을 가져오지만 조회를 통해 가져옴 테스트 리스트 -> 추후에 삭제
    List<SafetyFacDto> safetyPoints = TestSampleCode.safeRoutePoint;

    // 안전 경로 계산 함수
    public SafeRouteResponseDto getSafeRoute(RouteRequestDto routeRequestDto){

        // (출발지 위/경도, 목적지 위/경도, 경로 배열, 총걸린 시간, 총 거리)를 반환
        RouteResponseDto routeResponseDto = tmapRouteService.getPedestrianRoute(routeRequestDto);
        List<RoutePointDto> routePoints = routeResponseDto.getRoutePoints(); // 기존 안내 경로

        // 해당 객체 내에 있는 시작점과 끝점의 위/경도를 가지고 위험구역 boundingbox를 만듬
        BboxDto bbox = createBox( routePoints );
        // 구역 내에 있는지 확인하는 함수 isInsideBbox()로 안전위치 리스트를 필터링함.
        List<SafetyFacDto> inSafetyPoints = safetyPoints.stream().filter(point ->
                isInsideBbox(point.getLatitude().doubleValue(), point.getLongitude().doubleValue(),bbox))
                .toList();

        // 경유지 목록 생성 (우회 경로로 쓸)
        List<DetourWayPointDto> detourRoute = riskPointFilterService.getDetourWayPoint(routePoints, bbox);

        // 안전 로직 계산

        return null; //임의 값 추후에 삭제
    }

    // boundingBox를 만드는 함수
    public BboxDto createBox(List<RoutePointDto> routePoints){

        // 만약 리스트가 비어있다면
        if( routePoints == null || routePoints.isEmpty()){
            System.out.println("경로 좌표가 비어있습니다.");
            return null;
        }

        double minLat = routePoints.get(0).getLat().doubleValue();
        double maxLat = routePoints.get(0).getLat().doubleValue();
        double minLng = routePoints.get(0).getLat().doubleValue();
        double maxLng = routePoints.get(0).getLat().doubleValue();

        // 가장 크고 작은 위 경도 값 구하기
        for(RoutePointDto point : routePoints){
            minLat = Math.min(minLat, point.getLat().doubleValue() );
            minLng = Math.min(minLng, point.getLng().doubleValue() );
            maxLat = Math.max(maxLat, point.getLat().doubleValue() );
            maxLng = Math.max(maxLng, point.getLng().doubleValue() );
        }

        // 50m 정도 margin
        double latMargin = 50.0 / 111000.0;
        double centerLat = (minLat + maxLat) / 2.0;
        double lngMargin = 50.0 / (111000.0 * Math.cos(Math.toRadians(centerLat)));

        // BboxDto 반환(위험구역)
        return new BboxDto(
                minLat - latMargin,
                maxLat + latMargin,
                minLng - lngMargin,
                maxLng + lngMargin
                );
    }

    // Bbox안에 있는지 체크하는 함수
    public boolean isInsideBbox(double lat, double lng, BboxDto bbox){
        return lat >= bbox.getMinLat()
                && lat <= bbox.getMaxLat()
                && lng >= bbox.getMinLng()
                && lng <= bbox.getMaxLng();
    }

}
