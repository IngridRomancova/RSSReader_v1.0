package eu.evropskyrozhled.h2database.service.impl;

import eu.evropskyrozhled.h2database.service.model.keyword.KeywordView;

/**
 * Interface for KeywordServiceImpl.
 */
public interface KeywordViewService {

  void updateArticle(Long id, KeywordView keywordView);

}
