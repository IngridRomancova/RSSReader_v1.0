package eu.evropskyrozhled.h2database.service.repository;


import eu.evropskyrozhled.h2database.service.model.keyword.KeywordView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * JPA access to KeywordView data sources.
 */
@Repository
public interface KeywordViewRepository extends JpaRepository<KeywordView, Long> {

  Page<KeywordView> findByKeywordIdAndInvisible(Long keywordId, boolean invisible,
      Pageable pageable);

  Page<KeywordView> findByInvisible(boolean invisible, Pageable pageable);

  Page<KeywordView> findBySavedAndInvisible(boolean saved, boolean invisible, Pageable pageable);

  Page<KeywordView> findByFavouriteAndInvisible(boolean favourite, boolean invisible,
      Pageable pageable);

  Page<KeywordView> findAll(Specification<KeywordView> spec, Pageable pageable);
}
