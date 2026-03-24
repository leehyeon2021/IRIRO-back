package iriro.publicData.service;

import iriro.publicData.repository.CrimeRoadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;
import java.util.Objects;

@Service @RequiredArgsConstructor
public class CrimeRoadFetchService{

    @Value("${api.pub.service-key}")
    private String serviceKey;

    @Value("${api.pub.crime-road.url}")
    private String crimeRoadUrl;

    private final WebClient webClient = WebClient.builder().build();
    private final CrimeRoadRepository cr;

    // (공공데이터 수집) 범죄자도로명



}
