package iriro.community.controller;

import io.github.bonigarcia.wdm.config.OperatingSystem;
import iriro.community.dto.BoardDto;
import iriro.community.entity.BoardEntity;
import iriro.community.repository.BoardRepository;
import iriro.community.service.BoardService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {

    @Autowired
    private final BoardService boardService;


    // 1. 리뷰 등록 (회원만 가능)
    // http://localhost:8080/board/rvwrite
    //  { "boardTitle" : "테스트제목", "boardContent" : "테스트내용", "logId" : 1 }
    @PostMapping("/rvwrite")
    public ResponseEntity<?> rbAdd(@RequestBody BoardDto boardDto , HttpSession session){
        // 1) 세션 내 로그인 정보 확인하기
        Object object = session.getAttribute("email");
        if(object == null){ return ResponseEntity.ok(false);} // 만약에 비로그인이면 실패
        // 2) 로그인 중이면
        String loginEmail = (String)object;
        // 3) 서비스에게 입력받은 값과 세션에 저장된 값 전달한다.
        boolean result = boardService.rvAdd(boardDto,loginEmail);
        return ResponseEntity.ok(result);
    }

    // 2. 리뷰 전체 조회
    // http://localhost:8080/board/all
    @GetMapping("/all")
    public List<BoardDto> rbAllView(){
        return boardService.rvAllView();
    }

    // 3. 리뷰 상세 조회
    // http://localhost:8080/board/all/detail?boardId=1
    @GetMapping("/all/detail")
    public BoardDto rvView(@RequestParam Integer boardId){
        return boardService.rvView(boardId);
    }

    // 4. 리뷰 개별 삭제 (회원)
    // http://localhost:8080/board/rvdelete?boardId=11
    @DeleteMapping("/rvdelete")
    public ResponseEntity<?> rvDelete(@RequestParam Integer boardId , HttpSession session){
        Object object = session.getAttribute("email");
        if(object == null){return ResponseEntity.ok(false);}
        String loginEmail = (String)object;
        boolean result = boardService.rvDelete(boardId,loginEmail);
        return ResponseEntity.ok(result);

    }

    // 5. 글 추천
    // http://localhost:8080/board/ddabong
    @PostMapping("/ddabong")
    public boolean ddabong(@RequestParam Integer boardId){return boardService.ddabong(boardId); }
}
