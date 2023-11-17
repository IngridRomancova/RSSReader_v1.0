package eu.evropskyrozhled.h2database.service.repository;


import eu.evropskyrozhled.h2database.service.model.article.ArticleJoin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * JPA access to ArticleJoin data sources.
 */
@Repository
public interface ArticleJoinRepository extends JpaRepository<ArticleJoin, Long> {

}
