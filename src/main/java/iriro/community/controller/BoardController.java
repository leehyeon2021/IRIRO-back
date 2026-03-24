package iriro.community.controller;

import iriro.community.dto.BoardDto;
import iriro.community.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {

    @Autowired
    private BoardService boardService;

    // 1. 리뷰 등록
    // http://localhost:8080/board/rbwrite
    //  { "boardTitle" : "테스트제목", "boardContent" : "테스트내용", "logId" : 1 }
    @PostMapping("/rbwrite")
    public boolean rbAdd(@RequestBody BoardDto boardDto){
        boolean result = boardService.rvAdd(boardDto);
        return result;
    }

//    // 2. 리뷰 전체 조회
//    // http://localhost:8080/board/all
//    @GetMapping("/all")
//    public List<BoardDto> rbAllView(){
//
//    }
}
