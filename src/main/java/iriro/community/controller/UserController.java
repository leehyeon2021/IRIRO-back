package iriro.community.controller;

import iriro.community.dto.UserDto;
import iriro.community.entity.ReplyEntity;
import iriro.community.entity.UserEntity;
import iriro.community.repository.UserRepository;
import iriro.community.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {


    @Autowired // 주방장을 불러오거라
    private UserService userService;
    private UserRepository userRepository;

    // 1. 회원가입 post = create = save
    @PostMapping("/join")
    // http://localhost:8080/user/join
    // { "email" : "soso@naver.com","pwToken" : "1234","nickName" : "박진감"}
    public ResponseEntity<?> join(@RequestBody UserDto joinDto) {
        return ResponseEntity.ok(userService.join(joinDto));
    }

    // 2. 로그인 Get
    @PostMapping("/login")
    // http://localhost:8080/user/login
    // { "email" : "soso@naver.com","pwToken" : "1234"}
    public ResponseEntity<?> login(@RequestBody UserDto loginDto, HttpSession session) {
        // 1] 입력받은 아이디/비밀번호를 서비스에게 보낸다.
        boolean result = userService.login(loginDto);
        if (result) {// 1) 매개변수에 HttpSession session 받는다.
            // 2) 로그인 성공한 회원의 아이디를 세션 객체내 저장 , .setAttribute("속성명",속성값);
            session.setAttribute("email", loginDto.getEmail());
        }
        // 3] 아니면 실패
        return ResponseEntity.ok(result);
    }

    // 3. 로그아웃 Get
    @GetMapping("/logout")
    // http://localhost:8080/user/logout?userId=11
    public ResponseEntity<?> logout(HttpSession session){ // 1) 매개변수에 HttpSession session 받는다.
        session.removeAttribute("email");
        return ResponseEntity.ok(true);
    }
}
