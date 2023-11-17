package eu.evropskyrozhled.h2database.service.impl;

import eu.evropskyrozhled.h2database.service.model.keyword.Keyword;

/**
 * Interface for KeywordServiceImpl.
 */
public interface KeywordService {

  void deleteKeyword(Long id);

  void deleteKeywordJoinByArticleId(Long id);

  void deleteKeywordJoinByChannelId(Long id);

  void downloadAndSaveNewArticles(Long id);

  void recalculateUnreadArticles();

  void recalculateUnreadArticlesFromArticleId(Long id);

  void searchNewArticles();

  void setActivationStatus(Long id, Keyword keyword);

  void setInvisibleStatus(Long id, boolean isInvisible);

  Keyword updateKeyword(Long id, Keyword keyword);

  void updateUnreadStates(Long id);
}
