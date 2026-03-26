package iriro.community.entity;

import iriro.common.entity.BaseTime;
import iriro.community.dto.BoardDto;
import iriro.saferoute.entity.LocationlogEntity;
import jakarta.persistence.*;
import lombok.*;
import org.openqa.selenium.devtools.Reply;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table( name = "board")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoardEntity extends BaseTime {
    @Id @GeneratedValue( strategy = GenerationType.IDENTITY)
    private Integer boardId;

    @Column( nullable = false , length =100 )
    private String boardTitle;

    @Column( columnDefinition = "longtext" )
    private String boardContent;

    @Column( columnDefinition = "integer default 0")
    private Integer recommendCount;

    // 회원 번호(FK)
    @ManyToOne(fetch = FetchType.LAZY) // 성능 방어! 즉시로딩 말고 지연로딩으로
    @JoinColumn( name = "user_id")
    private UserEntity userEntity;

    // 로그 번호
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn( name = "log_id")
    private LocationlogEntity locationlogEntity;

    // 양방향 @OneToMany
    // 1:N 일대다 ,  보드 1개에는 여러 개의 댓글이 포함된다.
    @OneToMany(mappedBy = "boardEntity", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    @ToString.Exclude
    private List<ReplyEntity> replyList = new ArrayList<>();

    // Entity --> Dto 변환
    public BoardDto toDto(){
        return BoardDto.builder()
                .boardId(this.boardId)
                .userId(this.userEntity != null ? this.userEntity.getUserId() : null)
                // userEntity가 비어있지 않으면 아이디를 꺼내고, 비어있으면 null을 넣어라!
                .logId(this.locationlogEntity.getLogId())
                .boardTitle(this.boardTitle)
                .boardContent(this.boardContent)
                .recommendCount(this.recommendCount)
                .createdAt(this.getCreatedAt().toString())
                .updatedAt(this.getUpdatedAt().toString())
                .build();

    }

}
