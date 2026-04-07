package iriro.community.service;



import iriro.community.dto.BoardDto;
import iriro.community.entity.BoardEntity;
import iriro.community.repository.BoardRepository;

import iriro.community.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    // 1. 리뷰 등록 (회원만 가능)
    @Transactional
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
    public List<BoardDto> findAll() {
        return boardRepository.findAll(Sort.by(Sort.Direction.DESC , "boardId" )) // .findAll(페이징,정렬) 전체조회
                .stream() // .stream() 이란? 여러개 자료를 갖는 자료(리스트/배열)들의 순차적 처리 지원 함수
                .map(BoardEntity::toDto) // 메소드 레퍼런스란, 화살표 함수 간결하게 사용하는 문법 , 클래스명 :: 함수명
                .toList(); // 리스트 타입으로 반환

    }

    // 3. 리뷰 상세 조회
    public BoardDto findById( Integer boardId ){
            return boardRepository.findById(boardId) // .findById(pk번호) 개별엔티티조회
                    .orElse(null)
                    .toDto(); // 엔티티가 존재하면 dto로 변환
        }


    // 5. 리뷰 개별 삭제 (회원)
    @Transactional
    public boolean rvDelete(Integer boardId,String loginEmail) {
    if(boardId==null||loginEmail==null){return false;}
    BoardEntity board = boardRepository.findById(boardId).orElse(null);

        if(board == null) {return false;}
        if(board.getUserEntity()==null||!board.getUserEntity().getEmail().equals(loginEmail)){
            System.out.println("경고 : 해당하는 글의 작성자가 아닙니다.");
            return false;
        }
        boardRepository.delete(board);
        return true;
    }


    // 5. 글 추천
    @Transactional
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
