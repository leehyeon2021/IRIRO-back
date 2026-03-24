package iriro.publicData.service;

import iriro.publicData.entity.CrimeRoadEntity;
import iriro.publicData.repository.CrimeRoadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service @RequiredArgsConstructor
public class CrimeRoadFetchService{

    @Value("${api.pub.service-key}")
    private String serviceKey;

    @Value("${api.pub.crime-road.url}")
    private String crimeRoadUrl;

    private final WebClient webClient = WebClient.builder().build();
    private final CrimeRoadRepository cr;

    // (공공데이터 수집) 범죄자도로명
    public Map<String , Object> fetchCrimeRoad(){
        // * 일단 써놓음
        int numOfRows = 1000; // 한 번에 1000개 조회 가능
        int totalCount = 0; // 저장해야 함
        int totalPages = 1; // 하나씩 올라감

        // totalCount을 찾아라 하나둘셋 으아악
        for(int page=1;page <= totalPages; page++){

            // 서비스키를 주소상에 포함
            String uri = crimeRoadUrl
                    + "/getSexualAbuseNoticeAddrList"
                    + "?serviceKey="+serviceKey
                    + "&pageNo="+page
                    + "&numOfRows="+numOfRows
                    + "&type=json";

            // 요청할 API 주소 넣어주기
            Map<String,Object> response = webClient.get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
            System.out.println(response);

            // 일단 한 페이지만 가져와서 totalCount 찾아 저장 + totalPages 계산해서 조정 저장
            if(page==1){
                Map<String, Object> responseInner = (Map<String, Object>) response.get("response");
                Map<String,Object> body = (Map<String,Object>) responseInner.get("body");
                totalCount = (int) body.get("totalCount");
                totalPages = (totalCount + numOfRows - 1) / numOfRows;
            }

            // 저장
            Map<String, Object> responseInner = (Map<String, Object>) response.get("response");
            Map<String,Object> body = (Map<String,Object>) responseInner.get("body");
            Map<String,Object> items = (Map<String,Object>) body.get("items");
            List<Map<String,Object>> itemList = (List<Map<String,Object>>) items.get("item");

            for (Map<String, Object> item : itemList) {
                String ctpvNm = (String) item.get("ctpvNm");
                if (!ctpvNm.contains("서울")) continue;
                CrimeRoadEntity entity = CrimeRoadEntity.builder()
                        .criZip(Integer.parseInt((String) item.get("roadNmZip")))
                        .criSgg((String) item.get("sggNm"))
                        .criRoad((String) item.get("roadNm"))
                        .build();
                cr.save(entity);
            }
        }
        // Map<String, Object> result = new HashMap<>();로 return하기
        Map<String, Object> result = new HashMap<>();
        result.put("totalCount", totalCount);
        result.put("totalPages", totalPages);
        result.put("savedCount", cr.count());
        result.put("message", "저장 완료");

        // 반환
        return result;
    }


}
