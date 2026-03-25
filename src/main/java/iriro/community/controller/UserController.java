package iriro.community.controller;

import iriro.community.dto.UserDto;
import iriro.community.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private UserService userService;

    // 1. 회원가입 post = create = save
    @PostMapping("/join")
    public ResponseEntity<?> join(@RequestBody UserDto userDto){
        return ResponseEntity.ok( userService.join(userDto));
    }
}
