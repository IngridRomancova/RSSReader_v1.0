package eu.evropskyrozhled.h2database.service.model.keyword;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import java.util.Date;
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
@Table(name = "Keyword")
@Entity
public class Keyword {

  /**
   * Unique identifier of a keyword.
   */
  @Id
  @Column(name = "ident")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  /**
   * Name of a keyword.
   */
  @Column(name = "keyword")
  String keywordName;

  /**
   * Description of a keyword.
   */
  @Column(name = "description")
  String description;

  /**
   * Characterize state of a keyword.
   */
  @Column(name = "active")
  boolean active;

  /**
   * Byte array of tags.
   */
  @Lob
  @Column(length = 100000, name = "tags", nullable = false)
  private byte[] tags;

  /**
   * Characterize number of unread articles for a keyword.
   */
  @Column(name = "unread")
  long unread;

  /**
   * Last date of an actualization of a channel.
   */
  @Column(name = "updated_from")
  Date updatedFrom;
}
