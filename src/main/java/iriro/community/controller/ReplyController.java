package iriro.community.controller;

import iriro.community.dto.ReplyDto;
import iriro.community.entity.ReplyEntity;
import iriro.community.repository.ReplyRepository;
import iriro.community.service.ReplyService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reply")
public class ReplyController {

    @Autowired
    private final ReplyService replyService;

    // 1. 댓글 등록
    // http://localhost:8080/reply/rpwrite
    // { "replyContent" : "박진감보고싶습니감ㅠㅠ" , "boardId" : 2 }
    @PostMapping("/rpwrite")
    public ResponseEntity<?> rpAdd(@RequestBody ReplyDto replyDto, HttpSession session) {
        Object object = session.getAttribute("email");
        String loginEmail = (String) object;
        boolean result = replyService.rpAdd(replyDto, loginEmail);
        return ResponseEntity.ok(result);
    }

    // 2. 댓글 삭제
    // http://localhost:8080/reply/rpdelete?replyId=1
    @DeleteMapping("/rpdelete")
    public ResponseEntity<?> rpDelete(@RequestParam Integer replyId, HttpSession session) {
        Object object = session.getAttribute("email");
        if (object == null) { return ResponseEntity.ok(false);}
            String loginEmail = (String)object;
            boolean result = replyService.rpDelete(replyId, loginEmail);
            return ResponseEntity.ok(result);
        }
    }