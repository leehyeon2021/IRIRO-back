package iriro.community.controller;

import io.github.bonigarcia.wdm.config.OperatingSystem;
import iriro.community.dto.BoardDto;
import iriro.community.entity.BoardEntity;
import iriro.community.repository.BoardRepository;
import iriro.community.service.BoardService;
import iriro.community.service.JWTService;
import iriro.community.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {

    @Autowired
    private final BoardService boardService;
    private final JWTService jwtService;



    // 1. 리뷰 등록 (회원만 가능)
    // http://localhost:8080/board/rvwrite
    // Authorization
    // Bearer eyJhbGciOiJIUzI1NiJ9.eyJlbWFpbCI6InNvc29AbmF2ZXIuY29tIiwiaWF0IjoxNzc0NTA5MzI1LCJleHAiOjE3NzQ1OTU3MjV9.olTdmXyDEL9amHExge5VC8VdwSruWt7Q0ia4q7VcB58
    //  { "boardTitle" : "테스트제목", "boardContent" : "테스트내용", "logId" : 1 }
    @PostMapping("/rvwrite")
    public ResponseEntity<?> rbAdd(@RequestBody BoardDto boardDto ,
                                   @RequestHeader("Authorization")String token) {

        String loginEmail = null; // loginEmail 변수 선언

        // 만약에 토큰이 존재하고 Bearer로 시작할 때만 값을 꺼냄.  + 문자열.startsWith("시작문자")
        if (token != null && token.startsWith("Bearer")) {
            String realToken = token.substring(7);
            loginEmail = jwtService.getClaim(realToken);
        }
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
    @Transactional
    public ResponseEntity<?> rvDelete(@RequestParam Integer boardId , HttpServletRequest request){
        // 요청헤더에서 Authorization 토큰 꺼내기.
        String token = request.getHeader("Authorization");
        // 2. JWTService를 이용해 토큰 안의 이메일 추출
        String loginEmail = jwtService.getClaim(token);

        // 서비스한테 삭제하라고 고함지름
        boolean result = boardService.rvDelete(boardId,loginEmail);
        return ResponseEntity.ok(result);

    }

    // 5. 글 추천
    // http://localhost:8080/board/ddabong
    @PostMapping("/ddabong")
    public boolean ddabong(@RequestParam Integer boardId){return boardService.ddabong(boardId); }
}
