package iriro.community.controller;

import iriro.community.dto.UserDto;
import iriro.community.service.JWTService;
import iriro.community.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {


    @Autowired // 주방장을 불러오거라
    private final UserService userService;
    private final JWTService jwtService; // * 토큰

    // 1. 회원가입 post = create = save
    @PostMapping("/join")
    // http://localhost:8080/user/join
    // { "email" : "soso@naver.com","pwToken" : "1234","nickName" : "박진감"}
    public ResponseEntity<?> join(@RequestBody UserDto joinDto) {
        return ResponseEntity.ok(userService.join(joinDto));
    }

    // 2. 로그인
    @PostMapping("/login")
    // http://localhost:8080/user/login
    // { "email" : "soso@naver.com","pwToken" : "1234"}
    // Content-Type : application/json
    public ResponseEntity<?> login(@RequestBody UserDto loginDto) {
        // 입력받은 아이디/비밀번호를 서비스에게 보낸다.
        boolean result = userService.login(loginDto);

        // 로그인 성공시,
        if (result) {
            String token = jwtService.createToken(loginDto.getEmail()); // 로그인성공 아이디를 토큰에 저장
            return ResponseEntity.ok()
                    // 토큰! 서버저장X 클라이언트에 저장O
                    .header("Authorization", "Bearer " + token) // 인증정보 담는 구역
                    // 클라이언트에게 헤더에 발급받은 jwt 토큰 반환한다.
                    .body(true); // 성공
        }
        // 3] 아니면 실패
        return ResponseEntity.ok(false);
    }

    // 3. 로그아웃 Get
    @GetMapping("/logout")
    // http://localhost:8080/user/logout?userId=11
    public ResponseEntity<?> logout() {
        // 서버는 할 게 없지만 프론트에게 지워도 된다고 신호를 줌...
        return ResponseEntity.ok(true);
    }


    // 4. 마이페이지
    @GetMapping("/myinfo")
    public ResponseEntity<?> myInfo(@RequestHeader(value = "Authorization") String token) {
        if (token == null || !token.startsWith("Bearer")) {
            return ResponseEntity.ok(false);
        }
        token = token.replace("Bearer ", "");
        String email = jwtService.getClaim(token);
        if (email == null) {
            return ResponseEntity.ok(false);
        }
        UserDto result = userService.myInfo(email);
        if (result == null) {
            return ResponseEntity.status(404).body("회원 정보가 없습니다.");
        }
        return ResponseEntity.ok(result);
    }
}
