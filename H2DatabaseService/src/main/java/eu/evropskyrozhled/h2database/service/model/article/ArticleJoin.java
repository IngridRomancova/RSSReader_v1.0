package eu.evropskyrozhled.h2database.service.model.article;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Model of RSS Article View.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "Article_join")
@Entity
public class ArticleJoin {

  /**
   * Unique identifier of a joiner between channels and articles.
   */
  @Id
  @Column(name = "ident")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;


  /**
   * Article ident.
   */
  @Column(name = "article_ident")
  Long articleId;


  /**
   * Channel ident.
   */
  @Column(name = "channel_ident")
  Long channelId;


  public ArticleJoin(Long articleId, Long channelId) {
    this.articleId = articleId;
    this.channelId = channelId;
  }
}
