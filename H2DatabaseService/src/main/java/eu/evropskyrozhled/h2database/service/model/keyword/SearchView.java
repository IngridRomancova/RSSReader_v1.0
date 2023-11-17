package eu.evropskyrozhled.h2database.service.model.keyword;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serial;
import java.io.Serializable;
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
@Table(name = "Search_view")
public class SearchView implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;

  /**
   * Unique identifier of a keyword.
   */
  @Id
  @Column(name = "ident")
  Long id;

  /**
   * Name of a keyword.
   */
  @Column(name = "keyword")
  String keyword;


  /**
   * Characterize state of a keyword.
   */
  @Column(name = "active")
  boolean active;

  /**
   * Characterize number of unread articles under the keyword.
   */
  @Column(name = "unread")
  long unread;
}
