package eu.evropskyrozhled.h2database.service.impl;

import eu.evropskyrozhled.h2database.service.helper.CommonHelper;
import eu.evropskyrozhled.h2database.service.model.article.Article;
import eu.evropskyrozhled.h2database.service.model.keyword.Keyword;
import eu.evropskyrozhled.h2database.service.model.keyword.KeywordJoin;
import eu.evropskyrozhled.h2database.service.repository.ArticleRepository;
import eu.evropskyrozhled.h2database.service.repository.KeywordJoinRepository;
import eu.evropskyrozhled.h2database.service.repository.KeywordRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Keyword service for processing operations with keywords.
 */
@Service
public class KeywordServiceImpl implements KeywordService {

  private final KeywordRepository keywordRepository;
  private final KeywordJoinRepository keywordJoinRepository;
  private final ArticleRepository articleRepository;


  /**
   * Constructor for KeywordService.
   *
   * @param keywordRepository     creates connection to keyword repository
   * @param keywordJoinRepository creates connection to keywordJoin repository
   * @param articleRepository     creates connection to article repository
   */
  @Autowired
  public KeywordServiceImpl(KeywordRepository keywordRepository,
      KeywordJoinRepository keywordJoinRepository,
      ArticleRepository articleRepository) {
    this.keywordRepository = keywordRepository;
    this.keywordJoinRepository = keywordJoinRepository;
    this.articleRepository = articleRepository;
  }

  /**
   * Delete keyword and corresponding information.
   *
   * @param id of a keyword
   */
  @Override
  public void deleteKeyword(Long id) {
    keywordRepository.deleteById(id);
    deleteKeywordJoinByKeywordId(id);
  }

  /**
   * Delete keywordJoin by articleId.
   *
   * @param id of a channel
   */
  @Override
  public void deleteKeywordJoinByArticleId(Long id) {
    List<KeywordJoin> keywordJoins = keywordJoinRepository.findAll().stream()
        .filter(join -> join.getArticleId().equals(id)).toList();
    recalculateUnreadArticlesFromKeywordJoin(keywordJoins);
    keywordJoinRepository.deleteAll(keywordJoins);
  }

  /**
   * Delete keywordJoin by channelId.
   *
   * @param id of a channel
   */
  @Override
  public void deleteKeywordJoinByChannelId(Long id) {
    List<KeywordJoin> keywordJoins = keywordJoinRepository.findAll().stream()
        .filter(join -> join.getChannelId().equals(id)).toList();
    recalculateUnreadArticlesFromKeywordJoin(keywordJoins);
    keywordJoinRepository.deleteAll(keywordJoins);
  }

  /**
   * Get new articles and save its connections with keywords.
   *
   * @param id of a keyword.
   */
  @Override
  public void downloadAndSaveNewArticles(Long id) {
    Keyword keyword = keywordRepository.findById(id).orElseThrow(RuntimeException::new);

    if (keyword.isActive()) {
      List<Article> articlesWithMatch = getMatchedArticles(keyword);

      if (!articlesWithMatch.isEmpty()) {
        List<KeywordJoin> newKeywordJoins = getNewKeywordsJoinForSave(id, articlesWithMatch);
        keywordJoinRepository.saveAll(newKeywordJoins);

        keyword.setUnread(countUnreadArticlesForKeywords(keyword.getId()));

        Date lastUpdatedArticle = Collections.max(
            articlesWithMatch.stream().map(Article::getDate).toList());
        keyword.setUpdatedFrom(lastUpdatedArticle);
        keywordRepository.save(keyword);

      }
    }
  }

  /**
   * Recalculate unread articles for given keyword.
   */
  @Override
  public void recalculateUnreadArticles() {
    List<Long> keywordIdList = keywordRepository.findAll().stream().map(Keyword::getId).toList();

    keywordIdList.forEach(id -> {
          Keyword currentKeyword = keywordRepository.findById(id)
              .orElseThrow(RuntimeException::new);
          currentKeyword.setUnread(countUnreadArticlesForKeywords(id));
          keywordRepository.save(currentKeyword);
        }
    );
  }

