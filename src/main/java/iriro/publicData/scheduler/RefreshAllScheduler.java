package iriro.publicData.scheduler;

import iriro.publicData.service.CrimeRoadFetchService;
import iriro.publicData.service.FacilitySafeFetchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshAllScheduler {
    private final FacilitySafeFetchService ffs;
    private final CrimeRoadFetchService cfs;

    // 전체 삭제 후 새 데이터로 교체
    //@Scheduled(cron = "0 0 1 1 * *")  // 매월 1일 새벽 2시
    public boolean refreshAllData() {

        boolean isPoliceUpdated = ffs.fetchPoliceStation();
        boolean isSafeHouseUpdated = ffs.fetchSafeHouse();
        boolean isSafeFacUpdated = ffs.fetchSafeFac();
        boolean isCrimeRoadUpdated = cfs.fetchCrimeRoad();

        // 2. 4개 모두 true 확인 반환.
        return isPoliceUpdated && isSafeHouseUpdated && isSafeFacUpdated && isCrimeRoadUpdated;

    }
}
