package iriro.saferoute.service;

import iriro.saferoute.dto.*;
import iriro.saferoute.test.TestSampleCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SafeRouteService {
    private static final int MAX_DETOUR_DISTANCE_METER = 300; // 허용 가능한 우회 최대 거리

    private final TmapRouteService tmapRouteSvc; // TmapAPI 사용 서비스
    private final RiskFilterService riskFilterSvc; // 위험 지역 필터 서비스
    private final SafeFacFilterService safeFacFilterSvc; // 안전 지역 필터 서비스
    private final DetourRouteService detourRouteSvc; // 우회 경로 서비스

    //지금은 위험지역 샘플을 가져오지만 조회를 통해 가져옴 테스트 리스트 -> 추후에 삭제
    List<SafetyFacPointDto> allSafetyPoints = TestSampleCode.safeRoutePoint;
    List<RiskPointDto> allDangerPoints = TestSampleCode.dangerRoutePoints;

    // 안전 경로 계산 함수
    public SafeRouteResponseDto getSafeRoute(RouteRequestDto routeRequestDto){

        // (출발지 위/경도, 목적지 위/경도, 경로 배열, 총걸린 시간, 총 거리)를 반환
        RouteResponseDto originRoute = tmapRouteSvc.getPedestrianRoute(routeRequestDto); // 경로 생성 API 1번 호출
        List<RoutePointDto> routePoints = originRoute.getRoutePoints(); // 기존 안내 경로
        // 위험 지역을 DB에서 조사해 1차 필터링을 거친 값들만 리스트로 변환하여 가져옴. --> 추후 JPA활용하여 처리

        // 안전 지역 1,2차 필터링 ( bbox, 경로상 50m 이내 )
        List<SafetyFacPointDto> filteredSafetyFacPoints = safeFacFilterSvc.filterSafetyFacPoints(routePoints, allSafetyPoints);
        // 위험 리스트 1, 2, 3차 필터링( bbox, 경로상 50m 이내, 연속된 위험지역 건너뛰기 )
        List<RiskPointDto> filteredDangerPoints = riskFilterSvc.filterDangerPoints(routePoints, allDangerPoints);

        // 만약 필터링 된 위험리스트가 비어있으면 기존 경로 안전점수 계산 후 반환
        if(filteredDangerPoints.isEmpty()){
            // 기존 경로 안전 점수
            int safety_score = getSafetyScore(routePoints, filteredSafetyFacPoints, filteredDangerPoints);
            return SafeRouteResponseDto.builder().detourRoute(originRoute).safety_score(safety_score).build();
        }

        // 우회 경유지 목록 생성 ( null이면 기본 경로 반환)
        List<DetourWayPointDto> detourPoints = detourRouteSvc.getDetourWayPoints(routePoints, filteredDangerPoints);
        RouteResponseDto detourRoute = tmapRouteSvc.getDetourRoute(routeRequestDto, detourPoints); // ++추가 TmapAPI 호출
        List<RoutePointDto> detourRoutePoints = detourRoute.getRoutePoints();

        System.out.println("우회 경유지 목록: " + Arrays.deepToString(detourPoints.toArray()));
        System.out.println("우회 경유지 크기: " + detourPoints.size() );
        System.out.println("기존 경로 총 시간: " + originRoute.getTotalTime());
        System.out.println("기존 경로 총 거리: " + originRoute.getTotalDistance());
        System.out.println("우회 경로 총 시간: " + detourRoute.getTotalTime());
        System.out.println("우회 경로 총 거리: " + detourRoute.getTotalDistance());

        // 우회 경유지가 기존 경로보다 300m가 넓다면
        if(detourRoute.getTotalDistance() - originRoute.getTotalDistance() > MAX_DETOUR_DISTANCE_METER ){

            // 위험경로를 한 번만 우회하는 경로 생성
            DetourWayPointDto singleWayPoint = detourRouteSvc.createSingleDetourWayPoint(routePoints, filteredDangerPoints.get(0) );
            RouteResponseDto singleDetourRoute = tmapRouteSvc.getDetourRoute(routeRequestDto, List.of(singleWayPoint)); // 싱글 우회경로 생성
            List<RoutePointDto> singleRoutePoints = singleDetourRoute.getRoutePoints();

            // 안전 지역 1,2차 필터링 ( bbox, 경로상 50m 이내 )
            List<SafetyFacPointDto> singleFilteredSafetyFacPoints = safeFacFilterSvc.filterSafetyFacPoints(singleRoutePoints, allSafetyPoints);
            // 위험 리스트 1, 2, 3차 필터링( bbox, 경로상 50m 이내, 연속된 위험지역 건너뛰기 )
            List<RiskPointDto> singleFilteredDangerPoints = riskFilterSvc.filterDangerPoints(singleRoutePoints, allDangerPoints);

            int safeCount = singleFilteredSafetyFacPoints.stream().mapToInt(SafetyFacPointDto::getSafeCount).sum();
            int riskCount = singleFilteredDangerPoints.stream().mapToInt(RiskPointDto::getRiskCount).sum();
            System.out.println("경로에 있는 안전시설물 개수: " + safeCount);
            System.out.println("경로에 있는 위험물 개수: " + riskCount);

            // 싱글 경유지 우회 경로 안전 점수
            int safety_score = getSafetyScore(singleDetourRoute.getRoutePoints(), singleFilteredSafetyFacPoints, singleFilteredDangerPoints);
            return SafeRouteResponseDto.builder().detourRoute(singleDetourRoute).safety_score(safety_score).build();
        }

        // 안전 지역 1,2차 필터링 ( bbox, 경로상 50m 이내 )
        List<SafetyFacPointDto> detourFilteredSafetyFacPoints = safeFacFilterSvc.filterSafetyFacPoints(detourRoutePoints, allSafetyPoints);
        // 위험 리스트 1, 2, 3차 필터링( bbox, 경로상 50m 이내, 연속된 위험지역 건너뛰기 )
        List<RiskPointDto> detourFilteredDangerPoints = riskFilterSvc.filterDangerPoints(detourRoutePoints, allDangerPoints);

        // 여러 경유지 우회 경로 안전 점수
        int safety_score = getSafetyScore(detourRoute.getRoutePoints(), detourFilteredSafetyFacPoints, detourFilteredDangerPoints);
        return SafeRouteResponseDto.builder()
                .detourRoute(detourRoute).safety_score(safety_score).build();
    }

    // 안전 점수 계산 로직: 경로(우회경로 or 기본 경로) 상의 위험지역 개수와 안전시설물의 개수를 따진다. 안전시설물은 어떤 안전시설물인지에 따라 차등을 다르게 둔다.
    private int getSafetyScore( List<RoutePointDto> routePoints, List<SafetyFacPointDto> safetyFacPoints, List<RiskPointDto> dangerPoints ){
        // 각 위치들의 개수 합치기 (안전 시설물은 CCTV/보안등, 경찰서, 안전지킴이집, 안전벨
        int safeCount = safetyFacPoints.stream().mapToInt(safeFac -> getFacScore(safeFac.getFacType()) * safeFac.getSafeCount() ).sum();
        int riskCount = dangerPoints.stream().mapToInt(RiskPointDto::getRiskCount).sum() * -7;

        System.out.println("safeCount: " + safeCount);
        System.out.println("riskCount: " + riskCount);

        return 100 + safeCount + riskCount;
    }

    private int getFacScore(String FacType) {
        return switch (FacType) {
            case "보안등" -> 1; // 제일 낮음, 강제성 없음
            case "CCTV", "안전벨" -> 2; // 억제력 있음, 고장 있을 수 있음
            case "안전지킴이집" -> 4; // 도움을 요청할 사람이 존재함
            case "경찰서" -> 5; // 가장 강력하게 도움이 되는 사람이 존쟇ㅁ.
            default -> 0;
        };
    }

}
