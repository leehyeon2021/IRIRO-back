package iriro.publicData.service;

import iriro.common.service.GeocodingService;
import iriro.publicData.entity.FacilitySafeEntity;
import iriro.publicData.repository.FacilitySafeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service @RequiredArgsConstructor @Transactional
public class FacilitySafeFetchService {

    // 안심지킴이집
    @Value("${api.pub.service-key}") private String pubServiceKey;
    @Value("${api.pub.safe-house.url}") private String safeHouseUrl;

    // 경찰서(치안안전시설) - 위도경도 없는 것 있음 - 지오코딩 필요
    @Value("${api.admin.service-key}") private String adminServiceKey;
    @Value("${api.admin.police.url}") private String policeUrl;

    // 안전시설물(보안등,CCTV,안전벨)
    @Value("${api.seoul.service-key}") private String seoulServiceKey;
    @Value("${api.seoul.safe-fac.url}") private String safeFacUrl;

    private final WebClient webClient;
    private final FacilitySafeRepository fr;
    private final GeocodingService gs;

    // 안심지킴이집(List) 저장 (700여 개)
    public boolean fetchSafeHouse(){
        int numOfRows = 500;
        int totalCount = 0;
        int totalPages = 1;     // numOfRows와 totalCount를 고려하여 페이지 넘김
        try{ // 전체 개수 찾기
            for(int page=1;page<=totalPages;page++){
                // 서비스키 주소에 넣기
                String uri = safeHouseUrl
                        + "?serviceKey=" + pubServiceKey
                        + "&pageNo=" + page
                        + "&numOfRows=" + numOfRows
                        + "&type=JSON"
                        + "&ctprvnNm=서울특별시";
                // 요청할 API 주소 넣기 webClient
                Map<String,Object> response = webClient.get()
                        .uri(uri)
                        .retrieve() // 반환타입
                        .bodyToMono(Map.class)
                        .block();

                // 열어
                Map<String,Object> responseInner = (Map<String, Object>) response.get("response");
                Map<String,Object> body = (Map<String, Object>) responseInner.get("body");

                // 첫page에서 개수 찾아와 저장
                if(page==1){
                    totalCount = Integer.parseInt((String)body.get("totalCount"));
                    System.out.println("totalCount: "+totalCount);
                    totalPages = (totalCount+numOfRows-1)/numOfRows;
                }

                // item 저장(List임)
                List<Map<String,Object>> itemList = (List<Map<String, Object>>) body.get("items");
                for(Map<String,Object>item:itemList){
                    fr.save(FacilitySafeEntity.builder()
                            .facType("안심지킴이집")
                            .facSgg((String) item.get("signguNm"))
                            .facName((String) item.get("storNm"))
                            .facAdd((String) item.get("rdnmadr"))
                            .facLat(Double.parseDouble((String) item.get("latitude")))
                            .facLng(Double.parseDouble((String) item.get("longitude")))
                            .facUse((String) item.get("useYn"))
                            .facTel((String) item.get("phoneNumber"))
                            .build());
                }
            }
           return true;
        }catch(Exception e){System.out.println("안심지킴이집 저장 실패: "+e);return false;}
    }

    // 경찰서 저장 (3000여 개 중 서울은 400여 개)
    public boolean fetchPoliceStation() {
        int numOfRows = 500;
        int totalCount = 0;
        int totalPages = 1;

        try {
            for (int page = 1; page <= totalPages; page++) {

                String uri = policeUrl
                        + "?serviceKey=" + adminServiceKey
                        + "&pageNo=" + page
                        + "&numOfRows=" + numOfRows
                        + "&returnType=json";

                Map<String, Object> response = webClient.get()
                        .uri(uri)
                        .retrieve()
                        .bodyToMono(Map.class)
                        .block();

                Map<String, Object> body = (Map<String, Object>) response.get("body");

                if (page==1){
                    totalCount=Integer.parseInt(String.valueOf(body.get("totalCount")));
                    System.out.println("totalCount: "+totalCount);
                    totalPages=(totalCount+numOfRows-1)/numOfRows;
                }

                Map<String, Object> items = (Map<String, Object>) body.get("items");
                List<Map<String, Object>> itemList = (List<Map<String, Object>>) items.get("item");

                for (Map<String,Object> item : itemList){

                    // 서울만 저장
                    String rnAdres = (String) item.get("rn_adres");
                    if (rnAdres == null || !rnAdres.contains("서울")){
                        //System.out.println("문제 발생1: "+rnAdres);
                        continue;
                    }

                    String xStr = (String) item.get("x");
                    String yStr = (String) item.get("y");

                    double lat;  double lng;

                    // x="0" 이면 지오코딩 (x없음y도없음)
                    if (xStr == null || xStr.equals("0")) {
                        double[] coords = gs.getCoords(rnAdres);
                        if(coords == null)continue;
                        lat = coords[0];
                        lng = coords[1];
                    } else {
                        lat = Double.parseDouble(yStr);
                        lng = Double.parseDouble(xStr);
                    }

                    fr.save(FacilitySafeEntity.builder()
                            .facType("경찰서")
                            .facSgg((String) item.get("polcsttn"))
                            .facName((String) item.get("fclty_nm"))
                            .facAdd(rnAdres)
                            .facLat(lat)
                            .facLng(lng)
                            .facTel((String) item.get("telno"))
                            .build());
                }
            }
            return true;
        } catch (Exception e) {
            System.out.println("경찰서 저장 실패: " + e);
            return false;
        }
    }

