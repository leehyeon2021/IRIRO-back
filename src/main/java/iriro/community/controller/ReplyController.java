package iriro.community.controller;

import iriro.community.dto.ReplyDto;
import iriro.community.service.ReplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reply")
public class ReplyController {

    @Autowired
    private ReplyService replyService;

    // 1. 댓글 등록
    // http://localhost:8080/reply/rpwrite
    // { "replyContent" : "박진감보고싶습니감ㅠㅠ" }
    @PostMapping("/rpwrite")
    public boolean rpAdd(@RequestBody ReplyDto replyDto){
        boolean reusult = replyService.rpAdd(replyDto);
        return reusult;
    }

    // 2. 댓글 삭제
    // http://localhost:8080/reply/rpdelete?replyId=1
    @DeleteMapping("/rpdelete")
    public boolean rpDelete(Integer replyId){
        boolean result = replyService.rpDelete(replyId);
        return result;
    }

}
