package eu.evropskyrozhled.h2database.service.repository;


import eu.evropskyrozhled.h2database.service.model.keyword.Keyword;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * JPA access to Keyword data sources.
 */
@Repository
public interface KeywordRepository extends JpaRepository<Keyword, Long> {

  @Modifying
  @Transactional
  @Query("update Keyword k set k.unread = :unread")
  void updateUnread(@Param(value = "unread") long unread);
}
