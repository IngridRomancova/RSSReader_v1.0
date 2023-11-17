package eu.evropskyrozhled.h2database.service.model.article;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Model of RSS Channel Input.
 */
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "Channel")
@Entity
public class Channel {

  /**
   * Unique identifier of an article.
   */
  @Id
  @Column(name = "ident")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  /**
   * Title of a channel.
   */
  @Column(name = "title")
  String title;

  /**
   * Url link of a channel.
   */
  @Column(name = "link")
  String link;

  /**
   * Description link of a channel.
   */
  @Column(name = "description")
  String description;

  /**
   * Characterize state of a channel.
   */
  @Column(name = "active")
  boolean active;

  /**
   * Characterize validity of url.
   */
  @Column(name = "valid")
  boolean valid;

  /**
   * Last date of an actualization of a channel.
   */
  @Column(name = "updated_from")
  Date updatedFrom;

  /**
   * Number of unread articles.
   */
  @Column(name = "unread")
  long unread;
}
