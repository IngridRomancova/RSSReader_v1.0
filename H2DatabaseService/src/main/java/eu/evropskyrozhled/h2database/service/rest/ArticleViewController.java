package eu.evropskyrozhled.h2database.service.rest;


import static eu.evropskyrozhled.h2database.service.model.Constants.Rest.Endpoints.ARTICLE_VIEW;

import eu.evropskyrozhled.h2database.service.helper.CommonHelper;
import eu.evropskyrozhled.h2database.service.impl.ArticleService;
import eu.evropskyrozhled.h2database.service.impl.ArticleServiceImpl;
import eu.evropskyrozhled.h2database.service.model.article.Article;
import eu.evropskyrozhled.h2database.service.model.article.ArticleView;
import eu.evropskyrozhled.h2database.service.model.article.Channel;
import eu.evropskyrozhled.h2database.service.repository.ArticleRepository;
import eu.evropskyrozhled.h2database.service.repository.ArticleViewRepository;
import eu.evropskyrozhled.h2database.service.repository.ChannelRepository;
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
 * Rest Controller for Article View.
 */
@RestController
@RequestMapping(ARTICLE_VIEW)
public class ArticleViewController {

  private final ArticleViewRepository articleViewRepository;
  private final ArticleRepository articleRepository;
  private final ChannelRepository channelRepository;
  private final ArticleService articleService;

  /**
   * Constructor for ArticleViewController.
   *
   * @param articleViewRepository creates connection to articleView repository
   * @param articleRepository     creates connection to article repository
   * @param articleService        creates connection to article service
   * @param channelRepository     creates connection to channel repository
   */
  @Autowired
  public ArticleViewController(ArticleViewRepository articleViewRepository,
      ArticleRepository articleRepository,
      ArticleServiceImpl articleService,
      ChannelRepository channelRepository) {
    this.articleViewRepository = articleViewRepository;
    this.articleRepository = articleRepository;
    this.articleService = articleService;
    this.channelRepository = channelRepository;
  }

  final Sort sortByDateAndTitle =
      Sort.by(Sort.Direction.DESC, "date")
          .and(Sort.by(Sort.Direction.ASC, "title"));

