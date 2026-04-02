package iriro.saferoute.service;

import iriro.publicData.entity.CrimeRoadEntity;
import iriro.publicData.entity.FacilitySafeEntity;
import iriro.publicData.repository.CrimeRoadRepository;
import iriro.publicData.repository.FacilitySafeRepository;
import iriro.saferoute.dto.BboxDto;
import iriro.saferoute.dto.RiskPointDto;
import iriro.saferoute.dto.SafetyFacPointDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MapMarkerService {
    private static final double SEARCH_METER = 1000.0;

    private final CrimeRoadRepository crimeRoadRepo;
    private final FacilitySafeRepository facilitySafeRepo;

    // 사용자 기준 사각형 찾을 범위(1km) 이내로 위치값 계산
    public BboxDto createBoundingBox(double userLat, double userLng){
        double latMargin = SEARCH_METER / 111000.0;
        double lngMargin = SEARCH_METER / (111000.0 * Math.cos(Math.toRadians(userLat)));

        return new BboxDto(
                userLat - latMargin, userLat + latMargin,
                userLng - lngMargin, userLng + lngMargin);
    }

    // 안전 경로 가져오기
    public List<SafetyFacPointDto> getSafeMark(Double latitude, Double longitude){
        BboxDto bbox = createBoundingBox(latitude, longitude);
        return facilitySafeRepo
                .findAllInBoundingBox(
                        BigDecimal.valueOf(bbox.getMinLat()), BigDecimal.valueOf(bbox.getMaxLat()),
                        BigDecimal.valueOf(bbox.getMinLng()), BigDecimal.valueOf(bbox.getMaxLng())
                ).stream().map(FacilitySafeEntity::toSafetyFacPointDto).toList();
    }

    // 위험 경로 가져오기
    public List<RiskPointDto> getDangerMark(Double latitude, Double longitude){
        BboxDto bbox = createBoundingBox(latitude, longitude);
        return crimeRoadRepo
                .findAllInBoundingBox(
                        BigDecimal.valueOf(bbox.getMinLat()), BigDecimal.valueOf(bbox.getMaxLat()),
                        BigDecimal.valueOf(bbox.getMinLng()), BigDecimal.valueOf(bbox.getMaxLng())
                ).stream().map(CrimeRoadEntity::toRiskPointDto).toList();
    }
}
