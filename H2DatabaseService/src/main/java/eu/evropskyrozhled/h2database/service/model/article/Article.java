package eu.evropskyrozhled.h2database.service.model.article;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.transaction.Transactional;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * This table represents rss feeds for all articles.
 */
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Transactional
@Table(name = "Article", uniqueConstraints = @UniqueConstraint(columnNames = {"title"}))
public class Article {

  /**
   * Unique identifier of an article.
   */
  @Id
  @Column(name = "ident")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

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
   * Title of an article.
   */
  @Column(name = "title")
  String title;

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
   * Indicates whether the article has been read.
   */
  @Column(name = "clicked")
  boolean clicked;

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

  /**
   * Reference to Channel entity.
   */
  @Column(name = "channel")
  Long channel;

  /**
   * Indicates whether the article was archived.
   */
  @Column(name = "archived")
  boolean archived;

}
