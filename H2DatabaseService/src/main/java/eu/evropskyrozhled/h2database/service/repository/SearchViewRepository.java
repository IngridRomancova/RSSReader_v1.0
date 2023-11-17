package eu.evropskyrozhled.h2database.service.repository;


import eu.evropskyrozhled.h2database.service.model.keyword.SearchView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * JPA access to SearchView data sources.
 */
@Repository
public interface SearchViewRepository extends JpaRepository<SearchView, Long> {

  Page<SearchView> findByKeywordContainingIgnoreCase(String keyword, Pageable pageable);

}
