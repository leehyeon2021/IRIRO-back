package iriro.community.entity;

import iriro.common.entity.BaseTime;
import iriro.community.dto.UserDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Entity
@Table( name = "user")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class UserEntity extends BaseTime {
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    private Integer userId;

    @Column(nullable = false , length = 20 , unique = true )
    private String email;

    @Column(nullable = false , length = 20 )
    private String pwToken;

    @Column( nullable = false , length = 20 , unique = true )
    private String nickName;

    // Entity --> Dto 변환함수
    public UserDto userDto(){
        return UserDto.builder()
                .userId(this.userId)
                .email(this.email)
                // 비밀번호는 소중하니까 불포함한다.
                .nickName(this.nickName)
                .createAt( this.getCreatedAt().toString())
                .updateAt( this.getUpdatedAt().toString())
                .build();
    }

}
