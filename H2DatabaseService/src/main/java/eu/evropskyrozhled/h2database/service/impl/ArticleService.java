package eu.evropskyrozhled.h2database.service.impl;

import eu.evropskyrozhled.h2database.service.exception.RssException;
import eu.evropskyrozhled.h2database.service.model.article.ArticleView;
import eu.evropskyrozhled.h2database.service.model.article.Channel;

/**
 * Interface for ArticleServiceImpl.
 */
public interface ArticleService {

  void deleteAllUnsavedArticles();

  void deleteAllArticles();

  void downloadAndSavedNewArticles(Channel channel) throws RssException;

  void removeArticle(Long id);

  void setStatusFavourite(Long id, boolean isFavourite);

  void setStatusSaved(Long id, boolean isSaved);

  void scheduleUpdateAll();

  void updateReadArticle(ArticleView articleView, Long id);

}
