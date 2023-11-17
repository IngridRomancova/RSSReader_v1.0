package eu.evropskyrozhled.h2database.service.model.keyword;

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
 * Model of RSS Keyword Input.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "Keyword_join")
@Entity
public class KeywordJoin {

  /**
   * Unique identifier of a joiner between keywords and articles.
   */
  @Id
  @Column(name = "ident")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  /**
   * Keyword ident.
   */
  @Column(name = "keyword_ident")
  Long keywordId;

  /**
   * Article ident.
   */
  @Column(name = "article_ident")
  Long articleId;

  /**
   * Indicates whether the finding is visible.
   */
  @Column(name = "invisible")
  boolean invisible;

  /**
   * Channel ident.
   */
  @Column(name = "channel_ident")
  Long channelId;


  /**
   * Custom constructor.
   *
   * @param keywordId id on a Keyword table
   * @param articleId id in an Article table
   * @param channelId id in a Channel table
   */
  public KeywordJoin(Long keywordId, Long articleId, Long channelId) {
    this.keywordId = keywordId;
    this.articleId = articleId;
    this.channelId = channelId;
  }
}
