package eu.evropskyrozhled.h2database.service.rest;


import static eu.evropskyrozhled.h2database.service.model.Constants.Rest.Endpoints.CHANNEL_VIEW;

import eu.evropskyrozhled.h2database.service.model.article.ChannelView;
import eu.evropskyrozhled.h2database.service.repository.ChannelViewRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Rest Controller for ChannelView.
 */
@RestController
@RequestMapping(CHANNEL_VIEW)
public class ChannelViewController {

  private final ChannelViewRepository channelViewRepository;

  @Autowired
  public ChannelViewController(ChannelViewRepository channelViewRepository) {
    this.channelViewRepository = channelViewRepository;
  }

  /**
   * Get list of all channel, pageable.
   *
   * @param page number
   * @param size of channels for one page
   * @return ResponseEntity with nested channels
   */
  @GetMapping
  public ResponseEntity<Map<String, Object>> getChannels(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "15") int size
  ) {
    try {
      Pageable paging = PageRequest.of(page, size);
      Page<ChannelView> pageChannelViews = channelViewRepository.findAll(paging);
      List<ChannelView> channelViews = pageChannelViews.getContent();
      Map<String, Object> response = createResponse(channelViews, pageChannelViews);
      return new ResponseEntity<>(response, HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Search channel according to searchValue.
   *
   * @param searchValue that is search in all Channel columns
   * @param page        of a result
   * @param size        of search channels for a page
   * @return ResponseEntity with result
   */
  @GetMapping("searchChannels/{searchValue}")
  public ResponseEntity<Map<String, Object>> getChannel(
      @PathVariable String searchValue,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "15") int size) {
    try {
      Pageable paging = PageRequest.of(page, size);
      Page<ChannelView> pageChannelViews = channelViewRepository.findByTitleContainingIgnoreCase(
          searchValue, paging);
      List<ChannelView> channelViews = pageChannelViews.getContent();
      Map<String, Object> response = createResponse(channelViews, pageChannelViews);
      return new ResponseEntity<>(response, HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  private Map<String, Object> createResponse(final List<ChannelView> channelViews,
      final Page<ChannelView> pageChannelViews) {
    Map<String, Object> response = new HashMap<>();
    response.put("channelViews", channelViews);
    response.put("currentPage", pageChannelViews.getNumber());
    response.put("totalItems", pageChannelViews.getTotalElements());
    response.put("totalPages", pageChannelViews.getTotalPages());
    return response;
  }
}
