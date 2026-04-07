package iriro.community.controller;
import iriro.community.dto.BoardDto;
import iriro.community.service.BoardService;
import iriro.community.service.JWTService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@CrossOrigin( value = "http://localhost:5173" , exposedHeaders = "Authorization")
@RequiredArgsConstructor
@RequestMapping("/api/board")
public class BoardController {

    private final BoardService boardService;
    private final JWTService jwtService;



    // 1. 리뷰 등록 (회원만 가능)
    // http://localhost:8080/api/board
    // Authorization
    // Bearer eyJhbGciOiJIUzI1NiJ9.eyJlbWFpbCI6InNvc29AbmF2ZXIuY29tIiwiaWF0IjoxNzc0NTA5MzI1LCJleHAiOjE3NzQ1OTU3MjV9.olTdmXyDEL9amHExge5VC8VdwSruWt7Q0ia4q7VcB58
    //  { "boardTitle" : "테스트제목", "boardContent" : "테스트내용", "logId" : 1 }
    // { "boardTitle" : "회원만 쓸 수 있다능", "boardContent" : "회원이 씀", "logId" : 1 }
    @PostMapping("/rvwrite")
    public ResponseEntity<?> rbAdd(@RequestBody BoardDto boardDto ,
                                   @RequestHeader(value = "Authorization",required = false)String token) {


        // 만약에 토큰이 존재하고 Bearer로 시작할 때만 값을 꺼냄.  + 문자열.startsWith("시작문자")
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.ok(false);
        }
        String realToken = token.replace("Bearer ","");
        String loginEmail = jwtService.getClaim(realToken);
        if(loginEmail==null){
            return ResponseEntity.status(401).body("로그인이 만료되었습니다.");
        }
        boolean result = boardService.rvAdd(boardDto,loginEmail);
        return ResponseEntity.ok("리뷰가 등록되었습니다");
        }

    // 2. 리뷰 전체 조회
    // http://localhost:8080/api/board
    @GetMapping("/all")
    public ResponseEntity<?> findALl(){
        return ResponseEntity.ok(boardService.findAll());
    }

    // 3. 리뷰 상세 조회
    // http://localhost:8080/api/board/detail?boardId=1
    @GetMapping("/detail")
    public ResponseEntity<?> findById(@RequestParam Integer boardId){
        return ResponseEntity.ok(boardService.findById(boardId));
    }


    // 5. 리뷰 개별 삭제
    // http://localhost:8080/api/board?boardId=11
    @DeleteMapping("/rvdelete")
    public ResponseEntity<?> rvDelete(@RequestParam Integer boardId ,
                                      @RequestHeader(value="Authorization",required = false)String token){
        if (token == null || !token.startsWith("Bearer ")){
            return ResponseEntity.ok(false);
        }
        String realToken = token.replace("Bearer ","");
        String loginEmail = jwtService.getClaim(realToken);
        if(loginEmail==null){
            return ResponseEntity.ok(false);
        }
        boolean result = boardService.rvDelete(boardId,loginEmail);
        return ResponseEntity.ok(result);
        }

    // 6. 글 추천
    // http://localhost:8080/api/board/ddabong?boardId=10
    @PostMapping("/ddabong")
    public boolean ddabong(@RequestParam Integer boardId){return boardService.ddabong(boardId); }

}
