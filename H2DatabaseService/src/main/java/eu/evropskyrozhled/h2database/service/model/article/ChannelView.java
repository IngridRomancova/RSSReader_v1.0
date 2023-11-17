package eu.evropskyrozhled.h2database.service.model.article;

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
 * This view represents channel view.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Data
@Immutable
@Entity
@Table(name = "Channel_view")
public class ChannelView implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;

  /**
   * Unique identifier of an article.
   */
  @Id
  @Column(name = "ident")
  Long id;

  /**
   * Title of a channel.
   */
  @Column(name = "title")
  String title;


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
   * Characterize number of unread articles under the channel.
   */
  @Column(name = "unread")
  long unread;


}
