package iriro.community.controller;

import iriro.community.dto.ReplyDto;
import iriro.community.service.ReplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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


}
