package eu.evropskyrozhled.h2database.service.model.keyword;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Immutable;

/**
 * This view represents rss feeds for all articles sorted by keywords.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Data
@Immutable
@Entity
@Table(name = "Keyword_view")
public class KeywordView implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;

  /**
   * Unique identifier of a keywordView.
   */
  @Id
  @Column(name = "ident")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  /**
   * Identifier of a group of tags from at least at one was localized in an article.
   */
  @Column(name = "keyword_ident")
  Long keywordId;

  /**
   * Group of tags from at least at one was localized in an article.
   */
  @Column(name = "keyword")
  String keyword;


  /**
   * Identifier of an article.
   */
  @Column(name = "article_ident")
  Long articleId;

  /**
   * Title of an article.
   */
  @Column(name = "title")
  String title;

  /**
   * Url of an article.
   */
  @Column(name = "link")
  String link;

  /**
   * Description of an article.
   */
  @Column(name = "description")
  String description;

  /**
   * Short uri of an article.
   */
  @Column(name = "uri")
  String uri;

  /**
   * Published date of an article.
   */
  @Column(name = "date")
  Date date;

  /**
   * Channel of an article.
   */
  @Column(name = "channel")
  String channel;

  /**
   * Channel ident.
   */
  @Column(name = "channel_ident")
  Long channelId;

  /**
   * Indicates whether the article has been read.
   */
  @Column(name = "clicked")
  boolean clicked;

  /**
   * Indicates whether the finding is visible.
   */
  @Column(name = "invisible")
  boolean invisible;

  /**
   * Indicates whether the article is favourite.
   */
  @Column(name = "favourite")
  boolean favourite;

  /**
   * Indicates whether the article was saved.
   */
  @Column(name = "saved")
  boolean saved;


}
