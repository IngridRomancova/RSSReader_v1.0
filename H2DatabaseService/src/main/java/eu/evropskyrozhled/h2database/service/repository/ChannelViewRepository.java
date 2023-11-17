package eu.evropskyrozhled.h2database.service.repository;


import eu.evropskyrozhled.h2database.service.model.article.ChannelView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * JPA access to ChannelView data sources.
 */
@Repository
public interface ChannelViewRepository extends JpaRepository<ChannelView, Long> {

  Page<ChannelView> findByTitleContainingIgnoreCase(String title, Pageable pageable);

}
