package iriro.saferoute.test;

import iriro.saferoute.dto.RoutePointDto;
import iriro.saferoute.dto.RouteRequestDto;
import iriro.saferoute.dto.SafetyFacDto;

import java.math.BigDecimal;
import java.util.List;

// 가상 DB
public class TestSampleCode {

    // 1. 테스트요청: 출발지와 목적지 위/경도
    public static final RouteRequestDto testRouteRequest =
            new RouteRequestDto( 37.3895300, 126.959400, 37.3976478, 126.9312600);


    // 2. 가상 위험 테이블의 위/경도를 뽑아 온 값 ( 실제로는 위험테이블에서 조회해와서 리스트화 시켜야함 )
    public static final List<RoutePointDto> dangerRoutePoints = List.of(
            new RoutePointDto(new BigDecimal("37.389308261930765"), new BigDecimal("126.95942775162302"), 1),
            new RoutePointDto(new BigDecimal("37.389302705581805"), new BigDecimal("126.95934720354772"), 2),
            new RoutePointDto(new BigDecimal("37.38931658993338"), new BigDecimal("126.95918332917094"), 3),
            new RoutePointDto(new BigDecimal("37.38934714000943"), new BigDecimal("126.95907500483108"), 4),
            new RoutePointDto(new BigDecimal("37.389413796907185"), new BigDecimal("126.95896112442831"), 5),
            new RoutePointDto(new BigDecimal("37.389538780468584"), new BigDecimal("126.95885279744043"), 6),
            new RoutePointDto(new BigDecimal("37.389924842908314"), new BigDecimal("126.95863613964843"), 7),
            new RoutePointDto(new BigDecimal("37.39008593373755"), new BigDecimal("126.95854725432454"), 8),
            new RoutePointDto(new BigDecimal("37.39023591479455"), new BigDecimal("126.95846114683742"), 9),
            new RoutePointDto(new BigDecimal("37.39039700562371"), new BigDecimal("126.95837226151355"), 10),
            new RoutePointDto(new BigDecimal("37.390555318997386"), new BigDecimal("126.95828337626756"), 11),
            new RoutePointDto(new BigDecimal("37.39071085496515"), new BigDecimal("126.95819726862466"), 12),
            new RoutePointDto(new BigDecimal("37.39086361347744"), new BigDecimal("126.95811116105968"), 13),
            new RoutePointDto(new BigDecimal("37.391035813979464"), new BigDecimal("126.95801394284867"), 14),
            new RoutePointDto(new BigDecimal("37.391035813979464"), new BigDecimal("126.95801394284867"), 15),
            new RoutePointDto(new BigDecimal("37.39098303954915"), new BigDecimal("126.95785840291721"), 16),
            new RoutePointDto(new BigDecimal("37.39096637397351"), new BigDecimal("126.95781118545611"), 17),
            new RoutePointDto(new BigDecimal("37.39096637397351"), new BigDecimal("126.95781118545611"), 18),
            new RoutePointDto(new BigDecimal("37.39054695638396"), new BigDecimal("126.95658908612803"), 19),
            new RoutePointDto(new BigDecimal("37.39050529336228"), new BigDecimal("126.95652242669142"), 20),
            new RoutePointDto(new BigDecimal("37.39050529336228"), new BigDecimal("126.95652242669142"), 21),
            new RoutePointDto(new BigDecimal("37.390335859209024"), new BigDecimal("126.95599747917913"), 22),
            new RoutePointDto(new BigDecimal("37.390335859209024"), new BigDecimal("126.95599747917913"), 23),
            new RoutePointDto(new BigDecimal("37.39032752595021"), new BigDecimal("126.95594748395916"), 24),
            new RoutePointDto(new BigDecimal("37.39031086032507"), new BigDecimal("126.95589748897284"), 25),
            new RoutePointDto(new BigDecimal("37.39001365672666"), new BigDecimal("126.95500868924151"), 26),
            new RoutePointDto(new BigDecimal("37.38999976870574"), new BigDecimal("126.95496702675288"), 27),
            new RoutePointDto(new BigDecimal("37.38999976870574"), new BigDecimal("126.95496702675288"), 28),
            new RoutePointDto(new BigDecimal("37.38995532718756"), new BigDecimal("126.95484203936485"), 29),
            new RoutePointDto(new BigDecimal("37.38995532718756"), new BigDecimal("126.95484203936485"), 30),
            new RoutePointDto(new BigDecimal("37.389716453277686"), new BigDecimal("126.95412822208552"), 31),
            new RoutePointDto(new BigDecimal("37.38969978760304"), new BigDecimal("126.95407544957395"), 32),
            new RoutePointDto(new BigDecimal("37.389222039983025"), new BigDecimal("126.95265892511556"), 33),
            new RoutePointDto(new BigDecimal("37.38909704784518"), new BigDecimal("126.9522867402428"), 34),
            new RoutePointDto(new BigDecimal("37.38908038266636"), new BigDecimal("126.95226174298317"), 35),
            new RoutePointDto(new BigDecimal("37.38906927274543"), new BigDecimal("126.95225618824422"), 36),
            new RoutePointDto(new BigDecimal("37.38905538541863"), new BigDecimal("126.95225341110832"), 37),
            new RoutePointDto(new BigDecimal("37.38904983050774"), new BigDecimal("126.95225341126404"), 38),
            new RoutePointDto(new BigDecimal("37.38904983050774"), new BigDecimal("126.95225341126404"), 39),
            new RoutePointDto(new BigDecimal("37.38894983695619"), new BigDecimal("126.9519645514464"), 40),
            new RoutePointDto(new BigDecimal("37.38894983695619"), new BigDecimal("126.9519645514464"), 41),
            new RoutePointDto(new BigDecimal("37.38895261401507"), new BigDecimal("126.95194233116696"), 42),
            new RoutePointDto(new BigDecimal("37.38895539097479"), new BigDecimal("126.95191455583713"), 43),
            new RoutePointDto(new BigDecimal("37.38894983522118"), new BigDecimal("126.95186733806449"), 44),
            new RoutePointDto(new BigDecimal("37.38860541319803"), new BigDecimal("126.95088410379881"), 45),
            new RoutePointDto(new BigDecimal("37.38860818990994"), new BigDecimal("126.95084244084302"), 46),
            new RoutePointDto(new BigDecimal("37.388610966869685"), new BigDecimal("126.95081466551318"), 47),
            new RoutePointDto(new BigDecimal("37.38863040801686"), new BigDecimal("126.9507563369391"), 48),
            new RoutePointDto(new BigDecimal("37.38904701691542"), new BigDecimal("126.95022859547409"), 49),
            new RoutePointDto(new BigDecimal("37.38913311595228"), new BigDecimal("126.9501119370025"), 50)
    );

