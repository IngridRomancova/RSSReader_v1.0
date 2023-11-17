package eu.evropskyrozhled.h2database.service.impl;

import eu.evropskyrozhled.h2database.service.exception.RssException;
import eu.evropskyrozhled.h2database.service.helper.RssFeedHelper;
import eu.evropskyrozhled.h2database.service.model.article.Article;
import eu.evropskyrozhled.h2database.service.model.article.ArticleJoin;
import eu.evropskyrozhled.h2database.service.model.article.ArticleView;
import eu.evropskyrozhled.h2database.service.model.article.Channel;
import eu.evropskyrozhled.h2database.service.model.keyword.KeywordJoin;
import eu.evropskyrozhled.h2database.service.repository.ArticleJoinRepository;
import eu.evropskyrozhled.h2database.service.repository.ArticleRepository;
import eu.evropskyrozhled.h2database.service.repository.ChannelRepository;
import eu.evropskyrozhled.h2database.service.repository.KeywordJoinRepository;
import eu.evropskyrozhled.h2database.service.repository.KeywordRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * Article service for processing operations with articles.
 */
@Slf4j
@EnableScheduling
@Service
public class ArticleServiceImpl implements ArticleService {

  private final ArticleRepository articleRepository;
  private final ArticleJoinRepository articleJoinRepository;
  private final ChannelRepository channelRepository;
  private final KeywordJoinRepository keywordJoinRepository;
  private final KeywordRepository keywordRepository;
  private final ChannelServiceImpl channelService;
  private final KeywordServiceImpl keywordService;

  /**
   * Constructor for ArticleService.
   *
   * @param articleRepository     creates connection to article repository
   * @param articleJoinRepository creates connection to articleJoin repository
   * @param channelRepository     creates connection to channel repository
   * @param keywordJoinRepository creates connection to keywordJoin repository
   * @param keywordRepository     creates connection to keyword repository
   * @param channelService        creates connection to channel service
   * @param keywordService        creates connection to keyword service
   */
  @Autowired
  public ArticleServiceImpl(ArticleRepository articleRepository,
      ArticleJoinRepository articleJoinRepository,
      ChannelRepository channelRepository,
      KeywordJoinRepository keywordJoinRepository,
      KeywordRepository keywordRepository,
      ChannelServiceImpl channelService,
      KeywordServiceImpl keywordService) {
    this.articleRepository = articleRepository;
    this.articleJoinRepository = articleJoinRepository;
    this.channelRepository = channelRepository;
    this.keywordJoinRepository = keywordJoinRepository;
    this.keywordRepository = keywordRepository;
    this.channelService = channelService;
    this.keywordService = keywordService;
  }

  /**
   * Delete all unsaved articles.
   */
  @Override
  public void deleteAllUnsavedArticles() {
    List<Article> unsavedArticles = articleRepository.findAll().stream().filter(i -> !i.isSaved())
        .toList();
    List<Long> articleIds = unsavedArticles.stream().map(Article::getId).toList();
    List<KeywordJoin> keywordJoins = keywordJoinRepository.findAll().stream()
        .filter(join -> articleIds.contains(join.getArticleId())).toList();
    List<ArticleJoin> articleJoins = articleJoinRepository.findAll().stream()
        .filter(join -> articleIds.contains(join.getArticleId())).toList();
    keywordJoinRepository.deleteAll(keywordJoins);
    articleJoinRepository.deleteAll(articleJoins);
    articleRepository.deleteAll(unsavedArticles);

    recalculateUnreadArticles();
  }

  /**
   * Delete all articles and their connections to other table.
   */
  @Override
  public void deleteAllArticles() {
    keywordJoinRepository.deleteAll();
    articleJoinRepository.deleteAll();
    articleRepository.deleteAll();
    channelRepository.updateUnread(0);
    keywordRepository.updateUnread(0);
  }

  /**
   * Download and saved all new articles.
   *
   * @param channel with information about channel
   * @throws RssException that is customized
   */
  @Override
  public void downloadAndSavedNewArticles(Channel channel) throws RssException {
    channelService.isValidUri(channel);

    List<Article> newArticles = new ArrayList<>();

    if (channel.isActive() && channel.isValid()) {
      newArticles = findNewArticlesForChannel(channel);
    }

    articleRepository.saveAll(newArticles);
    channelService.increaseUnreadArticles(channel, newArticles.size());
    articleJoinRepository.saveAll(saveNewArticleJoins(newArticles));
  }

