package iriro.saferoute.service;

import iriro.publicData.entity.CrimeRoadEntity;
import iriro.publicData.entity.FacilitySafeEntity;
import iriro.publicData.repository.CrimeRoadRepository;
import iriro.publicData.repository.FacilitySafeRepository;
import iriro.saferoute.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SafeRouteService {

    private static final double BBOX_MARGIN_METER = 300.0; // BBox 여유 반경
    private static final double MAX_DETOUR_RATIO = 2.0; // 기본 경로 대비 허용 가능한 최대 우회 비율

    private static final double SOFT_BAND_M = 40.0; // 위험 반경 바깥쪽의 완충 구간 거리(경고 구역)
    private static final double SOFT_PEAK = 0.35; // 위험 노출을 얼마나 줄여 반영할지 결정(경고 구간에서는 0.35배 정도) -> 높이면 완충구간에도 더 민감하게 반응
    private static final double EXPOSURE_TO_POINTS = 2.6; // 위험 노출을 실제 점수 차감 포인트로 바꾸는 계수(높이면 더 민감하게 반응)
    private static final int MAX_RISK_DEDUCTION = 88; // 위험요소때문에 아무리 많이 깎여도 최대 88점까지 깍임
    private static final int MAX_FACILITY_BONUS = 8; // 안전시설 보너스 최대치 점수

    private final TmapRouteService tmapRouteSvc;
    private final CandidateRouteService candidateRouteSvc;
    private final SafeFacFilterService safeFacFilterSvc;
    private final GeoFilterService geoFilterSvc;
    private final RiskFilterService riskFilterSvc;
    private final CrimeRoadRepository crimeRoadRepo;
    private final FacilitySafeRepository facilitySafeRepo;

    // 안전 경로 반환 함수
    public SafeRouteResponseDto getSafeRoute(RouteRequestDto req) {

        RouteResponseDto baseRoute = tmapRouteSvc.getPedestrianRoute(req); // 기본 경로 조회
        BboxDto bbox = createBox(baseRoute.getRoutePoints(), req); // bbox 계산

        // bbox내 안전시설 조회
        List<SafetyFacPointDto> allSafety = facilitySafeRepo
                .findAllInBoundingBox(
                        BigDecimal.valueOf(bbox.getMinLat()), BigDecimal.valueOf(bbox.getMaxLat()),
                        BigDecimal.valueOf(bbox.getMinLng()), BigDecimal.valueOf(bbox.getMaxLng()))
                .stream().map(FacilitySafeEntity::toSafetyFacPointDto).toList();

        // bbox내 위험시설 조회
        List<RiskPointDto> allDanger = crimeRoadRepo
                .findAllInBoundingBox(
                        BigDecimal.valueOf(bbox.getMinLat()), BigDecimal.valueOf(bbox.getMaxLat()),
                        BigDecimal.valueOf(bbox.getMinLng()), BigDecimal.valueOf(bbox.getMaxLng()))
                .stream().map(CrimeRoadEntity::toRiskPointDto).toList();

        log.info("BBox 조회 - 안전시설={} 위험지점={}", allSafety.size(), allDanger.size());

        // 후보 경로 생성
        List<RouteResponseDto> candidates = candidateRouteSvc.generateCandidates(req, allDanger, baseRoute);
        int baseDist = Math.max(1, candidates.get(0).getTotalDistance()); // 기본 거리

        // 각 후보의 위험노출/거리비 로그 출력
        logCandidateTable(candidates, allDanger, baseDist);

        // 후보들 중 최종 선택
        RouteResponseDto best = pickLowestExposure(candidates, allDanger, baseDist);

        // 최종 선택 경로의 안전시설 보너스 계산
        int facRaw = facilityRawScore(best.getRoutePoints(), allSafety);
        double exp = computeExposure(best.getRoutePoints(), allDanger);
        int score = toDisplayScore(exp, facRaw);

        // 로그
        log.info("[ 최종 선택: {} | 거리={}m 예상시간={}s | 위험노출={} | 안전점수={} ]",
                labelForIndex(candidates, best),
                best.getTotalDistance(),
                best.getTotalTime() != null ? best.getTotalTime() : -1,
                String.format("%.2f", exp),
                score);

        return SafeRouteResponseDto.builder()
                .selectedRoute(best)
                .safety_score(score)
                .build();
    }

    // 로그를 위한 라벨 생성
    private String labelForIndex(List<RouteResponseDto> candidates, RouteResponseDto route) {
        int i = candidates.indexOf(route);
        if (i <= 0) {
            return "기본 경로(TMAP 보행)";
        }

        return "우회 경로(다중경유)";
    }

    // 후보 비교 로그 출력
    private void logCandidateTable(List<RouteResponseDto> candidates, List<RiskPointDto> allDanger, int baseDist) {
        log.info("========== 안전 경로 후보 비교 ==========");
        for (int idx = 0; idx < candidates.size(); idx++) {
            RouteResponseDto r = candidates.get(idx);
            List<RoutePointDto> pts = r.getRoutePoints();
            double exposure = computeExposure(pts, allDanger);
            double ratio = (double) r.getTotalDistance() / (double) baseDist;
            int nPts = pts != null ? pts.size() : 0;
            Integer tt = r.getTotalTime();
            log.info(
                    "[#{}] {} | 거리={}m 예상시간={}s 꼭짓점={}개 | 위험노출={} | 기준대비거리={}",
                    idx,
                    idx == 0 ? "기본 경로(TMAP 보행)" : "우회 경로(다중경유)",
                    r.getTotalDistance(),
                    tt != null ? tt : -1,
                    nPts,
                    String.format("%.3f", exposure),
                    String.format("%.3f", ratio));
        }
        log.info("========================================");
    }

    // 최종 경로 선택
    private RouteResponseDto pickLowestExposure(
            List<RouteResponseDto> candidates,
            List<RiskPointDto> allDanger,
            int baseDist) {

        RouteResponseDto bestOk = null;
        double expOk = Double.MAX_VALUE;

        RouteResponseDto bestAny = null;
        double expAny = Double.MAX_VALUE;

        // 후보들의 위험 노출도 계산
        for (RouteResponseDto c : candidates) {
            List<RoutePointDto> pts = c.getRoutePoints();
            double ex = computeExposure(pts, allDanger);
            double ratio = (double) c.getTotalDistance() / (double) baseDist; // 기본경로와의 거리 비율

            if (bestAny == null || ex < expAny - 1e-9
                    || (Math.abs(ex - expAny) < 1e-9 && c.getTotalDistance() < bestAny.getTotalDistance())) {
                expAny = ex;
                bestAny = c;
            }
            if (ratio <= MAX_DETOUR_RATIO) { // 만약 1.5배가 안 넘으면
                if (bestOk == null || ex < expOk - 1e-9
                        || (Math.abs(ex - expOk) < 1e-9 && c.getTotalDistance() < bestOk.getTotalDistance())) {
                    expOk = ex;
                    bestOk = c;
                }
            }
        }

        if (bestOk != null) {
            log.info("선택 규칙: 거리비≤{} 이면서 위험노출 최소", MAX_DETOUR_RATIO);
            return bestOk;
        }
        log.warn("거리비 {} 초과만 가능 → 위험노출 최소 후보로 대체", MAX_DETOUR_RATIO);

        return bestAny != null ? bestAny : candidates.get(0);
    }

    // 위험 노출도를 합산하여 반환하는 함수
    private double computeExposure(List<RoutePointDto> polyline, List<RiskPointDto> dangers) {
        if (polyline == null || polyline.size() < 2 || dangers == null || dangers.isEmpty()) {
            return 0.0;
        }
        double total = 0.0;
        for (RiskPointDto r : dangers) {
            if (r.getLatitude() == null || r.getLongitude() == null) {
                continue;
            }
            double d = geoFilterSvc.getMinDistance(polyline, r.getLatitude(), r.getLongitude());
            double hardR = riskFilterSvc.getDangerRadius(r.getRoadType());
            int w = Math.max(1, r.getRiskCount());
            if (d <= hardR) {
                total += w * (1.0 - d / Math.max(hardR, 1e-3));
            } else if (d <= hardR + SOFT_BAND_M) {
                double t = (d - hardR) / SOFT_BAND_M;
                total += w * SOFT_PEAK * (1.0 - t);
            }
        }

        return total;
    }

    // 내부의 계산값을 0~100점 사이로 바꾸는 함수.
    private int toDisplayScore(double exposure, int facilityRawSum) {
        int fac = Math.min(MAX_FACILITY_BONUS, facilityRawSum / 4); // 안전 시설 보너스

        // 위험 차감 계산
        int d = (int) Math.ceil(exposure * EXPOSURE_TO_POINTS);
        d = Math.min(MAX_RISK_DEDUCTION, d); // 아무리 크게 깎여도 12점 미만으로 안가게 최소값으로 설정

        return Math.max(0, Math.min(100, 100 - d + fac));
    }

    // 안전 시설 보너스 계산
    private int facilityRawScore(List<RoutePointDto> routePoints, List<SafetyFacPointDto> allSafety) {
        List<SafetyFacPointDto> near = safeFacFilterSvc.filterSafetyFacPoints(routePoints, allSafety);

        return near.stream()
                .mapToInt(p -> getFacScore(p.getFacType()) * Math.max(p.getSafeCount(), 1))
                .sum();
    }

    // 안전 시설물 가중치
    private int getFacScore(String t) {
        if (t == null) {
            return 0;
        }

        return switch (t) {
            case "보안등" -> 1;
            case "CCTV", "안전벨" -> 2;
            case "안전지킴이집" -> 4;
            case "경찰서" -> 5;
            default -> 0;
        };
    }

    // bbox 생성
    private BboxDto createBox(List<RoutePointDto> routePoints, RouteRequestDto req) {
        double minLat = Math.min(req.getStartLat(), req.getEndLat());
        double maxLat = Math.max(req.getStartLat(), req.getEndLat());
        double minLng = Math.min(req.getStartLng(), req.getEndLng());
        double maxLng = Math.max(req.getStartLng(), req.getEndLng());
        if (routePoints != null) {
            for (RoutePointDto p : routePoints) {
                minLat = Math.min(minLat, p.getLatitude());
                maxLat = Math.max(maxLat, p.getLatitude());
                minLng = Math.min(minLng, p.getLongitude());
                maxLng = Math.max(maxLng, p.getLongitude());
            }
        }
        double cLat = (minLat + maxLat) / 2.0;
        double latM = BBOX_MARGIN_METER / 111000.0;
        double lngM = BBOX_MARGIN_METER / (111000.0 * Math.cos(Math.toRadians(cLat)));

        return new BboxDto(minLat - latM, maxLat + latM, minLng - lngM, maxLng + lngM);
    }
}
