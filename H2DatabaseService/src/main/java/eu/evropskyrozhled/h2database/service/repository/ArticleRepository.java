package eu.evropskyrozhled.h2database.service.repository;

import eu.evropskyrozhled.h2database.service.model.article.Article;
import java.util.Date;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * JPA access to Article data sources.
 */
@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {

  List<Article> findByChannel(long channel);

  long countByChannelAndClicked(long channel, boolean clicked);

  long countByIdInAndClicked(List<Long> ids, boolean clicked);

  List<Article> findByDateAfter(Date date);
}
