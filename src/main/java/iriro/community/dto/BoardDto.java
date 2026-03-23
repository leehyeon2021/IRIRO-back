package iriro.community.dto;

import iriro.community.entity.BoardEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @AllArgsConstructor @NoArgsConstructor // 롬복
public class BoardDto {
    private Integer boardId;
    private Integer userId;
    private Integer logId;
    private String boardTitle;
    private String boardContent;
    private Integer recommendCount;
    private String createdAt;
    private String updatedAt;


    // + Dto --> Entity 변환
    public BoardEntity boardEntity(){
        return BoardEntity.builder()
                .boardTitle(this.boardTitle)
                .boardContent(this.boardContent)
                .recommendCount(this.recommendCount)
                .build();
    }
}
