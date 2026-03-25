package iriro.publicData.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service @RequiredArgsConstructor
public class RefreshAllDataService {

// 저장/조회 클래스 분리했으므로 아래 수정 필요
    private final FacilitySafeFetchService ffs;
    private final CrimeRoadFetchService cfs;

    // 전체 삭제 후 새 데이터로 교체
    @Scheduled(cron = "0 0 1 1 * *")  // 매월 1일 새벽 2시
    public void refreshAllData() {

        // 1. 전체 삭제 - db에 들어간 파일 전체 삭제

        // 2. 순서대로 저장 - sleep 같은 거 해야할 것 같음
        ffs.fetchPoliceStation();    // 경찰서
        ffs.fetchSafeHouse();        // 안심지킴이집
        ffs.fetchSafeFac();          // 안전시설물(보안등,CCTV,안전벨)
        cfs.fetchCrimeRoad();        // 위험도로명 - 가장 많음(오래걸림!!)
    }
}
