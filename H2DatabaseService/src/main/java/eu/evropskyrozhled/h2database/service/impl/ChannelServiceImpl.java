package eu.evropskyrozhled.h2database.service.impl;

import eu.evropskyrozhled.h2database.service.helper.CommonHelper;
import eu.evropskyrozhled.h2database.service.model.article.Article;
import eu.evropskyrozhled.h2database.service.model.article.Channel;
import eu.evropskyrozhled.h2database.service.repository.ArticleRepository;
import eu.evropskyrozhled.h2database.service.repository.ChannelRepository;
import java.util.Date;
import java.util.List;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Channel service for processing operations with channels.
 */
@Service
public class ChannelServiceImpl implements ChannelService {

  private final ArticleRepository articleRepository;
  private final ChannelRepository channelRepository;
  private final KeywordServiceImpl keywordService;

  /**
   * Constructor for ArticleService.
   *
   * @param articleRepository creates connection to article repository
   * @param channelRepository creates connection to channel repository
   * @param keywordService    creates connection to keyword service
   */
  @Autowired
  public ChannelServiceImpl(ArticleRepository articleRepository,
      ChannelRepository channelRepository,
      KeywordServiceImpl keywordService) {
    this.articleRepository = articleRepository;
    this.channelRepository = channelRepository;
    this.keywordService = keywordService;
  }

  /**
   * Delete channel and corresponding article and keywordJoin information.
   *
   * @param id of a channel
   */
  @Override
  public void deleteChannel(Long id) {
    keywordService.deleteKeywordJoinByChannelId(id);
    deleteChannelArticles(id);
    channelRepository.deleteById(id);
  }

  /**
   * Find valid and active channels.
   *
   * @return list of active and valid channels
   */
  @Override
  public List<Channel> findValidAndActiveChannels() {
    return channelRepository.findAll().stream()
        .filter(channel -> channel.isValid() && channel.isActive())
        .toList();
  }

  /**
   * Check valid URI for all channels.
   */
  @Override
  public void channelValidation() {
    channelRepository.findAll().stream().filter(channel -> !channel.isValid())
        .forEach(this::isValidUri);
  }

  /**
   * Increase the number of unread articles for the channel.
   *
   * @param channel          of a channel
   * @param numberOfArticles that are newly unread
   */
  @Override
  public void increaseUnreadArticles(@NonNull Channel channel, int numberOfArticles) {
    channel.setUnread(channel.getUnread() + numberOfArticles);
    channel.setUpdatedFrom(new Date());
    channelRepository.save(channel);
  }

  /**
   * Check if URI is valid.
   *
   * @param channel with info about uri
   */
  @Override
  public void isValidUri(Channel channel) {
    if (!channel.isValid() && CommonHelper.isValidUri(channel.getLink())) {
      channel.setValid(true);
      channelRepository.save(channel);
    }
  }

  /**
   * Recalculate unread articles for a channel.
   */
  @Override
  public void recalculateUnreadArticles() {
    List<Long> idList = channelRepository.findAll().stream().map(Channel::getId).toList();

    idList.forEach(id -> {
          Channel currentChannel = channelRepository.findById(id)
              .orElseThrow(RuntimeException::new);
          currentChannel.setUnread(articleRepository.countByChannelAndClicked(id, false));
          channelRepository.save(currentChannel);
        }
    );
  }

  /**
   * Reduce the number of unread articles for the channel.
   *
   * @param id               of a channel
   * @param numberOfArticles that are newly read
   */
  @Override
  public void reduceUnreadArticles(@NonNull Long id, int numberOfArticles) {
    Channel currentChannel = channelRepository.findById(id)
        .orElseThrow(RuntimeException::new);
    currentChannel.setUnread(currentChannel.getUnread() - numberOfArticles);
    channelRepository.save(currentChannel);
  }

  /**
   * Set active/inactive status for a channel.
   *
   * @param id      of a channel
   * @param channel that is updated
   */
  @Override
  public void setActivationStatus(Long id, Channel channel) {
    Channel currentChannel = channelRepository.findById(id).orElseThrow(RuntimeException::new);
    currentChannel.setActive(channel.isActive());
    channelRepository.save(currentChannel);
  }

  /**
   * Set invalid status for channel.
   *
   * @param channel with newly invalid status
   */
  @Override
  public void setInvalidStatus(Channel channel) {
    channel.setValid(false);
    channelRepository.save(channel);
  }

  /**
   * Updates channel with new values.
   *
   * @param id      of a channel
   * @param channel that is updated
   * @return Channel with updated values
   */
  @Override
  public Channel updateChannel(Long id, Channel channel) {
    Channel currentChannel = channelRepository.findById(id).orElseThrow(RuntimeException::new);
    currentChannel.setTitle(channel.getTitle());
    currentChannel.setDescription(channel.getDescription());
    currentChannel.setLink(channel.getLink());
    currentChannel = channelRepository.save(channel);
    return currentChannel;
  }

  private void deleteChannelArticles(Long id) {
    List<Article> articles = articleRepository.findAll().stream()
        .filter(article -> article.getChannel().equals(id)).toList();
    articleRepository.deleteAll(articles);
  }

}
