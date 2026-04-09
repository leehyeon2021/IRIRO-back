package iriro.article.entity;

import iriro.article.dto.ArticleDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "article")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ArticleEntity extends BaseTime{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer articleId;
    @Column(length = 100, nullable = false)
    private String articleTitle;
    @Column(length = 20)
    private String articleDate;
    @Column(columnDefinition = "text")
    private String articleContent;
    @Column(columnDefinition = "text")
    private String articleUrl;
    @Column(length = 20)
    private String articleWriter;
    @Column(columnDefinition = "text")
    private String articlePic;
    @Column(length = 10)
    private String articleSite;
    @Column(columnDefinition = "text")
    private String articleKeyword;
    @Column(length = 10)
    private String articleDistrict;

    public ArticleDto toDto(){
        return ArticleDto.builder()
                .articleId(this.articleId)
                .articleTitle(this.articleTitle)
                .articleDate(this.articleDate)
                .articleContent(this.articleContent)
                .articleUrl(this.articleUrl)
                .articleWriter(this.articleWriter)
                .articlePic(this.articlePic)
                .articleSite(this.articleSite)
                .articleKeyword(this.articleKeyword)
                .articleDistrict(this.articleDistrict)
                .articleCreatedAt(getArticleCreatedAt().toString())
                .build();
    }
}
