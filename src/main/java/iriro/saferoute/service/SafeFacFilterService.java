package iriro.saferoute.service;

import iriro.saferoute.dto.RoutePointDto;
import iriro.saferoute.dto.SafetyFacPointDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SafeFacFilterService {
    private static final double SAFE_RADIUS = 50.0; // 안전 반경 상수

    private final GeoFilterService geoFilterSvc;

    // 안전지역 필터링 함수(1, 2차)
    public List<SafetyFacPointDto> filterSafetyFacPoints(List<RoutePointDto> routePoints, List<SafetyFacPointDto> inSafetyPoints){
        // [2차 필터] : 1차 필터링된 위험 위치 리스트를 경로 50m 안에 들어오는 위험 위치들만 필터링, 길의 타입에 따라 다르게 필터링.
        // 2차 필터 적용 후 바로 반환
        return inSafetyPoints.stream().filter(point ->
                geoFilterSvc.getMinDistance(routePoints, point.getLatitude(), point.getLongitude()) <= SAFE_RADIUS ).toList();
    }

}
