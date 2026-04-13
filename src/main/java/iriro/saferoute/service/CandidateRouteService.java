package iriro.saferoute.service;

import iriro.saferoute.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CandidateRouteService {

    private static final double DETOUR_EXTRA_METER = 50.0; // 우회할 거리
    private static final int MAX_WAYPOINTS = 5; // 우회 경유지 최대 생성 개수
    private static final double WAYPOINT_MERGE_M = 15.0; // 가까운 경유지 합치는 기준

    private final TmapRouteService tmapRouteSvc;
    private final GeoFilterService geoFilterSvc;
    private final RiskFilterService riskFilterSvc;

    public List<RouteResponseDto> generateCandidates(
            RouteRequestDto req,
            List<RiskPointDto> allDangerPoints,
            RouteResponseDto baseRoute) {

        List<RouteResponseDto> res = new ArrayList<>();
        res.add(baseRoute); // 기본 경유 후보 경로로 등록

        List<RoutePointDto> routePoints = baseRoute.getRoutePoints();
        // 방어코드, 너무 좌표가 적으면 방향 계산을 못함
        if (routePoints == null || routePoints.size() < 2) {
            return res;
        }

        // 전체 위험지점 중 기본 경로에 실제로 영향을 주는 위험지점만 필터링
        List<RiskPointDto> onDangerPath = riskFilterSvc.filterDangerPoints(routePoints, allDangerPoints);
        if (onDangerPath.isEmpty()) {
            log.info("경로 인근 위험 없음 → 우회 후보 없음");
            return res;
        }

        // 위험 지점 정렬
        List<RiskPointDto> sortedDangerPath = onDangerPath.stream()
                .sorted(Comparator.comparingInt(r -> geoFilterSvc.getSequence(routePoints, r)))
                .toList();

        // 우회 경유지 목록 dwps = detour waypoints
        List<DetourWayPointDto> dwps = new ArrayList<>();
        int limit = Math.min(MAX_WAYPOINTS, sortedDangerPath.size()); // 경유지 5개 제한
        for (int i = 0; i < limit; i++) {
            // 각 위험지점을 기준으로 좌/우 법선 방향 우회 경유지 1개 계산하여 추가
            DetourWayPointDto dwp = perpendicularWaypoint(routePoints, sortedDangerPath.get(i), sortedDangerPath);
            if (dwp != null) {
                dwps.add(dwp);
            }
        }
        dwps = mergeCloseWaypoints(dwps); // 가까운 경유지 중복 제거
        if (dwps.isEmpty()) {
            return res;
        }

        // 우회점 경로 후보들 추가
        collectCandidates(req, res, dwps);
        return res;
    }

    // 근처 우회점 병합하는 함수
    private List<DetourWayPointDto> mergeCloseWaypoints(List<DetourWayPointDto> input) {
        List<DetourWayPointDto> DetourWayPointsList = new ArrayList<>();
        for (DetourWayPointDto dwp : input) {
            boolean duplicate = false; // 중복 플래그
            for (DetourWayPointDto point : DetourWayPointsList) {
                if (geoFilterSvc.distanceMeter(dwp.getLatitude(), dwp.getLongitude(),
                        point.getLatitude(), point.getLongitude()) < WAYPOINT_MERGE_M) {
                    duplicate = true;
                    break;
                }
            }
            if (!duplicate) {
                DetourWayPointsList.add(dwp);
            }
        }
        return DetourWayPointsList;
    }

    // 다중 우회 경로 생성 함수
    private void collectCandidates(
            RouteRequestDto req,
            List<RouteResponseDto> candidates,
            List<DetourWayPointDto> waypoints) {
        // 복사하여 계산
        List<DetourWayPointDto> cur = new ArrayList<>(waypoints);
        while (!cur.isEmpty()) {
            try {
                RouteResponseDto res = tmapRouteSvc.getDetourRoute(req, cur);
                candidates.add(res); // 생성에 성공시 후보  경로에 추가
                log.info("[우회 후보] 경유지={}개 거리={}m", cur.size(), res.getTotalDistance());
            } catch (Exception e) {
                log.warn("[우회] 경유지 {}개 실패: {} → 개수 축소", cur.size(), e.getMessage());
            } finally {
                cur.remove(cur.size() - 1); // 마지막 값 삭제
            }
        }
    }

    // 법선 방향 우회 경유지 계산 함수
    /*
        [핵심 원리]
            1. 위험지점이 경로상 어디쯤 있는지 확인
            2. 그 지점의 방향 벡터 구함
            3. 진행 방향의 왼쪽/오른쪽 방향 벡터 구함
            4. 두 방향 각각 우회 후보 좌표 만들어서 전체 위험지점과 더 멀어지는 쪽 선택
    */
    private DetourWayPointDto perpendicularWaypoint(
            List<RoutePointDto> routePoints,
            RiskPointDto anchor,
            List<RiskPointDto> allOnPath) {

        int seq = geoFilterSvc.getSequence(routePoints, anchor);
        int idx = seq - 1;
        if (idx <= 0 || idx >= routePoints.size()) {
            return null;
        }
        RoutePointDto curr = routePoints.get(idx);
        RoutePointDto prev = routePoints.get(idx - 1);

        double dy = curr.getLatitude() - prev.getLatitude();
        double dx = curr.getLongitude() - prev.getLongitude();
        double len = Math.hypot(dx, dy);
        if (len < 1e-12) { // 길이가 0에 수렴 시 방향 벡터 계산 불가
            return null;
        }
        double ux = dx / len;
        double uy = dy / len;
        double leftX = -uy;
        double leftY = ux;
        double rightX = uy;
        double rightY = -ux;

        double m = riskFilterSvc.getDangerRadius(anchor.getRoadType()) + DETOUR_EXTRA_METER;
        double mLat = 1.0 / 111000.0;
        double mLng = 1.0 / (111000.0 * Math.cos(Math.toRadians(curr.getLatitude())));

        double lLat = curr.getLatitude() + leftY * m * mLat;
        double lLng = curr.getLongitude() + leftX * m * mLng;
        double rLat = curr.getLatitude() + rightY * m * mLat;
        double rLng = curr.getLongitude() + rightX * m * mLng;

        double sL = weightedDistSum(lLat, lLng, allOnPath);
        double sR = weightedDistSum(rLat, rLng, allOnPath);
        return sL >= sR ? new DetourWayPointDto(lLat, lLng) : new DetourWayPointDto(rLat, rLng);
    }

    // 특정 좌표가 위험지점들과 얼마나 멀리 떨어지는 지 계산
    private double weightedDistSum(double lat, double lng, List<RiskPointDto> risks) {
        double s = 0;
        for (RiskPointDto risk : risks) {
            int weight = Math.max(1, risk.getRiskCount()); // 가중치
            s += weight * geoFilterSvc.distanceMeter(lat, lng, risk.getLatitude(), risk.getLongitude());
        }
        return s;
    }
}
