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
            new RiskPointDto("길", new BigDecimal("37.389413796907185"), new BigDecimal("126.95896112442831"), 1, 1),
            new RiskPointDto("길", new BigDecimal("37.39098303954915"), new BigDecimal("126.95785840291721"), 2, 2),
            new RiskPointDto("로", new BigDecimal("37.390335859209024"), new BigDecimal("126.95599747917913"), 1, 3),
            new RiskPointDto("길", new BigDecimal("37.389222039983025"), new BigDecimal("126.95265892511556"), 3, 4),
            new RiskPointDto("대로", new BigDecimal("37.38860541319803"), new BigDecimal("126.95088410379881"), 1, 5),

            // ===== bbox로 걸러져야 할 먼 위험점 =====
            new RiskPointDto("길", new BigDecimal("37.4005000"), new BigDecimal("126.9705000"), 1, 6),
            new RiskPointDto("로", new BigDecimal("37.4050000"), new BigDecimal("126.9650000"), 2, 7),
            new RiskPointDto("대로", new BigDecimal("37.3820000"), new BigDecimal("126.9800000"), 1, 8),
            new RiskPointDto("길", new BigDecimal("37.4100000"), new BigDecimal("126.9400000"), 4, 9),
            new RiskPointDto("로", new BigDecimal("37.3700000"), new BigDecimal("126.9300000"), 1, 10)
    );

    // 3. 가상의 안전한 시설의 위치 테이블
    public static final List<SafetyFacPointDto> safeRoutePoint = List.of(
            new SafetyFacPointDto("보안등", new BigDecimal("37.3893500"), new BigDecimal("126.9593000")),
            new SafetyFacPointDto("CCTV", new BigDecimal("37.3894200"), new BigDecimal("126.9590500")),
            new SafetyFacPointDto("안심지킴이집", new BigDecimal("37.3895600"), new BigDecimal("126.9588700")),
            new SafetyFacPointDto("경찰서", new BigDecimal("37.3899200"), new BigDecimal("126.9586400")),
            new SafetyFacPointDto("안전벨", new BigDecimal("37.3900900"), new BigDecimal("126.9585400")),

            new SafetyFacPointDto("보안등", new BigDecimal("37.3902500"), new BigDecimal("126.9584500")),
            new SafetyFacPointDto("CCTV", new BigDecimal("37.3904000"), new BigDecimal("126.9583600")),
            new SafetyFacPointDto("안심지킴이집", new BigDecimal("37.3905600"), new BigDecimal("126.9582700")),
            new SafetyFacPointDto("경찰서", new BigDecimal("37.3907100"), new BigDecimal("126.9581900")),
            new SafetyFacPointDto("안전벨", new BigDecimal("37.3908600"), new BigDecimal("126.9581100")),

            new SafetyFacPointDto("보안등", new BigDecimal("37.3910000"), new BigDecimal("126.9580000")),
            new SafetyFacPointDto("CCTV", new BigDecimal("37.3909800"), new BigDecimal("126.9578600")),
            new SafetyFacPointDto("안심지킴이집", new BigDecimal("37.3909700"), new BigDecimal("126.9578100")),
            new SafetyFacPointDto("경찰서", new BigDecimal("37.3905500"), new BigDecimal("126.9566000")),
            new SafetyFacPointDto("안전벨", new BigDecimal("37.3905000"), new BigDecimal("126.9565200")),

            new SafetyFacPointDto("보안등", new BigDecimal("37.3903300"), new BigDecimal("126.9560000")),
            new SafetyFacPointDto("CCTV", new BigDecimal("37.3903000"), new BigDecimal("126.9559000")),
            new SafetyFacPointDto("안심지킴이집", new BigDecimal("37.3900100"), new BigDecimal("126.9550100")),
            new SafetyFacPointDto("경찰서", new BigDecimal("37.3900000"), new BigDecimal("126.9549700")),
            new SafetyFacPointDto("안전벨", new BigDecimal("37.3899600"), new BigDecimal("126.9548400")),

            new SafetyFacPointDto("보안등", new BigDecimal("37.3897200"), new BigDecimal("126.9541300")),
            new SafetyFacPointDto("CCTV", new BigDecimal("37.3897000"), new BigDecimal("126.9540800")),
            new SafetyFacPointDto("안심지킴이집", new BigDecimal("37.3892200"), new BigDecimal("126.9526600")),
            new SafetyFacPointDto("경찰서", new BigDecimal("37.3891000"), new BigDecimal("126.9522900")),
            new SafetyFacPointDto("안전벨", new BigDecimal("37.3890800"), new BigDecimal("126.9522600")),

            new SafetyFacPointDto("보안등", new BigDecimal("37.3890500"), new BigDecimal("126.9522500")),
            new SafetyFacPointDto("CCTV", new BigDecimal("37.3889500"), new BigDecimal("126.9519600")),
            new SafetyFacPointDto("안심지킴이집", new BigDecimal("37.3889500"), new BigDecimal("126.9518700")),
            new SafetyFacPointDto("경찰서", new BigDecimal("37.3886100"), new BigDecimal("126.9508800")),
            new SafetyFacPointDto("안전벨", new BigDecimal("37.3886300"), new BigDecimal("126.9507600")),

            new SafetyFacPointDto("보안등", new BigDecimal("37.3890500"), new BigDecimal("126.9502300")),
            new SafetyFacPointDto("CCTV", new BigDecimal("37.3891300"), new BigDecimal("126.9501100")),
            new SafetyFacPointDto("안심지킴이집", new BigDecimal("37.3893200"), new BigDecimal("126.9498600")),
            new SafetyFacPointDto("경찰서", new BigDecimal("37.3896800"), new BigDecimal("126.9494000")),
            new SafetyFacPointDto("안전벨", new BigDecimal("37.3900400"), new BigDecimal("126.9489400")),

            new SafetyFacPointDto("보안등", new BigDecimal("37.3896000"), new BigDecimal("126.9484400")),
            new SafetyFacPointDto("CCTV", new BigDecimal("37.3895600"), new BigDecimal("126.9481600")),
            new SafetyFacPointDto("안심지킴이집", new BigDecimal("37.3899300"), new BigDecimal("126.9474200")),
            new SafetyFacPointDto("경찰서", new BigDecimal("37.3900100"), new BigDecimal("126.9473300")),
            new SafetyFacPointDto("안전벨", new BigDecimal("37.3904100"), new BigDecimal("126.9468200")),

            new SafetyFacPointDto("보안등", new BigDecimal("37.3913500"), new BigDecimal("126.9456300")),
            new SafetyFacPointDto("CCTV", new BigDecimal("37.3920100"), new BigDecimal("126.9448200")),
            new SafetyFacPointDto("안심지킴이집", new BigDecimal("37.3922400"), new BigDecimal("126.9445300")),
            new SafetyFacPointDto("경찰서", new BigDecimal("37.3919200"), new BigDecimal("126.9440700")),
            new SafetyFacPointDto("안전벨", new BigDecimal("37.3921400"), new BigDecimal("126.9437800")),

            new SafetyFacPointDto("보안등", new BigDecimal("37.3921000"), new BigDecimal("126.9433600")),
            new SafetyFacPointDto("CCTV", new BigDecimal("37.3919400"), new BigDecimal("126.9426300")),
            new SafetyFacPointDto("안심지킴이집", new BigDecimal("37.3922000"), new BigDecimal("126.9425200")),
            new SafetyFacPointDto("경찰서", new BigDecimal("37.3919800"), new BigDecimal("126.9415400")),
            new SafetyFacPointDto("안전벨", new BigDecimal("37.3922400"), new BigDecimal("126.9411900")),

            new SafetyFacPointDto("보안등", new BigDecimal("37.3926400"), new BigDecimal("126.9409600")),
            new SafetyFacPointDto("CCTV", new BigDecimal("37.3932700"), new BigDecimal("126.9405000")),
            new SafetyFacPointDto("안심지킴이집", new BigDecimal("37.3937800"), new BigDecimal("126.9398800")),
            new SafetyFacPointDto("경찰서", new BigDecimal("37.3942800"), new BigDecimal("126.9391200")),
            new SafetyFacPointDto("안전벨", new BigDecimal("37.3944000"), new BigDecimal("126.9389600"))
    );
}
