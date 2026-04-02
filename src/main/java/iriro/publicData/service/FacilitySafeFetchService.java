package iriro.publicData.service;

import iriro.common.service.GeocodingService;
import iriro.publicData.entity.FacilitySafeEntity;
import iriro.publicData.repository.FacilitySafeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;

@Service
@RequiredArgsConstructor
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
    @Transactional
    public boolean fetchSafeHouse(){
        int numOfRows = 500;
        int totalCount = 0;
        int totalPages = 1;     // numOfRows와 totalCount를 고려하여 페이지 넘김

        // 기존 데이터
        List<FacilitySafeEntity> oldList = fr.findByFacType("안심지킴이집");
        // 삭제 비교 위한 저장소
        Set<String> deleteCheck = new HashSet<>();

        try{ // 전체 개수 찾기
            for(int page=1;page<=totalPages;page++){
                int pageNo = page;
                // 서비스키 주소에 넣기
                // 요청할 API 주소 넣기 webClient
                Map<String,Object> response = webClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .path(safeHouseUrl)
                                .queryParam("serviceKey",pubServiceKey)
                                .queryParam("pageNo",pageNo)
                                .queryParam("numOfRows",numOfRows)
                                .queryParam("type","JSON")
                                .queryParam("ctprvnNm","서울특별시")
                                .build()
                        )
                        .retrieve()
                        .bodyToMono(new ParameterizedTypeReference<Map<String,Object>>(){})
                        .block();

                // 열기
                Map<String,Object> responseInner = (Map<String, Object>) response.get("response");
                Map<String,Object> body = (Map<String, Object>) responseInner.get("body");

                // 첫page에서 개수 찾아와 저장
                if(page==1){
                    totalCount = Integer.parseInt((String)body.get("totalCount"));
                    System.out.println("totalCount: "+totalCount);
                    totalPages = (totalCount+numOfRows-1)/numOfRows;
                }

                // item 저장(List)
                List<Map<String,Object>> itemList = (List<Map<String, Object>>) body.get("items");

                // 저장
                for(Map<String,Object>item:itemList){

                    // 수정 저장 용도
                    String facName = ((String) item.get("storNm"));
                    String facAdd = ((String) item.get("rdnmadr"));
                    String facSgg = (String) item.get("signguNm");
                    String facUse = (String) item.get("useYn");
                    String facTel = (String) item.get("phoneNumber");
                    double lat = Double.parseDouble((String) item.get("latitude"));
                    double lng = Double.parseDouble((String) item.get("longitude"));

                    // 비교 위한 저장
                    deleteCheck.add(facName+facAdd);

                    // DB 확인
                    Optional<FacilitySafeEntity> exists = fr.findByFacNameAndFacAdd( facName, facAdd );
                    if(exists.isPresent()){
                        FacilitySafeEntity exist = exists.get();
                        exist.setFacUse(facUse);
                        exist.setFacTel(facTel);
                        exist.setFacSgg(facSgg);
                        exist.setFacLat(lat);
                        exist.setFacLng(lng); // @Transactional 있으니 save 생략 가능
                    }else {
                        fr.save(FacilitySafeEntity.builder()
                                .facType("안심지킴이집")
                                .facSgg(facSgg)
                                .facName(facName)
                                .facAdd(facAdd)
                                .facLat(lat)
                                .facLng(lng)
                                .facUse(facUse)
                                .facTel(facTel)
                                .build());
                    }
                }
            }
            // 업데이트된 데이터에 없는 기존 데이터 삭제
            for(FacilitySafeEntity db : oldList){
                String dbNameAdd = db.getFacName().trim() + db.getFacAdd().trim();
                if(!deleteCheck.contains(dbNameAdd)){
                    System.out.println("중복/사라진 데이터 삭제: "+db + " " + dbNameAdd);
                    fr.delete(db);
                }
            }
           return true;
        }catch(Exception e){System.out.println("안심지킴이집 저장 실패: "+e);return false;}
    }

    // 경찰서 저장 (3000여 개 중 서울은 400여 개)
    @Transactional
    public boolean fetchPoliceStation() {
        int numOfRows = 100;
        int totalCount = 0;
        int totalPages = 1;

        // DB 저장된 경찰서 데이터 가져오기 (비교용)
        List<FacilitySafeEntity> oldList = fr.findByFacType("경찰서");
        // 삭제 비교 위한 저장소
        Set<String> deleteCheck = new HashSet<>();

        try {
            for (int page = 1; page <= totalPages; page++) {
                int pageNo = page;

                Map<String, Object> response = webClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .path(policeUrl)
                                .queryParam("serviceKey", adminServiceKey)
                                .queryParam("pageNo", pageNo)
                                .queryParam("numOfRows",numOfRows)
                                .queryParam("returnType","json")
                                .build())
                        .retrieve()
                        .bodyToMono(new ParameterizedTypeReference<Map<String,Object>>() {})
                        .block();

                // 열기
                Map<String, Object> body = (Map<String, Object>) response.get("body");
                // totalCount 가져오기
                if (page==1){
                    totalCount=Integer.parseInt(String.valueOf(body.get("totalCount")));
                    System.out.println("totalCount: "+totalCount);
                    totalPages=(totalCount+numOfRows-1)/numOfRows;
                }

                // 더 열기
                Map<String, Object> items = (Map<String, Object>) body.get("items");
                List<Map<String, Object>> itemList = (List<Map<String, Object>>) items.get("item");

                // 저장
                for (Map<String,Object> item : itemList){

                    String facAdd = (String) item.get("rn_adres");
                    String facName = (String) item.get("fclty_nm");
                    String facSgg = (String) item.get("polcsttn");

                    // 서울만 저장
                    if (facAdd == null || !facAdd.contains("서울")){
                        continue;
                    }

                    // 비교 위한 저장소 저장 (이름, 주소)
                    deleteCheck.add(facName.trim() + facAdd.trim());

                    // 전화번호 (-) null로 교체
                    String facTel = (String) item.get("telno");
                    if(facTel.equals("-")||facTel.equals("null")||facTel.isEmpty()){
                        facTel = null;
                    }

                    // 좌표
                    String xStr = (String) item.get("x");
                    String yStr = (String) item.get("y");

                    // x좌표가 0 이면 지오코딩
                    double lat;  double lng;
                    if (xStr == null || xStr.equals("0")) {
                        double[] coords = gs.getCoordsKakao(facAdd);
                        if(coords == null)continue;
                        lat = coords[0];
                        lng = coords[1];
                    } else {
                        lat = Double.parseDouble(yStr);
                        lng = Double.parseDouble(xStr);
                    }

                    // DB 확인
                    Optional<FacilitySafeEntity> exists = fr.findByFacNameAndFacAdd(facName , facAdd);
                    if(exists.isPresent()){
                        FacilitySafeEntity exist = exists.get();
                        exist.setFacTel(facTel);
                        exist.setFacSgg(facSgg);
                        exist.setFacLat(lat);
                        exist.setFacLng(lng);
                    }else {
                        fr.save(FacilitySafeEntity.builder()
                                .facType("경찰서")
                                .facSgg(facSgg)
                                .facName(facName)
                                .facAdd(facAdd)
                                .facLat(lat)
                                .facLng(lng)
                                .facTel(facTel)
                                .build());
                    }
                }
            }
            // 업데이트된 데이터에 없는 기존 데이터 삭제
            for(FacilitySafeEntity db : oldList){
                String dbNameAdd = db.getFacName().trim() + db.getFacAdd().trim();
                if(!deleteCheck.contains(dbNameAdd)){
                    System.out.println("중복/사라진 데이터 삭제: "+db + " " + dbNameAdd);
                    fr.delete(db);
                }
            }
            return true;
        }catch(Exception e){System.out.println("경찰서 저장 실패: " + e);return false;}
    }

    // 안전시설물(보안등,CCTV,안전벨) 저장 (8100여 개)
    @Transactional
    public boolean fetchSafeFac(){
        int numOfRows = 500;
        int totalCount = 0;
        int totalPages = 1;

        // FACI_CODE(시설코드) -> fac_type 매핑
        Map<String,Object> typeMapping = new HashMap<>();
        typeMapping.put("301","안전벨");
        typeMapping.put("302","CCTV");
        typeMapping.put("305","보안등");

        // DB 저장된 시설물 데이터 가져오기 (비교용)
        List<FacilitySafeEntity> oldList = fr.findByFacTypeIn(Arrays.asList("안전벨", "CCTV", "보안등"));
        // 삭제 비교 위한 저장소
        Set<String> deleteCheck = new HashSet<>();

        try{
            for(int page = 1 ; page <=totalPages; page++){
                // 시작 인덱스
                int startIndex = (page-1)*numOfRows+1;
                // 끝 인덱스
                int endIndex = page*numOfRows;

                // 주소에 넣기
                // 요청 API
                Map<String,Object> response = webClient.get()
                        .uri(safeFacUrl+"/{key}/{type}/{service}/{start}/{end}",
                                safeFacUrl, "json", "tbSafeReturnItem", startIndex, endIndex)
                        .retrieve()
                        .bodyToMono(new ParameterizedTypeReference<Map<String,Object>>() {})
                        .block();

                // 열기
                Map<String,Object> tbSafeReturnItem = (Map<String,Object>) response.get("tbSafeReturnItem");

                // totalCount('list_total_count')
                if(page==1){
                    totalCount = (int) tbSafeReturnItem.get("list_total_count");
                    System.out.println("totalCount: "+totalCount);
                    totalPages=(totalCount+numOfRows-1)/numOfRows;
                }

                // 더 열기 ('row')
                List<Map<String,Object>> itemList = (List<Map<String, Object>>) tbSafeReturnItem.get("row");

                // 저장
                for(Map<String,Object> item : itemList){

                    // 시설 코드 꺼내기
                    String faciCode = (String) item.get("FACI_CODE");
                    if(!typeMapping.containsKey(faciCode))continue;
                    String facType = (String) typeMapping.get(faciCode);
                    String facName = ((String) item.get("ASG_NM")).trim();
                    String facAdd = ((String) item.get("DELOC")).trim();
                    String facSgg = (String) item.get("SGG_NAME");
                    String facTel = (String) item.get("INST_TELNO");

                    // 비교 위한 저장소 저장
                    deleteCheck.add(facName.trim()+facAdd.trim());

                    // 좌표 ("POINT_WKT":"POINT (위도 경도)") 가공
                    String pointWkt = (String) item.get("POINT_WKT");
                    if(pointWkt == null || pointWkt.isEmpty()) continue;
                        // 숫자, 마침표, 공백 빼고 모두 제거([이거중하나]^제외0-9모든숫자.마침표랑공백)
                    String cleaned = pointWkt.replaceAll("[^0-9. ]", "").trim();
                        // 하나 이상의 공백을 기준으로 분리 (\\s공백문자,+한개이상)
                    String[] coords = cleaned.split("\\s+");
                    double lng = Double.parseDouble(coords[0]);
                    double lat = Double.parseDouble(coords[1]);

                    // 설치 대수는 빈값 체크하고 .parseInt
                    String instlCnt = (String) item.get("INSTL_CNT");
                    Integer instlcut = instlCnt!=null&&!instlCnt.isEmpty() ? Integer.parseInt(instlCnt) : null;

                    // DB 확인
                    Optional<FacilitySafeEntity> exists = fr.findByFacNameAndFacAdd(facName,facAdd);
                    if(exists.isPresent()){
                        FacilitySafeEntity exist = exists.get();
                        exist.setFacTel(facTel);
                        exist.setFacSgg(facSgg);
                        exist.setFacLng(lng);
                        exist.setFacLat(lat);
                    }else {
                        fr.save(FacilitySafeEntity.builder()
                                .facType(facType)
                                .facSgg(facSgg)
                                .facName(facName)
                                .facAdd(facAdd)
                                .facLat(lat)
                                .facLng(lng)
                                .facCount(instlcut)
                                .facTel(facTel)
                                .build());
                    }
                }
            }
            // 업데이트된 데이터에 없는 기존 데이터 삭제
            for(FacilitySafeEntity db : oldList){
                String dbNameAdd = db.getFacName().trim()+db.getFacAdd().trim();
                if(!deleteCheck.contains(dbNameAdd)){
                    System.out.println("중복/사라진 데이터 삭제: "+db + " " + dbNameAdd);
                    fr.delete(db);
                }
            }
            return true;
        }catch(Exception e){System.out.println("안전시설물 저장 실패: "+e);return false;}
    }
}
