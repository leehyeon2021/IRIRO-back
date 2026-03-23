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

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BoardService {
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final ReplyRepository replyRepository;

    @Autowired
    // 1. 리뷰 등록
    public boolean rvAdd( BoardDto boardDto ){
        // 1] dto --> entity 변환
        BoardEntity boardEntity = boardDto.boardEntity();
        // 2] JPA save 이용하여 insert 하기
        BoardEntity saved = boardRepository.save( boardEntity );
        // 3] save 결과에 pk 여부 성공판단
        if( saved.getBoardId() >= 1 ) return true;
        return false;
    }

}
