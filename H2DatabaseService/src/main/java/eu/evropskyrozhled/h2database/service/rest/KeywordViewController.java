package eu.evropskyrozhled.h2database.service.rest;


import static eu.evropskyrozhled.h2database.service.model.Constants.Rest.Endpoints.KEYWORD_VIEW;

import eu.evropskyrozhled.h2database.service.helper.CommonHelper;
import eu.evropskyrozhled.h2database.service.impl.ArticleService;
import eu.evropskyrozhled.h2database.service.impl.ArticleServiceImpl;
import eu.evropskyrozhled.h2database.service.impl.KeywordService;
import eu.evropskyrozhled.h2database.service.impl.KeywordServiceImpl;
import eu.evropskyrozhled.h2database.service.impl.KeywordViewService;
import eu.evropskyrozhled.h2database.service.impl.KeywordViewServiceImpl;
import eu.evropskyrozhled.h2database.service.model.keyword.KeywordView;
import eu.evropskyrozhled.h2database.service.repository.KeywordViewRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Rest Controller for a KeywordView.
 */
@RestController
@RequestMapping(KEYWORD_VIEW)
public class KeywordViewController {


  private final KeywordViewRepository keywordViewRepository;

  private final KeywordViewService keywordViewService;

  private final ArticleService articleService;

  private final KeywordService keywordService;


  /**
   * Constructor for KeywordViewController.
   *
   * @param keywordViewRepository creates connection to keywordView repository
   * @param keywordViewService    creates connection to keywordViewService
   * @param articleService        creates connection to article service
   * @param keywordService        creates connection to keyword service
   */
  @Autowired
  public KeywordViewController(KeywordViewRepository keywordViewRepository,
      KeywordViewServiceImpl keywordViewService,
      ArticleServiceImpl articleService,
      KeywordServiceImpl keywordService) {
    this.keywordViewRepository = keywordViewRepository;
    this.keywordViewService = keywordViewService;
    this.articleService = articleService;
    this.keywordService = keywordService;
  }

  final Sort sortByDateAndTitle =
      Sort.by(Sort.Direction.DESC, "date")
          .and(Sort.by(Sort.Direction.ASC, "title"));

