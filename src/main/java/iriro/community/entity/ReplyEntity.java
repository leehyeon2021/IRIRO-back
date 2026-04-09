package iriro.community.entity;

import iriro.common.entity.BaseTime;
import iriro.community.dto.ReplyDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table( name = "reply")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReplyEntity extends BaseTime {
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    private Integer replyId;

    // 회원번호(FK)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn( name = "user_id")
    private UserEntity userEntity;

    // 글 번호
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn( name = "board_id")
    private BoardEntity boardEntity;

    @Column( columnDefinition = "longtext" )
    private String replyContent;

    // Entity --> Dto 변환
    public ReplyDto toDto(){
        return ReplyDto.builder()
                .replyId(this.replyId)
                .userId(this.userEntity.getUserId())
                .boardId(this.boardEntity.getBoardId())
                .nickname(this.userEntity.getNickname())
                .replyContent(this.replyContent)
                .createdAt(this.getCreatedAt().toString())
                .build();
    }


}
