package iriro.saferoute.service;

import iriro.common.exception.LogSaveException;
import iriro.publicData.entity.CrimeRoadEntity;
import iriro.publicData.entity.FacilitySafeEntity;
import iriro.publicData.repository.CrimeRoadRepository;
import iriro.publicData.repository.FacilitySafeRepository;
import iriro.saferoute.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SafeRouteService {
    private static final double MAX_DETOUR_RATIO = 1.20; // 늘어난 비율이 20%가 넘으면
    private static final int BASE_SAFE_SCORE = 100;

    private final TmapRouteService tmapRouteSvc; // TmapAPI 사용 서비스
    private final RiskFilterService riskFilterSvc; // 위험 지역 필터 서비스
    private final SafeFacFilterService safeFacFilterSvc; // 안전 지역 필터 서비스
    private final DetourRouteService detourRouteSvc; // 우회 경로 서비스
    private final RouteLogSaveService routeLogSaveSvc;
    private final CrimeRoadRepository crimeRoadRepository;
    private final FacilitySafeRepository facilitySafeRepository;

    // boundingBox를 만드는 함수 .. 1차 필터링
    public BboxDto createBox(List<RoutePointDto> routePoints){

        // 만약 리스트가 비어있다면
        if( routePoints == null || routePoints.isEmpty()){
            System.out.println("경로 좌표가 비어있습니다.");
            return null;
        }

        double minLat = routePoints.get(0).getLatitude();
        double maxLat = routePoints.get(0).getLatitude();
        double minLng = routePoints.get(0).getLatitude();
        double maxLng = routePoints.get(0).getLatitude();

        // 가장 크고 작은 위 경도 값 구하기
        for(RoutePointDto point : routePoints){
            minLat = Math.min(minLat, point.getLatitude() );
            minLng = Math.min(minLng, point.getLongitude() );
            maxLat = Math.max(maxLat, point.getLatitude() );
            maxLng = Math.max(maxLng, point.getLongitude() );
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

    // 로그를 후기(평점)제외하고 저장하고 lodId를 반환하는 함수 --> API 명세서에 추가해야함
    public Long saveRouteLog(RouteResponseDto originRoute, int safety_score ){

        // SaveLogDto로 변환
        SaveLogDto saveLog = originRoute.toSaveLogDto();
        saveLog.setSafetyScore(safety_score); //안전 점수 추가

        // 로그 1차 저장 후 로그 Id 받아오기
        Long logId = routeLogSaveSvc.createRouteLog(saveLog);
        // 로그Id 경로까지 저장 후 잘 저장되었는지 반환하기 ( 로그 아이디와, 경로 배열을 반환)
        List<RoutePointDto> safeRoutePoints = originRoute.getRoutePoints();
        boolean result = routeLogSaveSvc.saveLogRoute( logId, safeRoutePoints );

        if(result) return logId; // 경로 저장까지 성공 시 로그아이디 반환
        else return 0L; // 실패시 0L 반환
    }

    // 로그를 저장하고 빌드하여 응답객체를 반환
    private SafeRouteResponseDto buildResponse(RouteResponseDto route, RouteResponseDto originRoute, int safetyScore){
        Long logId = saveRouteLog(originRoute, safetyScore);
        if (logId == 0L) { // 예외처리
            throw new LogSaveException("log_id is 0L");
        }

        return SafeRouteResponseDto.builder()
                .detourRoute(route)
                .safety_score(safetyScore)
                .logId(logId)
                .build();
    }

    // 안전 경로 계산 함수
    public SafeRouteResponseDto getSafeRoute(RouteRequestDto routeRequestDto){

        int safety_score;
        // (출발지 위/경도, 목적지 위/경도, 경로 배열, 총걸린 시간, 총 거리)를 반환
        RouteResponseDto originRoute = tmapRouteSvc.getPedestrianRoute(routeRequestDto); // 경로 생성 API 1번 호출
        List<RoutePointDto> routePoints = originRoute.getRoutePoints(); // 기존 안내 경로
        // 위험 지역을 DB에서 조사해 1차 필터링을 거친 값들만 리스트로 변환하여 가져옴. --> 추후 JPA활용하여 처리

        BboxDto bbox = createBox(routePoints);
        //지금은 위험지역 샘플을 가져오지만 조회를 통해 가져옴 테스트 리스트 -> 추후에 삭제
        List<SafetyFacPointDto> allSafetyPoints = facilitySafeRepository.findAllInBoundingBox(
                BigDecimal.valueOf(bbox.getMinLat()), BigDecimal.valueOf(bbox.getMaxLat()),
                BigDecimal.valueOf(bbox.getMinLng()), BigDecimal.valueOf(bbox.getMaxLng()))
                .stream().map(FacilitySafeEntity::toSafetyFacPointDto).toList(); // 해당 엔티티를 SafetyFacPoint로 변경

        List<RiskPointDto> allDangerPoints = crimeRoadRepository.findAllInBoundingBox(
                BigDecimal.valueOf(bbox.getMinLat()), BigDecimal.valueOf(bbox.getMaxLat()),
                BigDecimal.valueOf(bbox.getMinLng()), BigDecimal.valueOf(bbox.getMaxLng()))
                .stream().map(CrimeRoadEntity::toRiskPointDto).toList();

        System.out.println("안전시설물Dto: " + allSafetyPoints);
        System.out.println("위험Dto: " + allDangerPoints);

        List<RiskPointDto> riskFilterList = riskFilterSvc.filterDangerPoints(routePoints , allDangerPoints);
        // 만약 필터링 된 위험리스트가 비어있으면 기존 경로 안전점수 계산 후 반환
        if( riskFilterList.isEmpty()){
            System.out.println("위험리스트가 비어있습니다. 기존경로를 반환합니다.");
            // 기존 경로 안전 점수
            safety_score = calcSafetyScore(routePoints, allSafetyPoints, allDangerPoints);
            return buildResponse(originRoute, originRoute, safety_score); // 1차 기본경로 리턴
        }

        // 우회 경유지 목록 생성
        List<DetourWayPointDto> detourPoints = detourRouteSvc.getDetourWayPoints(routePoints, riskFilterList);

        // 우회 경유지가 없다면 기본 경로 반환
        if(detourPoints.isEmpty()){
            System.out.println("우회한 경유지가 없습니다. 기본 경로를 반환합니다.");
            safety_score = calcSafetyScore(routePoints, allSafetyPoints, allDangerPoints);
            return buildResponse(originRoute, originRoute, safety_score); // 2차 기본 경로 리턴
        }

        RouteResponseDto detourRoute = tmapRouteSvc.getDetourRoute(routeRequestDto, detourPoints); // ++추가 TmapAPI 호출

        System.out.println("우회 경유지 목록: " + Arrays.deepToString(detourPoints.toArray()));
        System.out.println("우회 경유지 크기: " + detourPoints.size() );
        System.out.println("기존 경로 총 시간: " + originRoute.getTotalTime());
        System.out.println("기존 경로 총 거리: " + originRoute.getTotalDistance());
        System.out.println("우회 경로 총 시간: " + detourRoute.getTotalTime());
        System.out.println("우회 경로 총 거리: " + detourRoute.getTotalDistance());

        double detourRatio = (double)detourRoute.getTotalDistance() / originRoute.getTotalDistance();
        // 우회 경유지가 기존 경로보다 1.2배 길다면
        if(  detourRatio > MAX_DETOUR_RATIO ){
            // 위험경로를 한 번만 우회하는 경로 생성
            DetourWayPointDto singleWayPoint = detourRouteSvc.createSingleDetourWayPoint(routePoints, riskFilterList.get(0) );
            RouteResponseDto singleDetourRoute = tmapRouteSvc.getDetourRoute(routeRequestDto, List.of(singleWayPoint)); // 싱글 우회경로 생성

            // 한 번 더 우회했지만 비율이 여전히 20%가 넘으면 기본 경로로 반환
            double detourRatio2 = (double)singleDetourRoute.getTotalDistance() / originRoute.getTotalDistance();
            if( detourRatio2 > MAX_DETOUR_RATIO){
                System.out.println("한 번 더 우회했지만 거리가 멉니다. 기본경로를 반환합니다.");
                safety_score = calcSafetyScore(routePoints, allSafetyPoints, allDangerPoints);
                return buildResponse(originRoute, originRoute, safety_score); // 3차 기본 경로 리턴
            }

            // 싱글 경유지 우회 경로 안전 점수
            safety_score = calcSafetyScore(singleDetourRoute.getRoutePoints(), allSafetyPoints, allDangerPoints);
            return buildResponse(singleDetourRoute, originRoute, safety_score); // 4차 단일 우회경로 리턴
        }


        // 여러 경유지 우회 경로 안전 점수
        safety_score = calcSafetyScore(detourRoute.getRoutePoints(), allSafetyPoints, allDangerPoints );
        return buildResponse(detourRoute, originRoute, safety_score); // 5차 우회 경로 리턴
    }

    // 안전 점수 계산 로직: 경로(우회경로 or 기본 경로) 상의 위험지역 개수와 안전시설물의 개수를 따진다. 안전시설물은 어떤 안전시설물인지에 따라 차등을 다르게 둔다.
    private int calcSafetyScore( List<RoutePointDto> routePoints, List<SafetyFacPointDto> allSafetyFacPoints, List<RiskPointDto> allDangerPoints ){
        // 경로에 대해서 안전 시설물과 위험시설물에 대하여 가져온다,

        List<SafetyFacPointDto> safetyFacPoints = safeFacFilterSvc.filterSafetyFacPoints(routePoints, allSafetyFacPoints);
        List<RiskPointDto> riskPoints = riskFilterSvc.filterDangerPoints(routePoints, allDangerPoints);

        System.out.println("우회한 경로의 안전시설물Dto: " + safetyFacPoints);
        System.out.println("우회한 경로의 위험Dto: " + riskPoints);

        // 각 위치들의 개수 합치기 (안전 시설물은 CCTV/보안등, 경찰서, 안전지킴이집, 안전벨
        int safeCount = safetyFacPoints.stream().mapToInt(safeFac -> getFacScore(safeFac.getFacType()) * safeFac.getSafeCount() ).sum();
        int riskCount = riskPoints.stream().mapToInt(RiskPointDto::getRiskCount).sum() * -7;

        System.out.println("안전점수(보안등1점, CCTV/안전벨2점, 안전지킴이집4점, 경찰서5점): " + safeCount);
        System.out.println("위험점수(위험경로당-7점): " + riskCount);
        System.out.println("안전점수(기본점수 100점): " + (BASE_SAFE_SCORE + safeCount + riskCount));

        return BASE_SAFE_SCORE + safeCount + riskCount;
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
