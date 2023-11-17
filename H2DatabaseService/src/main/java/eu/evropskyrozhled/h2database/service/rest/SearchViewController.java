package eu.evropskyrozhled.h2database.service.rest;


import static eu.evropskyrozhled.h2database.service.model.Constants.Rest.Endpoints.SEARCH_VIEW;

import eu.evropskyrozhled.h2database.service.model.keyword.SearchView;
import eu.evropskyrozhled.h2database.service.repository.SearchViewRepository;
import jakarta.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Rest Controller for SearchView.
 */
@RestController
@Transactional
@RequestMapping(SEARCH_VIEW)
public class SearchViewController {

  private final SearchViewRepository searchViewRepository;

  @Autowired
  public SearchViewController(SearchViewRepository searchViewRepository) {
    this.searchViewRepository = searchViewRepository;
  }

  /**
   * Get Keywords from SearchView.
   *
   * @param page of SearchView that is sent to frontend
   * @param size of SearchView elements on a page
   * @return ResponseEntity with nested SearchView elements
   */
  @GetMapping
  public ResponseEntity<Map<String, Object>> getKeywords(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "15") int size
  ) {
    try {
      Pageable paging = PageRequest.of(page, size);
      Page<SearchView> pageSearchViews = searchViewRepository.findAll(paging);
      List<SearchView> searchViews = pageSearchViews.getContent();
      Map<String, Object> response = createResponse(searchViews, pageSearchViews);
      return new ResponseEntity<>(response, HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Search keyword names that contains search value in its name.
   *
   * @param searchValue that is searched in Keyword Names.
   * @param page        of SearchView that is sent to frontend
   * @param size        of SearchView elements on a page
   * @return ResponseEntity with nested SearchView elements
   */
  @GetMapping("searchKeywords/{searchValue}")
  public ResponseEntity<Map<String, Object>> getChannel(
      @PathVariable String searchValue,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "15") int size) {
    try {
      Pageable paging = PageRequest.of(page, size);
      Page<SearchView> pageSearchViews = searchViewRepository.findByKeywordContainingIgnoreCase(
          searchValue, paging);
      List<SearchView> searchViews = pageSearchViews.getContent();
      Map<String, Object> response = createResponse(searchViews, pageSearchViews);
      return new ResponseEntity<>(response, HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  private Map<String, Object> createResponse(final List<SearchView> searchViews,
      final Page<SearchView> pageSearchViews) {
    Map<String, Object> response = new HashMap<>();
    response.put("searchViews", searchViews);
    response.put("currentPage", pageSearchViews.getNumber());
    response.put("totalItems", pageSearchViews.getTotalElements());
    response.put("totalPages", pageSearchViews.getTotalPages());
    return response;
  }

}
