package eu.evropskyrozhled.h2database.service.impl;

import eu.evropskyrozhled.h2database.service.model.article.Channel;
import java.util.List;

/**
 * Interface for ChannelServiceImpl.
 */
public interface ChannelService {

  void deleteChannel(Long id);

  List<Channel> findValidAndActiveChannels();

  void channelValidation();

  void increaseUnreadArticles(Channel channel, int numberOfArticles);

  void isValidUri(Channel channel);

  void recalculateUnreadArticles();

  void reduceUnreadArticles(Long id, int numberOfArticles);

  void setActivationStatus(Long id, Channel channel);

  void setInvalidStatus(Channel channel);

  Channel updateChannel(Long id, Channel channel);

}
