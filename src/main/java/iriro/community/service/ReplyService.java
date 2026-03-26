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
    public boolean rpAdd(ReplyDto replyDto,String loginEmail){
        // 1] dto --> entity 변환
        ReplyEntity saveReplyEntity = replyDto.toEntity();
        // ********** 저장하기 전에 FK 대입하기 , Fk의 엔티티를 찾아서 대입 **************

        UserEntity userEntity;

        // 비회원일 때
        if(loginEmail==null){
        Optional<UserEntity> entityOptional1 = userRepository.findById(1);
        userEntity= entityOptional1.get();
        }
        else{
            // 회원일 때
            // 현재 로그인 중인 email로 엔티티 찾기
            Optional<UserEntity> entityOptional = userRepository.findByEmail(loginEmail);
            if(entityOptional.isPresent()) {
                userEntity = entityOptional.get();
            } else{
                return false; // 세션은 있는데 DB에 유저가 없다면 실패
            }
        }
        // 찾은 유저를 댓글에 대입
        saveReplyEntity.setUserEntity(userEntity);

        // * 어떤 글에 다는 댓글인지
        Optional<BoardEntity> boardOptional = boardRepository.findById(replyDto.getBoardId());

        if(boardOptional.isPresent()){
            saveReplyEntity.setBoardEntity(boardOptional.get());

            replyRepository.save(saveReplyEntity);
            return true;
        }
        return false;
    }

    // 2. 댓글 삭제 ( 비회원은 삭제 X ... 회원만 삭제 O )
    public boolean rpDelete(Integer replyId , String loginEmail){
    Optional<ReplyEntity> replyOptional = replyRepository.findById(replyId);
    if(replyOptional.isPresent()){
        ReplyEntity reply = replyOptional.get();
        if(reply.getUserEntity().getEmail().equals(loginEmail)){
            replyRepository.deleteById(replyId);
            return true;
        }
    }
    return false;
    }




}
