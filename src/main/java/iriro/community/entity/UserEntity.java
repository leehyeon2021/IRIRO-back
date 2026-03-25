package iriro.community.entity;

import iriro.common.entity.BaseTime;
import iriro.community.dto.UserDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    // Entity --> Dto 변환함수 // 생고기에서 플레이팅 접시용으로 바꾸는 거야~
    public UserDto toDto(){
        return UserDto.builder()
                .userId(this.userId)
                .email(this.email)
                // 비밀번호는 소중하니까 불포함한다.
                .nickName(this.nickname)
                .createAt( this.getCreatedAt().toString())
                .updateAt( this.getUpdatedAt().toString())
                .build();
    }

}
