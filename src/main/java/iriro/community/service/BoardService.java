package iriro.community.service;



import iriro.community.dto.BoardDto;
import iriro.community.entity.BoardEntity;
import iriro.community.repository.BoardRepository;
import iriro.community.repository.ReplyRepository;
import iriro.community.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static aQute.bnd.annotation.headers.Resolution.optional;

@Service
@RequiredArgsConstructor
@Transactional
public class BoardService {
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final ReplyRepository replyRepository;

    // 1. 리뷰 등록
    public boolean rvAdd( BoardDto boardDto ){
        // 1] dto --> entity 변환
        BoardEntity boardEntity = boardDto.toEntity();
        // 2] JPA save 이용하여 insert 하기
        BoardEntity saved = boardRepository.save( boardEntity );
        // 3] save 결과에 pk 여부 성공판단
        if( saved.getBoardId() >= 1 ) return true;
        return false;
    }

    // 2. 리뷰 전체 조회
    public List<BoardDto> rvAllView(){
        // 1. DB(창고)에서 모든 엔티티(생고기 박스) 꺼내오기.
        List<BoardEntity> entityList = boardRepository.findAll();

        // 2. Dto(플레이팅접시)들을 담을 빈 박스를 새로 만든다.
        List<BoardDto> dtoList = new ArrayList<>();

        // 3. Entity --> Dto 변환 생고기박스에서 하나씩 꺼냄 앤나 접시에 옮김
        for(BoardEntity entity : entityList){
            BoardDto dto = entity.toDto(); // 생고기를 접시에 플레이팅함

            // 접시를 새박스에 쌓는다.
            dtoList.add(dto);
        }
        return dtoList;
    }


    // 3. 리뷰 상세 조회
    public BoardDto rvView(Integer boardId){
        BoardEntity entity = boardRepository.findById(boardId).orElse(null);
        return entity.toDto(); // 엔티티 --> 디티오
    }

    // 4. 리뷰 개별 삭제
    public boolean rvDelete(Integer boardId){
        try {
            boardRepository.deleteById(boardId);
            return true;
        } catch (Exception e){
            return false;
        }
    }

}
