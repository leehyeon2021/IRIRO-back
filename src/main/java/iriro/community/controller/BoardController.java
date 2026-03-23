package iriro.community.controller;

import iriro.community.dto.BoardDto;
import iriro.community.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {

    @Autowired
    private BoardService boardService;

    // 1. 리뷰 등록
    // http://localhost:8080/board/rbwrite
    // {"" : "" , "" : "" }
    @PostMapping("/rbwrite")
    public boolean rbAdd(@RequestBody BoardDto boardDto){
        boolean result = boardService.rvAdd(boardDto);
        return result;
    }
}
