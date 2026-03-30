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
import org.apache.catalina.User;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ReplyService {
    private final ReplyRepository replyRepository;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;

    // 1. 댓글 등록

    public boolean rpAdd(ReplyDto replyDto,String loginEmail) {
        if(loginEmail==null)return false;
        UserEntity userEntity = userRepository.findByEmail(loginEmail).orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        ReplyEntity saveEntity = replyDto.toEntity();
        saveEntity.setUserEntity((userEntity));
        // 찾은 유저를 댓글에 연결
        ReplyEntity savedEntity = replyRepository.save(saveEntity); // entity 저장
        return savedEntity.getReplyId() > 0;
    }

    // 2. 댓글 삭제
    public boolean rpDelete(Integer replyId , String loginEmail){
        ReplyEntity reply = replyRepository.findById(replyId).orElse(null);
        if(reply == null)return false;
        if(reply.getUserEntity() == null || !reply.getUserEntity().getEmail().equals(loginEmail)){
            return false;
        }
        replyRepository.deleteById(replyId);
        return true;
    }
}
