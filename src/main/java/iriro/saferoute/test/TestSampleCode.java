package iriro.saferoute.test;

import iriro.saferoute.dto.RiskPointDto;
import iriro.saferoute.dto.RouteRequestDto;
import iriro.saferoute.dto.SafetyFacPointDto;

import java.math.BigDecimal;
import java.util.List;

// 가상 DB
public class TestSampleCode {

    // 1. 테스트요청: 출발지와 목적지 위/경도
    public static final RouteRequestDto testRouteRequest =
            new RouteRequestDto( 37.3895300, 126.959400, 37.3976478, 126.9312600);


    // 2. 가상 위험 테이블의 위/경도를 뽑아 온 값 ( 실제로는 위험테이블에서 조회해와서 리스트화 시켜야함 )
    public static final List<RiskPointDto> dangerRoutePoints = List.of(
            // ===== 경로 근처 위험점 =====
            new RiskPointDto("길", 37.389413796907185, 126.95896112442831, 1),
            new RiskPointDto("길", 37.39098303954915, 126.95785840291721, 2),
            new RiskPointDto("로", 37.390335859209024, 126.95599747917913, 1),
            new RiskPointDto("길", 37.389222039983025, 126.95265892511556, 3),
            new RiskPointDto("대로", 37.38860541319803, 126.95088410379881, 1),

            // ===== bbox로 걸러져야 할 먼 위험점 =====
            new RiskPointDto("길", 37.4005000, 126.9705000, 1),
            new RiskPointDto("로", 37.4050000, 126.9650000, 2),
            new RiskPointDto("대로", 37.3820000, 126.9800000, 1),
            new RiskPointDto("길", 37.4100000, 126.9400000, 4),
            new RiskPointDto("로", 37.3700000, 126.9300000, 1)
    );

    // 3. 가상의 안전한 시설의 위치 테이블
    public static final List<SafetyFacPointDto> safeRoutePoint = List.of(
            // ===== 경로 근처 안전시설 =====
            new SafetyFacPointDto("보안등", 2, 37.3894200, 126.9590500),
            new SafetyFacPointDto("CCTV", 1, 37.3895200, 126.9588700),
            new SafetyFacPointDto("안전벨", 1, 37.3893000, 126.9591200),
            new SafetyFacPointDto("경찰서", 1, 37.3896500, 126.9587000),

            new SafetyFacPointDto("보안등", 2, 37.3909500, 126.9579800),
            new SafetyFacPointDto("CCTV", 1, 37.3910200, 126.9577600),
            new SafetyFacPointDto("안심지킴이집", 1, 37.3908600, 126.9580500),
            new SafetyFacPointDto("경찰서", 1, 37.3907000, 126.9579000),

            new SafetyFacPointDto("보안등", 2, 37.3903300, 126.9560500),
            new SafetyFacPointDto("CCTV", 2, 37.3902500, 126.9559000),
            new SafetyFacPointDto("안심지킴이집", 1, 37.3900100, 126.9550100),
            new SafetyFacPointDto("안전벨", 1, 37.3901200, 126.9552500),

            new SafetyFacPointDto("보안등", 2, 37.3892200, 126.9525600),
            new SafetyFacPointDto("CCTV", 1, 37.3890500, 126.9524200),
            new SafetyFacPointDto("안전벨", 1, 37.3890800, 126.9522600),
            new SafetyFacPointDto("안심지킴이집", 1, 37.3890000, 126.9521500),

            new SafetyFacPointDto("보안등", 2, 37.3886300, 126.9507600),
            new SafetyFacPointDto("CCTV", 1, 37.3887200, 126.9509200),
            new SafetyFacPointDto("경찰서", 1, 37.3896800, 126.9494000),
            new SafetyFacPointDto("CCTV", 1, 37.3891000, 126.9502000),

            new SafetyFacPointDto("보안등", 2, 37.3922400, 126.9445300),
            new SafetyFacPointDto("CCTV", 1, 37.3932700, 126.9405000),
            new SafetyFacPointDto("안심지킴이집", 1, 37.3928000, 126.9425000),
            new SafetyFacPointDto("경찰서", 1, 37.3935000, 126.9418000),

            // ===== bbox에서 걸러져야 할 먼 안전시설 =====
            new SafetyFacPointDto("보안등", 2, 37.4015000, 126.9715000),
            new SafetyFacPointDto("CCTV", 1, 37.4060000, 126.9660000),
            new SafetyFacPointDto("안심지킴이집", 1, 37.3815000, 126.9815000),
            new SafetyFacPointDto("경찰서", 1, 37.4110000, 126.9410000),
            new SafetyFacPointDto("안전벨", 1, 37.3695000, 126.9295000)
    );
}