  /**
   * Get KeywordView, pageable.
   *
   * @param page of KeywordView that is sent to frontend
   * @param size of keywordView elements on a page
   * @return ResponseEntity with nested KeywordView elements
   */
  @GetMapping()
  public ResponseEntity<Map<String, Object>> getKeywordView(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size
  ) {
    try {
      Pageable paging = PageRequest.of(page, size).withSort(sortByDateAndTitle);
      Page<KeywordView> pageKeywordViews = keywordViewRepository.findByInvisible(false, paging);
      List<KeywordView> keywordViews = pageKeywordViews.getContent();
      Map<String, Object> response = createResponse(keywordViews, pageKeywordViews);
      return new ResponseEntity<>(response, HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Show articles for given keyword, pageable.
   *
   * @param id   of a keyword
   * @param page of KeywordView that is sent to frontend
   * @param size of keywordView elements on a page
   * @return ResponseEntity with nested KeywordView elements
   */
  @GetMapping("/keyword/{id}")
  public ResponseEntity<Map<String, Object>> getKeywordArticles(
      @PathVariable Long id,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size
  ) {
    try {
      Pageable paging = PageRequest.of(page, size).withSort(sortByDateAndTitle);
      Page<KeywordView> pageKeywordViews = keywordViewRepository.findByKeywordIdAndInvisible(id,
          false, paging);
      List<KeywordView> keywordViews = pageKeywordViews.getContent();
      Map<String, Object> response = createResponse(keywordViews, pageKeywordViews);
      return new ResponseEntity<>(response, HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Update read status of an article and number of unread articles for the same channel. Pageable.
   *
   * @param id          of KeywordView
   * @param keywordView with information about read/unread article
   * @return ResponseEntity with nested KeywordView
   */
  @PutMapping("/clicked/{id}")
  public ResponseEntity<KeywordView> updateArticle(@PathVariable Long id,
      @RequestBody KeywordView keywordView) {
    keywordViewService.updateArticle(id, keywordView);
    return ResponseEntity.ok(keywordView);
  }

  /**
   * Find new articles for given keyword. Pageable.
   *
   * @param id   of a keyword
   * @param page of KeywordView that is sent to frontend
   * @param size of keywordView elements on a page
   * @return ResponseEntity with nested KeywordView elements
   */
  @PutMapping("/refresh/{id}")
  public ResponseEntity<Map<String, Object>> getNewArticlesForKeyword(
      @PathVariable Long id,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size

  ) {
    try {
      keywordService.downloadAndSaveNewArticles(id);

      Pageable paging = PageRequest.of(page, size).withSort(sortByDateAndTitle);
      Page<KeywordView> pageKeywordViews = keywordViewRepository.findByKeywordIdAndInvisible(id,
          false, paging);
      List<KeywordView> keywordViews = pageKeywordViews.getContent();
      Map<String, Object> response = createResponse(keywordViews, pageKeywordViews);
      return new ResponseEntity<>(response, HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Find new articles for all keywords. Pageable.
   *
   * @param page of KeywordView that is sent to frontend
   * @param size of keywordView elements on a page
   * @return ResponseEntity with nested KeywordView elements
   */
  @PutMapping("/filterAll")
  public ResponseEntity<Map<String, Object>> getArticlesForAllKeywords(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size
  ) {
    try {
      keywordService.searchNewArticles();

      Pageable paging = PageRequest.of(page, size).withSort(sortByDateAndTitle);
      Page<KeywordView> pageKeywordViews = keywordViewRepository.findByInvisible(false, paging);
      List<KeywordView> keywordViews = pageKeywordViews.getContent();
      Map<String, Object> response = createResponse(keywordViews, pageKeywordViews);
      return new ResponseEntity<>(response, HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Show saved articles for keywords. Pageable.
   *
   * @param page of KeywordView that is sent to frontend
   * @param size of keywordView elements on a page
   * @return ResponseEntity with nested KeywordView elements
   */
  @GetMapping("/showSaved")
  public ResponseEntity<Map<String, Object>> getSavedArticles(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size
  ) {
    try {
      Pageable paging = PageRequest.of(page, size).withSort(sortByDateAndTitle);

      Page<KeywordView> pageKeywordViews = keywordViewRepository.findBySavedAndInvisible(true,
          false, paging);
      List<KeywordView> keywordViews = pageKeywordViews.getContent();
      Map<String, Object> response = createResponse(keywordViews, pageKeywordViews);
      return new ResponseEntity<>(response, HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Show favourites articles for keywords. Pageable.
   *
   * @param page of KeywordView that is sent to frontend
   * @param size of keywordView elements on a page
   * @return ResponseEntity with nested KeywordView elements
   */
  @GetMapping("/showFavourites")
  public ResponseEntity<Map<String, Object>> getFavouritesArticles(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size
  ) {
    try {
      Pageable paging = PageRequest.of(page, size).withSort(sortByDateAndTitle);
      Page<KeywordView> pageKeywordViews = keywordViewRepository.findByFavouriteAndInvisible(
          true, false, paging);
      List<KeywordView> keywordViews = pageKeywordViews.getContent();
      Map<String, Object> response = createResponse(keywordViews, pageKeywordViews);
      return new ResponseEntity<>(response, HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Show invisible articles for keywords. Pageable.
   *
   * @param page of KeywordView that is sent to frontend
   * @param size of keywordView elements on a page
   * @return ResponseEntity with nested KeywordView elements
   */
  @GetMapping("/showInvisible")
  public ResponseEntity<Map<String, Object>> getInvisibleArticles(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size
  ) {
    try {
      Pageable paging = PageRequest.of(page, size).withSort(sortByDateAndTitle);
      Page<KeywordView> pageKeywordViews = keywordViewRepository.findByInvisible(true, paging);
      List<KeywordView> keywordViews = pageKeywordViews.getContent();
      Map<String, Object> response = createResponse(keywordViews, pageKeywordViews);
      return new ResponseEntity<>(response, HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Search article in filtered articles by searchValue. Pageable.
   *
   * @param searchValue that is searched in all columns of an Article table
   * @param page        of KeywordView that is sent to frontend
   * @param size        of keywordView elements on a page
   * @return ResponseEntity with nested KeywordView elements
   */
  @GetMapping("searchArticles/{searchValue}")
  public ResponseEntity<Map<String, Object>> getArticles(
      @PathVariable String searchValue,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size
  ) {
    try {
      Pageable paging = PageRequest.of(page, size).withSort(sortByDateAndTitle);
      Page<KeywordView> pageKeywordViews = keywordViewRepository.findAll(
          CommonHelper.textInAllColumns(searchValue), paging);
      List<KeywordView> keywordViews = pageKeywordViews.getContent();
      Map<String, Object> response = createResponse(keywordViews, pageKeywordViews);
      return new ResponseEntity<>(response, HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Set that article was saved.
   *
   * @param id          of KeywordView
   * @param keywordView with information about read/unread article
   * @return ResponseEntity with nested KeywordView
   */
  @PutMapping("/saved/{id}")
  public ResponseEntity<KeywordView> save(@PathVariable Long id,
      @RequestBody KeywordView keywordView) {
    KeywordView currentKeywordView = keywordViewRepository.findById(id)
        .orElseThrow(RuntimeException::new);
    articleService.setStatusSaved(currentKeywordView.getArticleId(), keywordView.isSaved());
    return ResponseEntity.ok(keywordView);
  }

  /**
   * Set that article is favourite.
   *
   * @param id          of KeywordView
   * @param keywordView with information about read/unread article
   * @return ResponseEntity with nested KeywordView
   */
  @PutMapping("/favourite/{id}")
  public ResponseEntity<KeywordView> favouriteJoin(@PathVariable Long id,
      @RequestBody KeywordView keywordView) {
    KeywordView currentKeywordView = keywordViewRepository.findById(id)
        .orElseThrow(RuntimeException::new);
    articleService.setStatusFavourite(currentKeywordView.getArticleId(), keywordView.isFavourite());
    return ResponseEntity.ok(keywordView);
  }

  /**
   * Set that article is invisible.
   *
   * @param id          of KeywordView
   * @param keywordView with information about read/unread article
   * @return ResponseEntity with nested KeywordView
   */
  @PutMapping("/invisible/{id}")
  public ResponseEntity<KeywordView> setInvisibleSearch(@PathVariable Long id,
      @RequestBody KeywordView keywordView) {
    KeywordView currentKeywordView = keywordViewRepository.findById(id)
        .orElseThrow(RuntimeException::new);
    keywordService.setInvisibleStatus(currentKeywordView.getId(), keywordView.isInvisible());
    return ResponseEntity.ok(keywordView);
  }

  /**
   * Remove all unsaved articles. Pageable.
   *
   * @param page of KeywordView that is sent to frontend
   * @param size of keywordView elements on a page
   * @return ResponseEntity with nested KeywordView elements
   */
  @DeleteMapping("/removeAllUnsaved")
  public ResponseEntity<Map<String, Object>> removeAllUnsaved(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size
  ) {
    try {
      articleService.deleteAllUnsavedArticles();

      Pageable paging = PageRequest.of(page, size).withSort(sortByDateAndTitle);
      Page<KeywordView> pageKeywordViews = keywordViewRepository.findByInvisible(false, paging);
      List<KeywordView> keywordViews = pageKeywordViews.getContent();
      Map<String, Object> response = createResponse(keywordViews, pageKeywordViews);
      return new ResponseEntity<>(response, HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }


  private Map<String, Object> createResponse(final List<KeywordView> keywordViews,
      final Page<KeywordView> pageKeywordViews) {
    Map<String, Object> response = new HashMap<>();
    response.put("keywordViews", keywordViews);
    response.put("currentPage", pageKeywordViews.getNumber());
    response.put("totalItems", pageKeywordViews.getTotalElements());
    response.put("totalPages", pageKeywordViews.getTotalPages());
    return response;
  }

}
