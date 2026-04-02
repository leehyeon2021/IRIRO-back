package iriro.article.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ArticleDto {
    private Integer articleId;
    private String articleTitle;
    private String articleDate;
    private String articleContent;
    private String articleUrl;
    private String articleWriter;
    private String articlePic;
    private String articleSite;
    private String articleKeyword;
}
