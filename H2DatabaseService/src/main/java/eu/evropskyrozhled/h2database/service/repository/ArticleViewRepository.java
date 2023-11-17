package eu.evropskyrozhled.h2database.service.repository;


import eu.evropskyrozhled.h2database.service.model.article.ArticleView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * JPA access to ArticleView data sources.
 */
@Repository
public interface ArticleViewRepository extends JpaRepository<ArticleView, Long> {

  ArticleView findByArticleId(long articleId);

  Page<ArticleView> findByChannelId(Long channelId, Pageable pageable);

  Page<ArticleView> findBySaved(boolean saved, Pageable pageable);

  Page<ArticleView> findByFavourite(boolean favourite, Pageable pageable);

  Page<ArticleView> findAll(Specification<ArticleView> spec, Pageable pageable);

}
