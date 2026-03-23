package iriro.community.entity;

import iriro.common.entity.BaseTime;
import iriro.community.dto.BoardDto;
import iriro.saferoute.entity.LocationlogEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    @ManyToOne
    @JoinColumn( name = "user_id")
    private UserEntity userEntity;

    @ManyToOne
    @JoinColumn( name = "log_id")
    private LocationlogEntity LocationlogEntity;

    // Entity --> Dto 변환
    public BoardDto boardDto(){
        return BoardDto.builder()
                .boardId(this.boardId)
                .boardTitle(this.boardTitle)
                .boardContent(this.boardContent)
                .recommendCount(this.recommendCount)
                .createdAt(this.getCreatedAt().toString())
                .updatedAt(this.getUpdatedAt().toString())
                .build();
    }

}