  /**
   * Recalculate unread articles from article Id.
   *
   * @param id of an article
   */
  public void recalculateUnreadArticlesFromArticleId(Long id) {
    List<KeywordJoin> keywordJoins = keywordJoinRepository.findByArticleId(id);
    Map<Long, Long> result = keywordJoins.stream()
        .collect(Collectors.groupingByConcurrent(KeywordJoin::getKeywordId, Collectors.counting()));

    for (Map.Entry<Long, Long> entry : result.entrySet()) {
      Keyword keyword = keywordRepository.findById(entry.getKey())
          .orElseThrow(RuntimeException::new);
      keyword.setUnread(keyword.getUnread() - entry.getValue());
    }
  }

  /**
   * Search new articles.
   */
  @Override
  public void searchNewArticles() {
    List<Keyword> keywords = keywordRepository.findAll().stream().filter(Keyword::isActive)
        .toList();
    createKeywordJoinList(keywords);
  }

  /**
   * Set active/inactive status for a keyword.
   *
   * @param id of a keywords
   */
  @Override
  public void setActivationStatus(Long id, Keyword keyword) {
    Keyword currentKeyword = keywordRepository.findById(id)
        .orElseThrow(RuntimeException::new);
    currentKeyword.setActive(keyword.isActive());
    keywordRepository.save(currentKeyword);
  }

  /**
   * Set invisible/visible status for keyword join.
   *
   * @param id          of a keywordJoin
   * @param isInvisible true if it is invisible
   */
  @Override
  public void setInvisibleStatus(Long id, boolean isInvisible) {
    KeywordJoin currentKeywordJoin = keywordJoinRepository.findById(id)
        .orElseThrow(RuntimeException::new);
    currentKeywordJoin.setInvisible(isInvisible);
    keywordJoinRepository.save(currentKeywordJoin);
  }

  /**
   * Updates keyword with new values.
   *
   * @param id      of a keyword
   * @param keyword that is updated
   * @return Keyword with updated values
   */
  @Override
  public Keyword updateKeyword(Long id, Keyword keyword) {
    Keyword currentKeyword = keywordRepository.findById(id).orElseThrow(RuntimeException::new);
    currentKeyword.setKeywordName(keyword.getKeywordName());
    currentKeyword.setDescription(keyword.getDescription());
    currentKeyword = keywordRepository.save(keyword);
    return currentKeyword;
  }

  /**
   * Update unread state for a keyword.
   *
   * @param id of a keyword;
   */
  @Override
  public void updateUnreadStates(Long id) {
    Keyword currentKeyword = keywordRepository.findById(id)
        .orElseThrow(RuntimeException::new);
    currentKeyword.setUnread(currentKeyword.getUnread() - 1);
    keywordRepository.save(currentKeyword);
  }

  private List<KeywordJoin> createKeywordJoinList(List<Keyword> keywords) {
    List<KeywordJoin> keywordJoinList = new ArrayList<>();

    for (Keyword keyword : keywords) {
      List<Article> articlesWithMatch = getMatchedArticles(keyword);

      if (!articlesWithMatch.isEmpty()) {
        List<KeywordJoin> newKeywordJoins = getNewKeywordsJoinForSave(
            keyword.getId(),
            articlesWithMatch);
        keywordJoinList.addAll(newKeywordJoins);
        keywordJoinRepository.saveAll(newKeywordJoins);
        keyword.setUnread(countUnreadArticlesForKeywords(keyword.getId()));

        Date lastUpdatedArticle = Collections.max(
            articlesWithMatch.stream().map(Article::getDate).toList());
        keyword.setUpdatedFrom(lastUpdatedArticle);
        keywordRepository.save(keyword);
      }
    }
    return keywordJoinList;
  }

