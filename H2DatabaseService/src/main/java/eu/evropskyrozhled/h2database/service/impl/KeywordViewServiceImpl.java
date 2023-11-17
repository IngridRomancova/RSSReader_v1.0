package eu.evropskyrozhled.h2database.service.impl;

import eu.evropskyrozhled.h2database.service.model.article.Article;
import eu.evropskyrozhled.h2database.service.model.keyword.KeywordView;
import eu.evropskyrozhled.h2database.service.repository.ArticleRepository;
import eu.evropskyrozhled.h2database.service.repository.KeywordViewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * KeywordView service for processing operations with keywords and corresponding tables.
 */
@Service
public class KeywordViewServiceImpl implements KeywordViewService {

  private final KeywordViewRepository keywordViewRepository;
  private final KeywordServiceImpl keywordService;
  private final ArticleRepository articleRepository;
  private final ChannelServiceImpl channelService;


  /**
   * Constructor for KeywordViewService.
   *
   * @param keywordViewRepository creates connection to keywordView repository
   * @param keywordService        creates connection to keyword service
   * @param articleRepository     creates connection to article service
   * @param channelService        creates connection to channel service
   */
  @Autowired
  public KeywordViewServiceImpl(KeywordViewRepository keywordViewRepository,
      KeywordServiceImpl keywordService,
      ArticleRepository articleRepository,
      ChannelServiceImpl channelService) {
    this.keywordViewRepository = keywordViewRepository;
    this.keywordService = keywordService;
    this.articleRepository = articleRepository;
    this.channelService = channelService;
  }


  /**
   * Updates unread status for am article and corresponding tables.
   *
   * @param id          of and keywordView
   * @param keywordView keywordView that is updated
   */
  @Override
  public void updateArticle(Long id, KeywordView keywordView) {
    KeywordView currentKeywordView = keywordViewRepository.findById(id)
        .orElseThrow(RuntimeException::new);
    Article currentArticle = articleRepository.findById(currentKeywordView.getArticleId())
        .orElseThrow(RuntimeException::new);

    if (!currentArticle.isClicked()) {
      currentArticle.setClicked(keywordView.isClicked());
      channelService.reduceUnreadArticles(currentArticle.getChannel(), 1);
      keywordService.updateUnreadStates(keywordView.getKeywordId());
      articleRepository.save(currentArticle);
    }
  }
}