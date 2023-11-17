package eu.evropskyrozhled.h2database.service.repository;


import eu.evropskyrozhled.h2database.service.model.keyword.KeywordJoin;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * JPA access to KeywordJoin data sources.
 */
@Repository
public interface KeywordJoinRepository extends JpaRepository<KeywordJoin, Long> {

  List<KeywordJoin> findByArticleId(long articleId);

}