    // 3. 가상의 안전한 시설의 위치 테이블
    public static final List<SafetyFacDto> safeRoutePoint = List.of(
            new SafetyFacDto("보안등", new BigDecimal("37.3893500"), new BigDecimal("126.9593000")),
            new SafetyFacDto("CCTV", new BigDecimal("37.3894200"), new BigDecimal("126.9590500")),
            new SafetyFacDto("안심지킴이집", new BigDecimal("37.3895600"), new BigDecimal("126.9588700")),
            new SafetyFacDto("경찰서", new BigDecimal("37.3899200"), new BigDecimal("126.9586400")),
            new SafetyFacDto("안전벨", new BigDecimal("37.3900900"), new BigDecimal("126.9585400")),

            new SafetyFacDto("보안등", new BigDecimal("37.3902500"), new BigDecimal("126.9584500")),
            new SafetyFacDto("CCTV", new BigDecimal("37.3904000"), new BigDecimal("126.9583600")),
            new SafetyFacDto("안심지킴이집", new BigDecimal("37.3905600"), new BigDecimal("126.9582700")),
            new SafetyFacDto("경찰서", new BigDecimal("37.3907100"), new BigDecimal("126.9581900")),
            new SafetyFacDto("안전벨", new BigDecimal("37.3908600"), new BigDecimal("126.9581100")),

            new SafetyFacDto("보안등", new BigDecimal("37.3910000"), new BigDecimal("126.9580000")),
            new SafetyFacDto("CCTV", new BigDecimal("37.3909800"), new BigDecimal("126.9578600")),
            new SafetyFacDto("안심지킴이집", new BigDecimal("37.3909700"), new BigDecimal("126.9578100")),
            new SafetyFacDto("경찰서", new BigDecimal("37.3905500"), new BigDecimal("126.9566000")),
            new SafetyFacDto("안전벨", new BigDecimal("37.3905000"), new BigDecimal("126.9565200")),

            new SafetyFacDto("보안등", new BigDecimal("37.3903300"), new BigDecimal("126.9560000")),
            new SafetyFacDto("CCTV", new BigDecimal("37.3903000"), new BigDecimal("126.9559000")),
            new SafetyFacDto("안심지킴이집", new BigDecimal("37.3900100"), new BigDecimal("126.9550100")),
            new SafetyFacDto("경찰서", new BigDecimal("37.3900000"), new BigDecimal("126.9549700")),
            new SafetyFacDto("안전벨", new BigDecimal("37.3899600"), new BigDecimal("126.9548400")),

            new SafetyFacDto("보안등", new BigDecimal("37.3897200"), new BigDecimal("126.9541300")),
            new SafetyFacDto("CCTV", new BigDecimal("37.3897000"), new BigDecimal("126.9540800")),
            new SafetyFacDto("안심지킴이집", new BigDecimal("37.3892200"), new BigDecimal("126.9526600")),
            new SafetyFacDto("경찰서", new BigDecimal("37.3891000"), new BigDecimal("126.9522900")),
            new SafetyFacDto("안전벨", new BigDecimal("37.3890800"), new BigDecimal("126.9522600")),

            new SafetyFacDto("보안등", new BigDecimal("37.3890500"), new BigDecimal("126.9522500")),
            new SafetyFacDto("CCTV", new BigDecimal("37.3889500"), new BigDecimal("126.9519600")),
            new SafetyFacDto("안심지킴이집", new BigDecimal("37.3889500"), new BigDecimal("126.9518700")),
            new SafetyFacDto("경찰서", new BigDecimal("37.3886100"), new BigDecimal("126.9508800")),
            new SafetyFacDto("안전벨", new BigDecimal("37.3886300"), new BigDecimal("126.9507600")),

            new SafetyFacDto("보안등", new BigDecimal("37.3890500"), new BigDecimal("126.9502300")),
            new SafetyFacDto("CCTV", new BigDecimal("37.3891300"), new BigDecimal("126.9501100")),
            new SafetyFacDto("안심지킴이집", new BigDecimal("37.3893200"), new BigDecimal("126.9498600")),
            new SafetyFacDto("경찰서", new BigDecimal("37.3896800"), new BigDecimal("126.9494000")),
            new SafetyFacDto("안전벨", new BigDecimal("37.3900400"), new BigDecimal("126.9489400")),

            new SafetyFacDto("보안등", new BigDecimal("37.3896000"), new BigDecimal("126.9484400")),
            new SafetyFacDto("CCTV", new BigDecimal("37.3895600"), new BigDecimal("126.9481600")),
            new SafetyFacDto("안심지킴이집", new BigDecimal("37.3899300"), new BigDecimal("126.9474200")),
            new SafetyFacDto("경찰서", new BigDecimal("37.3900100"), new BigDecimal("126.9473300")),
            new SafetyFacDto("안전벨", new BigDecimal("37.3904100"), new BigDecimal("126.9468200")),

            new SafetyFacDto("보안등", new BigDecimal("37.3913500"), new BigDecimal("126.9456300")),
            new SafetyFacDto("CCTV", new BigDecimal("37.3920100"), new BigDecimal("126.9448200")),
            new SafetyFacDto("안심지킴이집", new BigDecimal("37.3922400"), new BigDecimal("126.9445300")),
            new SafetyFacDto("경찰서", new BigDecimal("37.3919200"), new BigDecimal("126.9440700")),
            new SafetyFacDto("안전벨", new BigDecimal("37.3921400"), new BigDecimal("126.9437800")),

            new SafetyFacDto("보안등", new BigDecimal("37.3921000"), new BigDecimal("126.9433600")),
            new SafetyFacDto("CCTV", new BigDecimal("37.3919400"), new BigDecimal("126.9426300")),
            new SafetyFacDto("안심지킴이집", new BigDecimal("37.3922000"), new BigDecimal("126.9425200")),
            new SafetyFacDto("경찰서", new BigDecimal("37.3919800"), new BigDecimal("126.9415400")),
            new SafetyFacDto("안전벨", new BigDecimal("37.3922400"), new BigDecimal("126.9411900")),

            new SafetyFacDto("보안등", new BigDecimal("37.3926400"), new BigDecimal("126.9409600")),
            new SafetyFacDto("CCTV", new BigDecimal("37.3932700"), new BigDecimal("126.9405000")),
            new SafetyFacDto("안심지킴이집", new BigDecimal("37.3937800"), new BigDecimal("126.9398800")),
            new SafetyFacDto("경찰서", new BigDecimal("37.3942800"), new BigDecimal("126.9391200")),
            new SafetyFacDto("안전벨", new BigDecimal("37.3944000"), new BigDecimal("126.9389600"))
    );
}
