package iriro.community.controller;

import iriro.community.dto.ReplyDto;
import iriro.community.service.JWTService;
import iriro.community.service.ReplyService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
@RequestMapping("/api/board")
public class ReplyController {

    private final ReplyService replyService;
    private final JWTService jwtService;

    // 1. 댓글 등록
    // http://localhost:8080/api/board/rp
    // Bearer eyJhbGciOiJIUzI1NiJ9.eyJlbWFpbCI6InNvc29AbmF2ZXIuY29tIiwiaWF0IjoxNzc0OTM3NjY1LCJleHAiOjE3NzUwMjQwNjV9.e2i3Yl9CpiEjvSH-uAwxy1pINyKvrbzHt0XNGwPS7Ws
    // { "replyContent" : "박진감보고싶습니감ㅠㅠ" , "boardId" : 2 }
    @PostMapping("/rp")
    public ResponseEntity<?> rpAdd(@RequestBody ReplyDto replyDto, @RequestHeader(value = "Authorization",required = false) String token) {

        // 기본값=비회원 이메일
        String loginEmail = "test@gmail.com";

        if(token != null && token.startsWith("Bearer ")) {
            String realToken = token.replace("Bearer ", "");
            String realEmail = jwtService.getClaim(realToken);
            if (realEmail != null) {
                loginEmail = realEmail;
            }
        }
        boolean result = replyService.rpAdd(replyDto, loginEmail);
        return ResponseEntity.ok(result);
    }

    // 2. 댓글 삭제
    // http://localhost:8080/api/board/rpdelete?replyId=1
    @DeleteMapping("/rpdelete")
    public ResponseEntity<?> rpDelete(@RequestParam Integer replyId,
                                      @RequestHeader(value = "Authorization",required = false)String token) {
        String loginEmail = "test.gmail.com";
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.ok(replyService.rpDelete(replyId, loginEmail));
        }
        String realToken = token.replace("Bearer ", "");
        String realEmail = jwtService.getClaim(realToken);
        if (realEmail != null) loginEmail = realEmail;
        return ResponseEntity.ok(replyService.rpDelete(replyId, loginEmail));
    }


    }