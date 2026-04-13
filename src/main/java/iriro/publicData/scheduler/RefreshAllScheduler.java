package iriro.publicData.scheduler;

import iriro.publicData.service.CrimeRoadFetchService;
import iriro.publicData.service.FacilitySafeFetchService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshAllScheduler {
    private final FacilitySafeFetchService ffs;
    private final CrimeRoadFetchService cfs;

    // 전체 업데이트 (불필요 데이터 삭제 + 수정되었거나 없는 데이터 추가 )
    @Scheduled(cron = "0 0 1 1 * *")  // 매월 1일 새벽 2시
    public boolean refreshAllData() {


        System.out.println("========== [경찰서 수집 시작] ==========");
        boolean isPoliceUpdated = ffs.fetchPoliceStation();
        System.out.println("========== [경찰서 수집 결과] " + isPoliceUpdated + " ==========");

        System.out.println("========== [안전지킴이집 수집 시작] ==========");
        boolean isSafeHouseUpdated = ffs.fetchSafeHouse();
        System.out.println("========== [안전지킴이집 수집 결과] "+isSafeHouseUpdated+" ==========");

        System.out.println("========== [안전시설물 수집 시작] ==========");
        boolean isSafeFacUpdated = ffs.fetchSafeFac();
        System.out.println("========== [안전시설물 수집 결과] "+isSafeFacUpdated+" ==========");

        System.out.println("========== [위험도로명 수집 시작] ==========");
        boolean isCrimeRoadUpdated = cfs.fetchCrimeRoad();
        System.out.println("========== [위험도로명 수집 결과] "+isCrimeRoadUpdated+" ==========");

        // 2. 4개 모두 true 확인 반환.
        return isPoliceUpdated && isSafeHouseUpdated && isSafeFacUpdated && isCrimeRoadUpdated;

    }
}
