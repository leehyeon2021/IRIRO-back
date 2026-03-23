package iriro.community.entity;

import iriro.common.entity.BaseTime;
import iriro.community.dto.ReplyDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

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

    @Column( columnDefinition = "longtext" )
    private String replyContent;

    // 회원번호(FK)
    @ManyToOne
    @JoinColumn( name = "user_id")
    private UserEntity userEntity;

    // 글 번호
    @ManyToOne
    @JoinColumn( name = "board_id")
    private BoardEntity boardEntity;

    // Entity --> Dto 변환
    public ReplyDto toDto(){
     return ReplyDto.builder()
             .replyId(this.replyId).userId(this.replyId).boardId(this.replyId).replyContent(this.replyContent).createdAt(getCreatedAt().toString()).updatedAt(getUpdatedAt().toString())
             .build();
    }

}
