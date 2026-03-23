package iriro.community.entity;

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

public class UserEntity {
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    private Integer userId;

    @Column( nullable = false , length = 20 , unique = true )
    private String nickName;

}
