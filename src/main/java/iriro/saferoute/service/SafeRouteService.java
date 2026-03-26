package iriro.saferoute.service;

import iriro.saferoute.dto.*;
import iriro.saferoute.test.TestSampleCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SafeRouteService {
    private static final int MAX_DETOUR_DISTANCE_METER = 300; // 허용 가능한 우회 최대 거리

    private final TmapRouteService tmapRouteService;
    private final RiskPointFilterService riskPointFilterService;

    //지금은 위험지역 샘플을 가져오지만 조회를 통해 가져옴 테스트 리스트 -> 추후에 삭제
    List<SafetyFacPointDto> safetyPoints = TestSampleCode.safeRoutePoint;
    List<RiskPointDto> allDangerPoints = TestSampleCode.dangerRoutePoints;

    // 안전 경로 계산 함수
    public SafeRouteResponseDto getSafeRoute(RouteRequestDto routeRequestDto){

        // (출발지 위/경도, 목적지 위/경도, 경로 배열, 총걸린 시간, 총 거리)를 반환
        RouteResponseDto originRoute = tmapRouteService.getPedestrianRoute(routeRequestDto); // 경로 생성 API 1번 호출
        List<RoutePointDto> routePoints = originRoute.getRoutePoints(); // 기존 안내 경로
        // 위험 지역을 DB에서 조사해 1차 필터링을 거친 값들만 리스트로 변환하여 가져옴. --> 추후 JPA활용하여 처리

        // 해당 객체 내에 있는 시작점과 끝점의 위/경도를 가지고 위험구역 boundingbox를 만듬
        BboxDto bbox = createBox( routePoints );
        // 안전시설물 1차 필터링
        List<SafetyFacPointDto> inSafetyPoints = safetyPoints.stream().filter(point ->
                isInsideBbox(point.getLatitude().doubleValue(), point.getLongitude().doubleValue(),bbox))
                .toList();
        // 위험 리스트 1차 필터링
        List<RiskPointDto> bboxDangerPoints = allDangerPoints.stream().filter(point ->
                        isInsideBbox(point.getLatitude().doubleValue(), point.getLongitude().doubleValue(),bbox))
                .toList();
        // 위험 리스트 2,3차 필터링( 경로상 50m 이내, 연속된 위험지역 건너뛰기 )
        List<RiskPointDto> filteredDangerPoints = riskPointFilterService.filterDangerPoints(routePoints, bboxDangerPoints); // 2,3차 필터 함수 적용

        // 만약 필터링 된 위험리스트가 비어있으면 기존 경로 안전점수 계산 후 반환
        if(filteredDangerPoints.isEmpty()){
            // *** 안전점수 함수 추가해야함 ***
            SafeRouteResponseDto.builder().detourRoute(originRoute).safety_score(80).build();
        }

        // 우회 경유지 목록 생성 ( null이면 기본 경로 반환)
        List<DetourWayPointDto> detourPoints = riskPointFilterService.getDetourWayPoints(routePoints, filteredDangerPoints);
        RouteResponseDto detourRoute = tmapRouteService.getDetourRoute(routeRequestDto, detourPoints); // ++추가 TmapAPI 호출

        System.out.println("우회 경유지 목록: " + Arrays.deepToString(detourPoints.toArray()));
        System.out.println("우회 경유지 크기: " + detourPoints.size() );
        System.out.println("기존 경로 총 시간: " + originRoute.getTotalTime());
        System.out.println("기존 경로 총 거리: " + originRoute.getTotalDistance());
        System.out.println("우회 경로 총 시간: " + detourRoute.getTotalTime());
        System.out.println("우회 경로 총 거리: " + detourRoute.getTotalDistance());

        // 우회 경유지가 기존 경로보다 300m가 넓다면
        if(detourRoute.getTotalDistance() - originRoute.getTotalDistance() > MAX_DETOUR_DISTANCE_METER ){
            // 위험경로를 한 번만 우회하는 경로 생성
            DetourWayPointDto singleWayPoint = riskPointFilterService.createSingleDetourWayPoint(routePoints, filteredDangerPoints.get(0) );
            RouteResponseDto singleDetourRoute = tmapRouteService.getDetourRoute(routeRequestDto, detourPoints); // 싱글 우회경로 생성

            // *** 안전점수 함수 추가해야함 ***
            return SafeRouteResponseDto.builder().detourRoute(singleDetourRoute).safety_score(90).build();
        }

        // 테스트용 안전경로 반환(우회 경로, 점수) -> 추후에 삭제
        return SafeRouteResponseDto.builder()
                .detourRoute(detourRoute).safety_score(100).build();
    }

    // 안전 점수 계산 함수 --> 추후에.. 3/26 에정
    // 안전 점수 계산 로직: 경로(우회경로 or 기본 경로) 상의 위험지역 개수와 안전시설물의 개수를 따진다. 안전시설물은 어떤 안전시설물인지에 따라 차등을 다르게 둔다.
    private int getSafetyScore(){

        return 0;
    }


    // boundingBox를 만드는 함수
    private BboxDto createBox(List<RoutePointDto> routePoints){

        // 만약 리스트가 비어있다면
        if( routePoints == null || routePoints.isEmpty()){
            System.out.println("경로 좌표가 비어있습니다.");
            return null;
        }

        double minLat = routePoints.get(0).getLatitude().doubleValue();
        double maxLat = routePoints.get(0).getLatitude().doubleValue();
        double minLng = routePoints.get(0).getLatitude().doubleValue();
        double maxLng = routePoints.get(0).getLatitude().doubleValue();

        // 가장 크고 작은 위 경도 값 구하기
        for(RoutePointDto point : routePoints){
            minLat = Math.min(minLat, point.getLatitude().doubleValue() );
            minLng = Math.min(minLng, point.getLongitude().doubleValue() );
            maxLat = Math.max(maxLat, point.getLatitude().doubleValue() );
            maxLng = Math.max(maxLng, point.getLongitude().doubleValue() );
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
    private boolean isInsideBbox(double lat, double lng, BboxDto bbox){
        return lat >= bbox.getMinLat()
                && lat <= bbox.getMaxLat()
                && lng >= bbox.getMinLng()
                && lng <= bbox.getMaxLng();
    }

}
