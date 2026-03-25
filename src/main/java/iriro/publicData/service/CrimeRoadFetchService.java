package iriro.publicData.service;

import iriro.common.service.GeocodingService;
import iriro.publicData.entity.CrimeRoadEntity;
import iriro.publicData.repository.CrimeRoadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service @RequiredArgsConstructor
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
        // * 일단 써놓음
        int numOfRows = 1000;   // 한 번에 1000개 조회 가능
        int totalCount = 0;     // 저장해야 함
        int totalPages = 1;     // numOfRows와 totalCount를 고려하여 페이지 넘김

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
                }

                // 저장
                Map<String, Object> items = (Map<String, Object>) body.get("items");
                List<Map<String, Object>> itemList = (List<Map<String, Object>>) items.get("item");

                for (Map<String, Object> item : itemList) {
                    String ctpvNm = (String) item.get("ctpvNm");
                    if(ctpvNm==null||!ctpvNm.contains("서울")) continue;

                    // 도로명 분리(criRoad , criType)
                    String roadName = (String) item.get("roadNm");
                    String roadType = getRoadSuffix(roadName);
                    String fullAdr = "서울특별시 "+item.get("ssgNm")+" "+roadName;
                    System.out.println(fullAdr);
                    double[] coords = gs.getCoords(fullAdr);

                    cr.save(CrimeRoadEntity.builder()
                            .criZip(Integer.parseInt((String) item.get("roadNmZip")))
                            .criSgg((String) item.get("sggNm"))
                            .criRoad()
                            .criType(roadType)
                            .build());
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

        return "";
    }
    /* 마지막 글자만 따오는 함수
    System.out.println(getRoadSuffix("테헤란대로")); // 대로
    System.out.println(getRoadSuffix("세종로"));     // 로
    System.out.println(getRoadSuffix("충장길"));     // 길  */

}
