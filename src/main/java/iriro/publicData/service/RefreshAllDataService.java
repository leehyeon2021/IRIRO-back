package iriro.publicData.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service @Transactional @RequiredArgsConstructor
public class RefreshAllDataService {
    private final FacilitySafeService fs;
    private final CrimeRoadService cs;

    /*
    // 전체 삭제 후 새 데이터로 교체
    @Scheduled(cron = "0 0 1 1 * *")  // 매월 1일 새벽 2시
    public void refreshAllData() {

        // 1. 전체 삭제
        fs.deleteAll();
        cs.deleteAll();

        // 2. 순서대로 저장
        fs.fetchPoliceStation();    // 경찰서
        fs.fetchSafeHouse();        // 안심지킴이집
        fS.fetchSafeLight();        // 보안등
        fS.fetchCctv();             // CCTV
        fs.fetchSafeBell();         // 안전벨
        cs.fetchCrimeRoad();        // 범죄자도로명
    }
     */
}
