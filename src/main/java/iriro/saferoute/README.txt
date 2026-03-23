
< 파일 구조 >
- controller: SafeRouteController: 경로 추천 및 경로 로그 저장

- dto : RoutePointDto( 위도, 경도, 순서 )가 포함된 Dto
        SafeRouteRequest: 사용자가 입력한 출발지/목적지에 대한 위도, 경도
        SafeRouteResponse: 응답 객체(경로, 안전점수, 걸리는시간, 거리)

- entity: LocationlogEntity: 로그 저장 엔티티
          RoutePointLogEntity: 로그 저장 시 경로 배열 저장 엔티티

- repository: LocationLogRespository: 로그 저장에 관한 Repo
              RoutePointLogRepository: 로그 저장 시 경로 배열 저장에 관한 Repo

- service: SafeRouteService: 안전 경로 핵심 로직( 경로에 대한 안전점수 계산 로직 )
            -> 기존 경로가 위험지역이 없고 어느정도의 안전점수(20점?)만 넘기면 바로 리턴
            -> 위험지역이 경로 상에 가까이( 30m )에 있다면 우회 경로를 추가로 받아옴. ( 총 3개 )
            -> 추가로 받은 경로 2개 + 기존 경로 1개 => 3개 전부에 위험지역이 있다면 1번 더 우회 경로를 추가로 받아옴. ( 총 5개 )
            -> 이 5개의 경로 중 가장 안전점수가 높은 경로를 반환.
            -- 안전 점수 계산 로직 --
            CCTV, 보안등
            치안시설
            위험지역

           TmapRouteService: 티맵 경로API 호출( 경로가 담긴 List 반환 )




           + 위험 지역 표시, 안전 지역 표시 하는 서비스는 누가 맡는가?