  /**
   * Get Articles from ArticleView for all channels, pageable.
   *
   * @param page number
   * @param size number of articles on a page
   * @return ResponseEntity with information about articles
   */
  @GetMapping()
  public ResponseEntity<Map<String, Object>> getArticleView(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size
  ) {
    try {
      List<ArticleView> articleViews;
      Pageable paging = PageRequest.of(page, size).withSort(sortByDateAndTitle);

      Page<ArticleView> pageArticleViews = articleViewRepository.findAll(paging);
      articleViews = pageArticleViews.getContent();
      Map<String, Object> response = createResponse(articleViews, pageArticleViews);
      return new ResponseEntity<>(response, HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Get Articles from ArticleView for given channel, pageable.
   *
   * @param id   of a channel
   * @param page number
   * @param size number of articles on a page
   * @return ResponseEntity with information about articles
   */
  @GetMapping("/channel/{id}")
  public ResponseEntity<Map<String, Object>> getChannelArticles(
      @PathVariable Long id,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size
  ) {
    try {
      Pageable paging = PageRequest.of(page, size).withSort(sortByDateAndTitle);

      Page<ArticleView> pageArticleViews = articleViewRepository.findByChannelId(id, paging);
      List<ArticleView> articleViews = pageArticleViews.getContent();
      Map<String, Object> response = createResponse(articleViews, pageArticleViews);
      return new ResponseEntity<>(response, HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Start update of a given channel. Get new articles from ArticleView, pageable.
   *
   * @param id   of a channel
   * @param page number
   * @param size number of articles on a page
   * @return ResponseEntity with information about articles
   */
  @PutMapping("/updateChannel/{id}")
  public ResponseEntity<Map<String, Object>> updateChannel(
      @PathVariable Long id,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size
  ) {
    Channel channel = channelRepository.findById(id).orElseThrow(RuntimeException::new);

    try {
      articleService.downloadAndSavedNewArticles(channel);

      Pageable paging = PageRequest.of(page, size).withSort(sortByDateAndTitle);
      Page<ArticleView> pageArticleViews = articleViewRepository.findByChannelId(id, paging);
      List<ArticleView> articleViews = pageArticleViews.getContent();
      Map<String, Object> response = createResponse(articleViews, pageArticleViews);
      return new ResponseEntity<>(response, HttpStatus.OK);
    } catch (Exception e) {
      channel.setValid(false);
      channelRepository.save(channel);
      return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
    }
  }

  /**
   * Start update of all channels. Get new articles from ArticleView, pageable.
   *
   * @param page number
   * @param size number of articles on a page
   * @return ResponseEntity with information about articles
   */
  @PutMapping("/updateAll")
  public ResponseEntity<Map<String, Object>> updateAllChannels(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size
  ) {
    articleService.scheduleUpdateAll();

    Pageable paging = PageRequest.of(page, size).withSort(sortByDateAndTitle);
    Page<ArticleView> pageArticleViews = articleViewRepository.findAll(paging);
    List<ArticleView> articleViews = pageArticleViews.getContent();
    Map<String, Object> response = createResponse(articleViews, pageArticleViews);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  /**
   * Show saved articles from ArticleView, pageable.
   *
   * @param page number
   * @param size number of articles on a page
   * @return ResponseEntity with information about articles
   */
  @GetMapping("/showSaved")
  public ResponseEntity<Map<String, Object>> getSavedArticles(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size
  ) {
    try {
      Pageable paging = PageRequest.of(page, size).withSort(sortByDateAndTitle);
      Page<ArticleView> pageArticleViews = articleViewRepository.findBySaved(true, paging);
      List<ArticleView> articleViews = pageArticleViews.getContent();
      Map<String, Object> response = createResponse(articleViews, pageArticleViews);
      return new ResponseEntity<>(response, HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Show favourite articles from ArticleView, pageable.
   *
   * @param page number
   * @param size number of articles on a page
   * @return ResponseEntity with information about articles
   */
  @GetMapping("/showFavourites")
  public ResponseEntity<Map<String, Object>> getFavouriteArticles(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size
  ) {
    try {
      Pageable paging = PageRequest.of(page, size).withSort(sortByDateAndTitle);
      Page<ArticleView> pageArticleViews = articleViewRepository.findByFavourite(true, paging);
      List<ArticleView> articleViews = pageArticleViews.getContent();
      Map<String, Object> response = createResponse(articleViews, pageArticleViews);
      return new ResponseEntity<>(response, HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Show articles that contain search string in some of their field, pageable.
   *
   * @param page number
   * @param size number of articles on a page
   * @return ResponseEntity with information about articles
   */
  @GetMapping("searchArticles/{searchValue}")
  public ResponseEntity<Map<String, Object>> getArticles(
      @PathVariable String searchValue,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size
  ) {
    try {
      Pageable paging = PageRequest.of(page, size).withSort(sortByDateAndTitle);
      Page<ArticleView> pageArticleViews = articleViewRepository.findAll(
          CommonHelper.textInAllColumns(searchValue), paging);
      List<ArticleView> articleViews = pageArticleViews.getContent();
      Map<String, Object> response = createResponse(articleViews, pageArticleViews);
      return new ResponseEntity<>(response, HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Remove all articles, including the saved ones.
   *
   * @return ResponseEntity with status ok
   */
  @DeleteMapping("/removeAll")
  public ResponseEntity<ArticleView> removeAll() {
    articleService.deleteAllArticles();
    return ResponseEntity.ok().build();
  }

  /**
   * Remove all unsaved articles, pageable.
   *
   * @param page number
   * @param size number of articles on a page
   * @return ResponseEntity with information about articles
   */
  @DeleteMapping("/removeAllUnsaved")
  public ResponseEntity<Map<String, Object>> removeAllUnsaved(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size
  ) {
    try {
      articleService.deleteAllUnsavedArticles();

      Pageable paging = PageRequest.of(page, size).withSort(sortByDateAndTitle);
      Page<ArticleView> pageArticleViews = articleViewRepository.findAll(paging);
      List<ArticleView> articleViews = pageArticleViews.getContent();
      Map<String, Object> response = createResponse(articleViews, pageArticleViews);
      return new ResponseEntity<>(response, HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

  }

  /**
   * Handle read articles, pageable.
   *
   * @param id          of an article
   * @param articleView contains information about channel, article etc.
   * @return ResponseEntity with information about articles
   */
  @PutMapping("/clicked/{id}")
  public ResponseEntity<ArticleView> updateArticle(@PathVariable Long id,
      @RequestBody ArticleView articleView) {
    articleService.updateReadArticle(articleView, id);
    ArticleView currentArticleView = articleViewRepository.findByArticleId(id);
    return ResponseEntity.ok(currentArticleView);
  }

  /**
   * Set that article is favourite.
   *
   * @param id      of an article
   * @param article view with information about article
   * @return ResponseEntity with status ok
   */
  @PutMapping("/favourite/{id}")
  public ResponseEntity<ArticleView> favouriteArticle(@PathVariable Long id,
      @RequestBody ArticleView article) {
    Article currentArticle = articleRepository.findById(id).orElseThrow(RuntimeException::new);
    currentArticle.setFavourite(!currentArticle.isFavourite());
    articleRepository.save(currentArticle);
    ArticleView currentArticleView = articleViewRepository.findByArticleId(id);
    return ResponseEntity.ok(currentArticleView);
  }

  /**
   * Set that article is saved.
   *
   * @param id      of an article
   * @param article view with information about article
   * @return ResponseEntity with ok status.
   */
  @PutMapping("/saved/{id}")
  public ResponseEntity<ArticleView> saveArticle(@PathVariable Long id,
      @RequestBody ArticleView article) {
    Article currentArticle = articleRepository.findById(id).orElseThrow(RuntimeException::new);
    currentArticle.setSaved(!currentArticle.isSaved());
    articleRepository.save(currentArticle);
    ArticleView currentArticleView = articleViewRepository.findByArticleId(id);
    return ResponseEntity.ok(currentArticleView);
  }

  /**
   * Remove article from database.
   *
   * @param id of an article.
   * @return ResponseEntity with ok status
   */
  @DeleteMapping("/remove/{id}")
  public ResponseEntity<ArticleView> removeArticle(@PathVariable Long id) {
    articleService.removeArticle(id);
    return ResponseEntity.ok().build();
  }

  private Map<String, Object> createResponse(final List<ArticleView> articleViews,
      final Page<ArticleView> pageArticleViews) {
    Map<String, Object> response = new HashMap<>();
    response.put("articleViews", articleViews);
    response.put("currentPage", pageArticleViews.getNumber());
    response.put("totalItems", pageArticleViews.getTotalElements());
    response.put("totalPages", pageArticleViews.getTotalPages());
    return response;
  }
}
