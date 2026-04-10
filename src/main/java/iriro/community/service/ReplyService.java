package iriro.community.service;

import iriro.community.dto.ReplyDto;
import iriro.community.entity.BoardEntity;
import iriro.community.entity.ReplyEntity;
import iriro.community.entity.UserEntity;
import iriro.community.repository.BoardRepository;
import iriro.community.repository.ReplyRepository;
import iriro.community.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReplyService {
    private final ReplyRepository replyRepository;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;

    // 1. 댓글 등록
    @Transactional
    public boolean rpAdd(ReplyDto replyDto,String loginEmail) {
        if(loginEmail==null)return false;
        UserEntity userEntity = userRepository.findByEmail(loginEmail).orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        BoardEntity boardEntity = boardRepository.findById(replyDto.getBoardId()).orElseThrow(()->new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        ReplyEntity saveEntity = replyDto.toEntity();

        saveEntity.setUserEntity((userEntity));
        saveEntity.setBoardEntity(boardEntity);
        ReplyEntity savedEntity = replyRepository.save(saveEntity); // entity 저장
        return savedEntity.getReplyId() > 0;
    }

    // 2. 댓글 삭제
    @Transactional
    public boolean rpDelete(Integer replyId , String loginEmail){
        ReplyEntity reply = replyRepository.findById(replyId).orElse(null);

        if(reply == null)return false;

        // 비회원은 삭제 불가능
        if(loginEmail.equals("iriro@google.com")){
            System.out.println("비회원 댓글은 삭제가 불가능합니다.");
            return false;
        }
        // 작성자 본인 확인
        if(reply.getUserEntity() == null || !reply.getUserEntity().getEmail().equals(loginEmail)){
            return false;
        }
        replyRepository.deleteById(replyId);
        return true;
    }

    // 3. 댓글 전체 조회
    public List<ReplyDto> findAll() {
        return replyRepository.findAll(Sort.by(Sort.Direction.DESC, "replyId"))
                .stream()
                .map(ReplyEntity::toDto)
                .toList();
    }
}