  /**
   * Count unread articles for given id.
   *
   * @param id of an id
   * @return number of unread articles for the given keyword.
   */
  private long countUnreadArticlesForKeywords(Long id) {
    return articleRepository.countByIdInAndClicked(mapToArticleIds(id), false);
  }

  /**
   * Delete keywordJoin by keywordId.
   *
   * @param id of a keyword
   */
  private void deleteKeywordJoinByKeywordId(Long id) {
    List<KeywordJoin> keywordJoins = keywordJoinRepository.findAll().stream()
        .filter(join -> join.getKeywordId().equals(id)).toList();
    keywordJoinRepository.deleteAll(keywordJoins);
  }

  /**
   * Get list of article ids that already exist for given keyword.
   *
   * @param id of a keyword
   * @return list of articleIds
   */
  private List<Long> getArticleIdsJoinsForGivenId(@NonNull final Long id) {
    return keywordJoinRepository.findAll().stream()
        .filter(i -> i.getKeywordId().equals(id))
        .map(KeywordJoin::getArticleId)
        .toList();
  }

  /**
   * Get keyword join for a visible keyword.
   *
   * @param id of a keyword
   * @return list of keywordJoin
   */
  private List<KeywordJoin> getKeywordJoinForVisibleKeyword(final Long id) {
    return keywordJoinRepository.findAll().stream()
        .filter(i -> i.getKeywordId().equals(id) && !i.isInvisible())
        .toList();
  }

  /**
   * Look for all articles and find matches according to tags.
   *
   * @param keyword that contains tags
   * @return list of matched articles
   */
  private List<Article> getMatchedArticles(Keyword keyword) {
    List<String> tags = CommonHelper.getTagList(keyword);
    List<Article> articlesAfterLastSearch =
        articleRepository.findByDateAfter(keyword.getUpdatedFrom());

    return articlesAfterLastSearch.stream()
        .filter(i -> ((i.getDescription() != null && !i.getDescription().isEmpty())
            && tags.stream().anyMatch(i.getDescription().toLowerCase()::contains))
            || tags.stream().anyMatch(i.getTitle().toLowerCase()::contains)
            || ((i.getLink() != null && !i.getLink().isEmpty())
            && tags.stream().anyMatch(i.getLink().toLowerCase()::contains))
            || ((i.getUri() != null && !i.getUri().isEmpty())
            && tags.stream().anyMatch(i.getUri().toLowerCase()::contains))
        )
        .distinct().toList();
  }

  /**
   * Creates new KeywordJoins.
   *
   * @param id          of a keyword
   * @param allArticles articles
   * @return list of keywordJoins
   */
  private List<KeywordJoin> getNewKeywordsJoinForSave(Long id, List<Article> allArticles) {
    List<Long> currentArticleIds = getArticleIdsJoinsForGivenId(id);

    return allArticles.stream()
        .filter(a -> !currentArticleIds.contains(a.getId()))
        .map(a -> new KeywordJoin(id, a.getId(), a.getChannel()))
        .distinct().toList();
  }

  /**
   * Map articleIds from keywordJoins.
   *
   * @param id of a keyword
   * @return list of article ids
   */
  private List<Long> mapToArticleIds(Long id) {
    return getKeywordJoinForVisibleKeyword(id).stream()
        .map(KeywordJoin::getArticleId)
        .toList();
  }

  private void recalculateUnreadArticlesFromKeywordJoin(List<KeywordJoin> keywordJoins) {
    Map<Long, Long> result = keywordJoins.stream()
        .collect(Collectors.groupingByConcurrent(KeywordJoin::getKeywordId, Collectors.counting()));

    for (Map.Entry<Long, Long> entry : result.entrySet()) {
      Keyword keyword = keywordRepository.findById(entry.getKey())
          .orElseThrow(RuntimeException::new);
      keyword.setUnread(keyword.getUnread() - entry.getValue());
    }
  }
}
