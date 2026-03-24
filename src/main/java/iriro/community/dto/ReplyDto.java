package iriro.community.dto;

import iriro.community.entity.ReplyEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor // 롬복
public class ReplyDto {
    private Integer replyId; // 댓글번호
    private Integer userId; // 회원번호
    private Integer boardId; // 글 번호
    private String replyContent; // 내용
    private String createdAt;
    private String updatedAt;

    // + Dto --> Entity 변환 // 사용자가 직접 입력한 데이터만.
    public ReplyEntity toEntity(){
        return ReplyEntity.builder()
                .replyContent(this.replyContent)
                .build();
    }
}
