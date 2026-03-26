package iriro.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

/* 쓰는 법
private final GeocodingService gs;
double[] coords = gs.getCoords("서울특별시 강남구 어쩌고");
*/

@Service
@RequiredArgsConstructor
public class GeocodingService {

    @Value("${tmap.app-key}")
    private String tmapServiceKey;

    @Value("${tmap.geocoding.url}")
    private String tmapGeocodingUrl;

    private final WebClient webClient;

    // 주소 -> 위도, 경도 반환. 실패 시 null 반환
    public double[] getCoords(String address, String addressFlag) {
        try {
            // 1. URL 생성
            String uri = tmapGeocodingUrl
                    + "?version=1"
                    + "&fullAddr=" + URLEncoder.encode(address, "UTF-8")
                    + "&appKey=" + tmapServiceKey
                    + "&addressFlag=" + addressFlag;

            // 2. 응답
            Map<String, Object> response = webClient.get()
                    .uri(java.net.URI.create(uri))
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response == null) return null;

            // 추출
            Map<String, Object> coordinateInfo = (Map<String, Object>) response.get("coordinateInfo");
            if (coordinateInfo == null) return null;
            List<Map<String, Object>> coordinateList = (List<Map<String, Object>>) coordinateInfo.get("coordinate");
            if (coordinateList == null || coordinateList.isEmpty()) return null;
            Map<String, Object> firstResult = coordinateList.get(0);

            // 좌표 넣기
            String lat = String.valueOf(firstResult.get("lat"));
            String lon = String.valueOf(firstResult.get("lon"));

            // lat/newLat 둘 다 받을 수 있도록
            if (lat.equals("null") || lat.equals("0")){
                lat = String.valueOf(firstResult.get("newLat"));
                lon = String.valueOf(firstResult.get("newLon"));
            }
            if(lat.equals("null")||lat.isEmpty())return null;

            // 숫자 변환 후 반환
            return new double[]{Double.parseDouble(lat), Double.parseDouble(lon)};

        } catch (Exception e) {
            System.out.println("지오코딩 실패: " + address + " / " + e.getMessage());
            return null;
        }
    }
}