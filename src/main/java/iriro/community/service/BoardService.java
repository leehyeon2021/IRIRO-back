package iriro.community.service;



import iriro.community.dto.BoardDto;
import iriro.community.entity.BoardEntity;
import iriro.community.entity.UserEntity;
import iriro.community.repository.BoardRepository;

import iriro.community.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Transactional
public class BoardService {
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    // 1. 리뷰 등록 (회원)
    public boolean rvAdd(BoardDto boardDto, String loginEmail) {
        // 1] dto --> entity 변환
        BoardEntity saveEntity = boardDto.toEntity();
        // ********* 저장하기 전에 FK 대입하기 , FK의 엔티티를 찾아서 대입 ***********
        // 현재 로그인 중인 email로 엔티티 찾기
        Optional<UserEntity> entityOptional = userRepository.findByEmail(loginEmail);
        if (!entityOptional.isPresent()) { // !부정문 , isPresent() 아니면
            return false; // 존재하지 않은 회원으로 실패
        }
        // 저장할 게시물 엔티티에 set 참조 엔티티(회원엔티티);
        saveEntity.setUserEntity(entityOptional.get());

        BoardEntity savedEntity = boardRepository.save(saveEntity); // 2] entity 저장한다.
        if (savedEntity.getBoardId() > 0) {
            return true;
        } else {
            return false;
        }
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

    // 4. 리뷰 개별 삭제 (회원)
    public boolean rvDelete(Integer boardId, String loginEmail) {
        Optional<BoardEntity> boardOptional = boardRepository.findById(boardId);
        if (boardOptional.isPresent()) {
            BoardEntity board = boardOptional.get();
            if (board.getUserEntity().getEmail().equals(loginEmail)) {
                boardRepository.deleteById(boardId);
                return true;
            }
        }
        return false;
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
