package eu.evropskyrozhled.h2database.service.repository;


import eu.evropskyrozhled.h2database.service.model.article.Channel;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * JPA access to Channel data sources.
 */
@Repository
public interface ChannelRepository extends JpaRepository<Channel, Long> {

  @Modifying
  @Transactional
  @Query("update Channel ch set ch.unread = :unread")
  void updateUnread(@Param(value = "unread") long unread);
}
