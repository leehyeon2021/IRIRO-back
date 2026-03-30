package iriro.saferoute.service;

import iriro.common.exception.LogSaveException;
import iriro.saferoute.dto.RoutePointDto;
import iriro.saferoute.dto.SaveLogDto;
import iriro.saferoute.entity.LocationlogEntity;
import iriro.saferoute.entity.RoutePointLogEntity;
import iriro.saferoute.repository.LocationLogRepository;
import iriro.saferoute.repository.RoutePointLogRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class RouteLogSaveService {
    // 사용자 로그 저장하는 서비스
    private final LocationLogRepository locationLogRepo;
    private final RoutePointLogRepository routePointLogRepo;

    // 경로 추천 시 일단 경로 제외 로그에 저장
    public Long createRouteLog(SaveLogDto saveLogDto){
        log.info("RouteLogSaveService.createRouteLog saveLogDto = {}", saveLogDto);
        //후기와 사용자 빼고 다 저장 가능.
        LocationlogEntity saveEntity = saveLogDto.toEntity();
        return locationLogRepo.save( saveEntity ).getLogId();
    }

    // 경로 추천 시 경로 저장
    public boolean saveLogRoute(Long logId, List<RoutePointDto> routePoints){
        // 경로 리스트 체크 --> 비어있다면 저장할 수 없으므로 false 반환
        if(routePoints == null || routePoints.isEmpty()) return false;

        // 경로 저장
        LocationlogEntity locationlogEntity = locationLogRepo.findById(logId)
                .orElseThrow(() -> new LogSaveException("logId is not exists"));

        List<RoutePointLogEntity> pointEntities = routePoints.stream()
                .map(point -> RoutePointLogEntity.builder()
                        .locationlogEntity(locationlogEntity)
                        .latitude(BigDecimal.valueOf(point.getLatitude()))
                        .longitude(BigDecimal.valueOf(point.getLongitude()))
                        .sequence(point.getSequence())
                        .build())
                .toList();
        routePointLogRepo.saveAll(pointEntities);
        return true;
    }

    // 후기(별점)을 받으면 추가로 저장 --> update에 가깝다.
    public boolean updateLogRating(Long logId, Integer rating){
        // 후기 저장, 사용자 저장 (update하기)
        return true;
    }

}