  /**
   * Remove article of a given id.
   *
   * @param id of an article
   */
  @Override
  public void removeArticle(@NonNull Long id) {
    Article article = articleRepository.findById(id).orElseThrow(RuntimeException::new);

    keywordService.deleteKeywordJoinByArticleId(id);

    Optional<ArticleJoin> articleJoin = articleJoinRepository.findAll().stream()
        .filter(join -> join.getArticleId().equals(id)).findFirst();

    if (articleJoin.isPresent()) {
      articleJoinRepository.delete(articleJoin.get());

      if (!article.isClicked()) {
        channelService.reduceUnreadArticles(articleJoin.get().getChannelId(), 1);
      }

    }

    articleRepository.deleteById(id);
  }

  /**
   * Set favourite status for an article.
   *
   * @param id          of an article
   * @param isFavourite true if article is favourite
   */
  @Override
  public void setStatusFavourite(Long id, boolean isFavourite) {
    Article currentArticle = articleRepository.findById(id)
        .orElseThrow(RuntimeException::new);
    currentArticle.setFavourite(isFavourite);
    articleRepository.save(currentArticle);
  }

  /**
   * Set saved/unsaved status for an article.
   *
   * @param id      of an article
   * @param isSaved true if article is saved
   */
  @Override
  public void setStatusSaved(Long id, boolean isSaved) {
    Article currentArticle = articleRepository.findById(id)
        .orElseThrow(RuntimeException::new);
    currentArticle.setSaved(isSaved);
    articleRepository.save(currentArticle);
  }

  /**
   * Automatic job that updates all channels after fixed delay interval.
   */
  @Override
  @Scheduled(fixedDelayString = "${fixedDelay.in.milliseconds}",
      initialDelayString = "${initialDelay.in.milliseconds}")
  public void scheduleUpdateAll() {
    log.info("Scheduled channel update running...");

    channelService.channelValidation();
    List<Channel> channels = channelService.findValidAndActiveChannels();

    List<Article> newArticles = channels.parallelStream()
        .map(channel -> {
          List<Article> newChannelArticles = new ArrayList<>();

          try {
            newChannelArticles = findNewArticlesForChannel(channel);
            channelService.increaseUnreadArticles(channel, newChannelArticles.size());
          } catch (Exception e) {
            channelService.setInvalidStatus(channel);
            new ArrayList<>();
          }
          return newChannelArticles;
        })
        .flatMap(List::stream).toList();

    articleRepository.saveAll(newArticles);
    articleJoinRepository.saveAll(saveNewArticleJoins(newArticles));
  }

  /**
   * Set that article was read.
   *
   * @param articleView contains article information from frontend
   * @param id          of a channel.
   */
  @Override
  public void updateReadArticle(ArticleView articleView, @NonNull Long id) {
    Article currentArticle = articleRepository.findById(id).orElseThrow(RuntimeException::new);

    if (!currentArticle.isClicked()) {
      channelService.reduceUnreadArticles(articleView.getChannelId(), 1);
      keywordService.recalculateUnreadArticlesFromArticleId(id);
      currentArticle.setClicked(articleView.isClicked());
      articleRepository.save(currentArticle);
    }
  }

  private List<Article> findNewArticlesForChannel(Channel channel) throws RssException {
    List<Article> channelArticles = RssFeedHelper.getChannelRss(channel.getLink(),
        channel.getUpdatedFrom(), channel.getId());

    if (!channelArticles.isEmpty()) {
      List<Article> currentArticles = articleRepository.findByChannel(channel.getId());
      return RssFeedHelper.filterNewChannelArticles(currentArticles, channelArticles);
    }
    return new ArrayList<>();
  }

  private void recalculateUnreadArticles() {
    channelService.recalculateUnreadArticles();
    keywordService.recalculateUnreadArticles();
  }

  private List<ArticleJoin> saveNewArticleJoins(List<Article> newArticles) {
    return newArticles.stream()
        .map(a -> new ArticleJoin(a.getId(), a.getChannel()))
        .toList();
  }
}
