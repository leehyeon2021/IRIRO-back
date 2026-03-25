package iriro.community.service;

import iriro.community.dto.ReplyDto;
import iriro.community.entity.ReplyEntity;
import iriro.community.repository.ReplyRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

@Service
@RequiredArgsConstructor
@Transactional
public class ReplyService {
    private final ReplyRepository replyRepository;

    // 1. 댓글 등록
    public boolean rpAdd(ReplyDto replyDto){
        // 1] dto --> entity 변환
        ReplyEntity replyEntity = replyDto.toEntity();
        // 2] JPA save 이용하여 insert 하기
        ReplyEntity saved = replyRepository.save(replyEntity);
        // 3] save 결과에 pk 여부 성공판단
        if(saved.getReplyId() >= 1) return true;
        return false;
    }

    // 2. 댓글 삭제
    public boolean rpDelete(Integer replyId){
        try{ replyRepository.deleteById(replyId);
        return true; } catch (Exception e){
        return false;}
    }




}
