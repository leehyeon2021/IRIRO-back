package iriro.saferoute.service;

import iriro.common.exception.EmailNotFoundException;
import iriro.common.exception.LogSaveException;
import iriro.community.entity.UserEntity;
import iriro.community.repository.UserRepository;
import iriro.saferoute.dto.*;
import iriro.saferoute.entity.LocationlogEntity;
import iriro.saferoute.entity.RoutePointLogEntity;
import iriro.saferoute.repository.LocationLogRepository;
import iriro.saferoute.repository.RoutePointLogRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class RouteLogSaveService {
    private static final Integer GUEST_USER_ID = 1; // 비회원 user_id

    // 사용자 로그 저장하는 서비스
    private final LocationLogRepository locationLogRepo;
    private final RoutePointLogRepository routePointLogRepo;
    private final UserRepository userRepo;

    // 로그 저장 함수
    public Long saveRouteLog(SafeRouteResponseDto safeRouteResponse, String email ){

        RouteResponseDto originRoute = safeRouteResponse.getDetourRoute();
        int safety_score = safeRouteResponse.getSafety_score();

        // SaveLogDto로 변환
        SaveLogDto saveLog = originRoute.toSaveLogDto();
        log.info("saveLogDto = {}", saveLog);
        saveLog.setSafetyScore(safety_score); //안전 점수 추가

        // 로그 1차 저장 후 로그 Id 받아오기
        Long logId = createRouteLog(saveLog, email);
        // 로그Id 경로까지 저장 후 잘 저장되었는지 반환하기 ( 로그 아이디와, 경로 배열을 반환)
        List<RoutePointDto> safeRoutePoints = originRoute.getRoutePoints();
        boolean result = saveLogRoute( logId, safeRoutePoints );

        if(result) return logId; // 경로 저장까지 성공 시 로그아이디 반환
        else return 0L; // 실패시 0L 반환
    }

    // 경로 추천 시 일단 경로 제외 로그에 저장
    public Long createRouteLog(SaveLogDto saveLogDto, String email){
        log.info("RouteLogSaveService.createRouteLog saveLogDto = {}", saveLogDto);
        //후기와 사용자 빼고 다 저장 가능.
        LocationlogEntity saveEntity = saveLogDto.toEntity();
        UserEntity saveUser;
        // 이메일 확인
        if(email == null || email.isBlank()){
            saveUser = UserEntity.builder().userId(GUEST_USER_ID).build();
        }else{ // 이메일이 있으면
            saveUser = userRepo.findByEmail(email)
                    .orElseThrow(() -> new EmailNotFoundException("email is not found"));
        }

        saveEntity.setUserEntity( saveUser ); // 유저엔티티 저장

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
    public void updateLogRating(RouteRatingRequestDto routeRatingRequest){
        // 후기 저장
        LocationlogEntity logEntity = locationLogRepo.findById(routeRatingRequest.getLogId())
                .orElseThrow(() -> new LogSaveException("log is not found"));

        logEntity.setRating(routeRatingRequest.getRating());
    }

}
