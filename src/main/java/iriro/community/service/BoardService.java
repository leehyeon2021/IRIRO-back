package iriro.community.service;



import iriro.community.dto.BoardDto;
import iriro.community.entity.BoardEntity;
import iriro.community.entity.UserEntity;
import iriro.community.repository.BoardRepository;

import iriro.community.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    // 1. 리뷰 등록 (회원만 가능)
    public boolean rvAdd(BoardDto boardDto, String loginEmail) {

        // 비회원이면 글쓰기 거절
        if (loginEmail == null) return false;

            return userRepository.findByEmail(loginEmail)
                    .map(userEntity -> {
                        BoardEntity saveEntity = boardDto.toEntity();
                        saveEntity.setUserEntity(userEntity);
                        return boardRepository.save(saveEntity).getBoardId() > 0;
                    })
                    .orElse(false);
    }

    // 2. 리뷰 전체 조회
    public List<BoardDto> rvAllView() {
        // 1. DB(창고)에서 모든 엔티티(생고기 박스) 꺼내오기.
        List<BoardEntity> entityList = boardRepository.findAll();

        // 2. Dto(플레이팅접시)들을 담을 빈 박스를 새로 만든다.
        List<BoardDto> dtoList = new ArrayList<>();

        // 3. Entity --> Dto 변환 생고기박스에서 하나씩 꺼냄 앤나 접시에 옮김
        for (BoardEntity entity : entityList) {
            BoardDto dto = entity.toDto(); // 생고기를 접시에 플레이팅함

            // 접시를 새박스에 쌓는다.
            dtoList.add(dto);
        }
        return dtoList;
    }

    // 3. 리뷰 상세 조회
    public BoardDto rvView(Integer boardId) {
        BoardEntity entity = boardRepository.findById(boardId).orElse(null);
        return entity.toDto(); // 엔티티 --> 디티오
    }

//    // 4. 리뷰 개별 수정
//    @Transactional
//    public BoardDto rvUpdate(Integer boardId, BoardDto boardDto, HttpServletRequest request){
//        BoardEntity boardEntity = boardRepository.findByIdAndUserId(boardId,email)
//    }


    // 5. 리뷰 개별 삭제 (회원)
    public void rvDelete(Integer boardId,String loginEmail) {
        BoardEntity board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("해당 리뷰가 존재하지 않습니다."));
        if(board.getUserEntity()==null||!board.getUserEntity().getEmail().equals(loginEmail))
    }


    // 5. 글 추천
    public boolean ddabong(Integer boardId) {
        Optional<BoardEntity> optionalBoard = boardRepository.findById(boardId);

        if (optionalBoard.isPresent()) {
            BoardEntity board = optionalBoard.get();

            board.setRecommendCount(board.getRecommendCount() + 1);

            return true;
        }
        return false;
    }
}
