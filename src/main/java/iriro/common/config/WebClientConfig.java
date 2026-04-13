package iriro.common.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.JdkClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import io.netty.channel.ChannelOption;
import reactor.netty.http.HttpProtocol;
import reactor.netty.http.client.HttpClient;

import java.net.http.HttpClient.Redirect;
import java.net.http.HttpClient.Version;
import java.time.Duration;

@Configuration
public class WebClientConfig {

    // 브라우저나 일반 클라이언트로 보이게 하기 위한 설정(User-Agent)
    private static final String BROWSER_LIKE_USER_AGENT =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36";

    // 기본 User-Agent = 신분표
    private static final String DEFAULT_USER_AGENT =
            "Mozilla/5.0 (compatible; IRIRO/1.0; +https://localhost) Spring WebClient";

    @Bean
    public WebClient webClient(){
        return WebClient.builder()
                .defaultHeader("User-Agent", DEFAULT_USER_AGENT)
                .build();
    }

    // -reactor netty 기반 webclient, 옵션
    // -HTTP11 -> HTTP/1.1만 사용하게 HTTP/2가 문제일 수도 있기 때문에
    @Bean
    public WebClient redirectWebClient() {
        HttpClient httpClient = HttpClient.create()
                .protocol(HttpProtocol.HTTP11)
                .followRedirect(true) // 301,302 리다이렉트 응답시 자동으로 따라감
                .responseTimeout(Duration.ofSeconds(15)) // 타임아웃 시간 설정
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 15_000); // 서버 조차 연결이 안될 경우 타임아웃 시간

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .defaultHeader(HttpHeaders.USER_AGENT, BROWSER_LIKE_USER_AGENT)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE + ", */*;q=0.8")
                .build();
    }

    // Netty에서 안될 경우 JDK 기본 Httpclient 사용
    @Bean
    @Qualifier("publicDataWebClient")
    public WebClient publicDataWebClient() {
        java.net.http.HttpClient jdk = java.net.http.HttpClient.newBuilder()
                .version(Version.HTTP_1_1)
                .connectTimeout(Duration.ofSeconds(20))
                .followRedirects(Redirect.NORMAL)
                .build();

        return WebClient.builder()
                .clientConnector(new JdkClientHttpConnector(jdk))
                .defaultHeader(HttpHeaders.USER_AGENT, BROWSER_LIKE_USER_AGENT)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE + ", */*;q=0.8")
                .build();
    }
}
