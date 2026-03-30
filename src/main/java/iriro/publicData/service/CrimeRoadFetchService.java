package iriro.publicData.service;

import iriro.common.service.GeocodingService;
import iriro.publicData.entity.CrimeRoadEntity;
import iriro.publicData.repository.CrimeRoadRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;

@Service @RequiredArgsConstructor @Transactional
public class CrimeRoadFetchService{

    @Value("${api.pub.service-key}")
    private String serviceKey;

    @Value("${api.pub.crime-road.url}")
    private String crimeRoadUrl;

    private final WebClient webClient;
    private final CrimeRoadRepository cr;
    private final GeocodingService gs;

    // 위험도로명(Map) 저장
    public boolean fetchCrimeRoad(){
        int numOfRows = 1000;   // 한 번에 1000개 조회 가능
        int totalCount = 0;     // 저장해야 함
        int totalPages = 1;     // numOfRows와 totalCount를 고려하여 페이지 넘김

        // 기존 데이터 체크용
        List<CrimeRoadEntity> oldList = cr.findAll();
        // 삭제 비교 위한 저장소
        Set<String> deleteCheck = new HashSet<>();
        // criCount
        Set<String> count = new HashSet<>();

        try {
            // totalCount을 찾아라 하나둘셋 으아악
            for (int page = 1; page <= totalPages; page++) {

                // 서비스키를 주소상에 포함
                String uri = crimeRoadUrl
                        + "/getSexualAbuseNoticeAddrList"
                        + "?serviceKey=" + serviceKey
                        + "&pageNo=" + page
                        + "&numOfRows=" + numOfRows
                        + "&type=json";

                // 요청할 API 주소 넣어주기
                Map<String, Object> response = webClient.get()
                        .uri(uri)
                        .retrieve()
                        .bodyToMono(Map.class)
                        .block();
                System.out.println(response);

                // 열어
                Map<String, Object> responseInner = (Map<String, Object>) response.get("response");
                Map<String, Object> body = (Map<String, Object>) responseInner.get("body");

                // 일단 한 페이지만 가져와서 totalCount 찾아 저장 + totalPages 계산해서 조정 저장
                if (page == 1) {
                    totalCount = (int) body.get("totalCount");
                    totalPages = (totalCount + numOfRows - 1) / numOfRows;
                    System.out.println("totalCount: "+totalCount);
                }

                // 더 열어
                Map<String, Object> items = (Map<String, Object>) body.get("items");
                List<Map<String, Object>> itemList = (List<Map<String, Object>>) items.get("item");

                // 저장
                for (Map<String, Object> item : itemList) {

                    // 서울만
                    String ctpvNm = (String) item.get("ctpvNm");
                    if(ctpvNm==null||!ctpvNm.contains("서울")) continue;

                    // 중복 제거
                    String roadName = (String) item.get("roadNm");
                    String sggNm = (String) item.get("sggNm");
                    String it = sggNm + "_" + roadName;

                    // 삭제 비교 위한 저장
                    deleteCheck.add(it);

                    // cri_count 추가
                    if(!count.add(it)){ // Set은 중복이면 저장x false 반환
                        Optional<CrimeRoadEntity> lists = cr.findByCriSggAndCriRoad(sggNm, ctpvNm);
                        lists.ifPresent( list -> list.setCriCount(list.getCriCount()+1));
                        continue;
                    }

                    String fullAdr  = "서울특별시 " + sggNm + " " + roadName;
                    String roadType = getRoadSuffix(roadName);
                    int zipCode = Integer.parseInt((String) item.get("roadNmZip"));

                    // DB에 있나요
                    Optional<CrimeRoadEntity> exists = cr.findByCriSggAndCriRoad(sggNm, ctpvNm);
                    if(exists.isPresent()){
                        // 있으면 업데이트
                        CrimeRoadEntity exist = exists.get();
                        exist.setCriType(roadType);
                        exist.setCriZip(zipCode);
                        exist.setCriCount(exist.getCriCount() + 1); // 중복
                            // 좌표가 없을 때만!! 지오코딩 (지오코딩 횟수 줄이기 위함)
                            if (exist.getCriLat() == null) {
                                double[] coords = gs.getCoordsKakao(fullAdr);
                                if (coords != null) {
                                    exist.setCriLat(coords[0]);
                                    exist.setCriLng(coords[1]);
                                }
                            }
                    } else {
                        // 없을 때만!! 지오코딩
                        double[] coords = gs.getCoordsKakao(fullAdr);
                        if (coords == null) {
                            System.out.println("좌표 저장 실패: " + fullAdr);
                            continue;
                        }
                        cr.save(CrimeRoadEntity.builder()
                                .criZip(zipCode)
                                .criSgg(sggNm)
                                .criRoad(roadName)
                                .criType(roadType)
                                .criLat(coords[0])
                                .criLng(coords[1])
                                .criCount(1) // 기본: 1
                                .build());
                    }
                }
            }
            // 업데이트된 데이터에 없는 기존 데이터 삭제
            for (CrimeRoadEntity db : oldList) {
                String dbNameAdd = db.getCriSgg().trim() + "_" + db.getCriRoad().trim();
                if (!deleteCheck.contains(dbNameAdd)) {
                    System.out.println("중복/사라진 데이터 삭제: "+db + " " + dbNameAdd);
                    cr.delete(db);
                }
            }
            return true;
        }catch(Exception e){System.out.println("위험도로명 저장 실패: "+e);return false;}
    }

    public static String getRoadSuffix(String roadName) {
        if (roadName == null || roadName.isBlank()) return "";

        if (roadName.endsWith("대로")) return "대로";
        if (roadName.endsWith("로")) return "로";
        if (roadName.endsWith("길")) return "길";
        if (roadName.endsWith("동")) return "동";
        if (roadName.endsWith("가")) return "가";

        return "";
    }
}
