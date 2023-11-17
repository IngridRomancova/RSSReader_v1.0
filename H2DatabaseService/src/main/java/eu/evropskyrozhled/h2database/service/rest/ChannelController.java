package eu.evropskyrozhled.h2database.service.rest;


import static eu.evropskyrozhled.h2database.service.model.Constants.Rest.Endpoints.CHANNELS;

import eu.evropskyrozhled.h2database.service.helper.CommonHelper;
import eu.evropskyrozhled.h2database.service.impl.ChannelService;
import eu.evropskyrozhled.h2database.service.model.article.Channel;
import eu.evropskyrozhled.h2database.service.repository.ChannelRepository;
import jakarta.transaction.Transactional;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Rest Controller for Channel.
 */
@RestController
@Transactional
@RequestMapping(CHANNELS)
public class ChannelController {

  private final ChannelRepository channelRepository;
  private final ChannelService channelService;

  /**
   * Constructor for Channel controller.
   *
   * @param channelRepository creates connection to channel repository
   * @param channelService    creates connection to channel service
   */
  @Autowired
  public ChannelController(ChannelRepository channelRepository,
      ChannelService channelService) {
    this.channelRepository = channelRepository;
    this.channelService = channelService;
  }

  /**
   * Get all channels.
   *
   * @return list of channels
   */
  @GetMapping
  public List<Channel> getChannels() {
    return channelRepository.findAll();
  }

  /**
   * Get channel based on channel id.
   *
   * @param id of a channel
   * @return Channel
   */
  @GetMapping("/{id}")
  public Channel getChannel(@PathVariable Long id) {
    return channelRepository.findById(id).orElseThrow(RuntimeException::new);
  }

  /**
   * Save new channel.
   *
   * @param channel information
   * @return ResponseEntity with new channel
   */
  @PostMapping
  public ResponseEntity<Channel> createChannel(@RequestBody Channel channel)
      throws URISyntaxException {
    if (CommonHelper.isValidUri(channel.getLink())) {
      Channel savedChannel = channelRepository.save(channel);
      return ResponseEntity.created(new URI("/channels/" + savedChannel.getId()))
          .body(savedChannel);
    } else {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

  }

  /**
   * Update channel information.
   *
   * @param id      of a channel
   * @param channel with updated values
   * @return ResponseEntity with updated channel
   */
  @PutMapping("/{id}")
  public ResponseEntity<Channel> updateChannel(@PathVariable Long id,
      @RequestBody Channel channel) {
    Channel currentChannel = channelService.updateChannel(id, channel);
    return ResponseEntity.ok(currentChannel);
  }

  /**
   * Set channel to active/inactive state.
   *
   * @param id      of a channel
   * @param channel channel with new boolean value for active/inactive state
   * @return ResponseEntity with new active/inactive state
   */
  @PutMapping("/active/{id}")
  public ResponseEntity<Channel> activeChannel(@PathVariable Long id,
      @RequestBody Channel channel) {
    channelService.setActivationStatus(id, channel);
    return ResponseEntity.ok(channel);
  }

  /**
   * Delete channel.
   *
   * @param id of a deleted channel
   * @return ResponseEntity with deleted channel
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<Channel> deleteChannel(@PathVariable Long id) {
    channelService.deleteChannel(id);
    return ResponseEntity.ok().build();
  }
}
