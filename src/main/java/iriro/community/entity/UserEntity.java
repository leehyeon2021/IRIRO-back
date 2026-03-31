package iriro.community.entity;

import iriro.common.entity.BaseTime;
import iriro.community.dto.UserDto;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Entity
@Table( name = "users")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

// email, pwToken, nickname 길이 너무 짧아서 늘림, nickName x nickname으로 해야함, users X, user O
public class UserEntity extends BaseTime {
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    private Integer userId;

    @Column(nullable = false , length = 40 , unique = true )
    private String email;

    @Column(nullable = false , length = 60 )
    private String pwToken;

    @Column( nullable = false , length = 40 , unique = true )
    private String nickname;


    // 1:N 일대다
        // 유저 1명에는 여러 개의 보드가 포함된다.
    @OneToMany(mappedBy = "userEntity",cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    @ToString.Exclude
    private List<BoardEntity> boardList = new ArrayList<>();

    // 유저 1명에는 여러 개의 리플이 포함된다.
    @OneToMany(mappedBy = "userEntity",cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    @ToString.Exclude
    private List<ReplyEntity> replyList = new ArrayList<>();



    // Entity --> Dto 변환함수 // 생고기에서 플레이팅 접시용으로 바꾸는 거야~
    public UserDto toDto(){
        return UserDto.builder()
                .userId(this.userId)
                .email(this.email)
                // 비밀번호는 소중하니까 불포함한다.
                .nickName(this.nickname)
                .myBoards(this.boardList.stream().map(BoardEntity::toDto).collect(Collectors.toList()))
                .myReplies(this.replyList.stream().map(ReplyEntity::toDto).collect(Collectors.toList()))
                .createAt( this.getCreatedAt().toString())
                .updateAt( this.getUpdatedAt().toString())
                .build();
    }

}
