package iriro.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

/* 쓰는 법
private final GeocodingService 지오;
double[] 코드 = 지오.getCoords("서울특별시 강남구 어쩌고");
*/

@Service
@RequiredArgsConstructor
public class GeocodingService {

    @Value("${tmap.app-key}")
    private String tmapServiceKey;

    @Value("${tmap.geocoding.url}")
    private String tmapGeocodingUrl;

    private final WebClient webClient = WebClient.builder().build();

    // 주소 → [위도, 경도] 반환. 실패 시 null 반환
    public double[] getCoords(String address) {
        try {
            String uri = tmapGeocodingUrl
                    + "?version=1"
                    + "&fullAddr=" + URLEncoder.encode(address, "UTF-8")
                    + "&appKey=" + tmapServiceKey;

            Map<String, Object> response = webClient.get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            Map<String, Object> coordinateInfo = (Map<String, Object>) response.get("coordinateInfo");
            List<Map<String, Object>> coordinate = (List<Map<String, Object>>) coordinateInfo.get("coordinate");

            if (coordinate == null || coordinate.isEmpty()) return null;

            String lat = (String) coordinate.get(0).get("lat");
            String lon = (String) coordinate.get(0).get("lon");

            if (lat == null || lat.equals("0") || lat.isEmpty()) return null;

            return new double[]{Double.parseDouble(lat), Double.parseDouble(lon)};

        } catch (Exception e) {
            System.out.println("지오코딩 실패: " + address + " / " + e.getMessage());
            return null;
        }
    }
}