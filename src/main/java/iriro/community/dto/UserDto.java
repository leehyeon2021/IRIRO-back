package iriro.community.dto;

import iriro.community.entity.ReplyEntity;
import iriro.community.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor // 롬복
public class UserDto {
    private Integer userId; // 회원번호
    private String email; // 아이디(이메일)
    private String pwToken; // 비밀번호
    private String nickName; // 닉네임
    private String createAt; // 가입일


    // 내가 쓴 글 목록 , 댓글 목록 담을 칸
    @Builder.Default
    private List<BoardDto> myBoards = new ArrayList<>();
    @Builder.Default
    private List<ReplyDto> myReplies = new ArrayList<>();

    // + Dto --> Entity 변환 // 사용자가 직접 입력한 데이터만.
    public UserEntity toEntity(){
        return UserEntity.builder()
                .email(this.email)
                .pwToken(this.pwToken)
                .nickname(this.nickName)
                .build();
    }
}