    // 안전시설물(보안등,CCTV,안전벨) 저장 (8100여 개)
    public boolean fetchSafeFac(){
        int numOfRows = 500;
        int totalCount = 0;
        int totalPages = 1;

        // FACI_CODE(시설코드) -> fac_type 매핑
        Map<String,Object> typeMapping = new HashMap<>();
        typeMapping.put("301","안전벨");
        typeMapping.put("302","CCTV");
        typeMapping.put("305","보안등");

        try{
            for(int page = 1 ; page <=totalPages; page++){
                // 시작 인덱스
                int startIndex = (page-1)*numOfRows+1;
                // 끝 인덱스
                int endIndex = page*numOfRows;

                // 주소에 넣기
                String uri = safeFacUrl
                        + "/" + seoulServiceKey
                        + "/json/tbSafeReturnItem/"
                        + startIndex + "/" + endIndex;

                // 요청 API
                Map<String,Object> response = webClient.get()
                        .uri(uri)
                        .retrieve()
                        .bodyToMono(Map.class)
                        .block();

                // 열기
                Map<String,Object> tbSafeReturnItem = (Map<String,Object>) response.get("tbSafeReturnItem");

                // totalCount(list_total_count)
                if(page==1){
                    totalCount = (int) tbSafeReturnItem.get("list_total_count");
                    System.out.println("totalCount: "+totalCount);
                    totalPages=(totalCount+numOfRows-1)/numOfRows;
                }

                // row 저장(List임)
                List<Map<String,Object>> itemList = (List<Map<String, Object>>) tbSafeReturnItem.get("row");
                for(Map<String,Object> item : itemList){
                    // 시설 코드 꺼내기
                    String faciCode = (String) item.get("FACI_CODE");
                    if(!typeMapping.containsKey(faciCode))continue;
                    // 좌표("POINT_WKT":"POINT (126.968590563668 37.5793826677127)") 나누어 넣기
                    String pointWkt = (String) item.get("POINT_WKT");
                    if(pointWkt == null || pointWkt.isEmpty()) continue;
                        // 숫자, 마침표, 공백 빼고 모두 제거([이거중하나]^제외0-9모든숫자.마침표랑공백)
                    String cleaned = pointWkt.replaceAll("[^0-9. ]", "").trim();
                        // 하나 이상의 공백을 기준으로 분리 (\\s공백문자,+한개이상)
                    String[] coords = cleaned.split("\\s+");
                    double lng = Double.parseDouble(coords[0]);
                    double lat = Double.parseDouble(coords[1]);

                    // 설치 대수는 빈값 체크하고 .parseInt해야 함
                    String instlCnt = (String) item.get("INSTL_CNT");
                    Integer instlcut = instlCnt!=null&&!instlCnt.isEmpty() ? Integer.parseInt(instlCnt) : null;

                    // 드디어 저장
                    fr.save(FacilitySafeEntity.builder()
                            .facType((String)typeMapping.get(faciCode))          // 안전벨/CCTV/보안등
                            .facSgg((String)item.get("SGG_NAME"))
                            .facName((String)item.get("ASG_NM"))
                            .facAdd((String)item.get("DELOC"))
                            .facLat(lat)
                            .facLng(lng)
                            .facCount(instlcut)
                            .facTel((String)item.get("INST_TELNO"))
                            .build());
                }
            }
            return true;
        }catch(Exception e){System.out.println("안전시설물 저장 실패: "+e);return false;}
    }
}
