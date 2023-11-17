package eu.evropskyrozhled.h2database.service.rest;


import static eu.evropskyrozhled.h2database.service.model.Constants.Rest.Endpoints.KEYWORD_JOIN;

import eu.evropskyrozhled.h2database.service.model.keyword.KeywordJoin;
import eu.evropskyrozhled.h2database.service.repository.KeywordJoinRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Rest Controller for KeywordJoin.
 */
@RestController
@RequestMapping(KEYWORD_JOIN)
public class KeywordJoinController {

  private final KeywordJoinRepository keywordJoinRepository;

  @Autowired
  public KeywordJoinController(KeywordJoinRepository keywordJoinRepository) {
    this.keywordJoinRepository = keywordJoinRepository;
  }

  /**
   * Get all KeywordJoins.
   *
   * @return List of KeywordJoins
   */
  @GetMapping
  public List<KeywordJoin> getKeywordJoin() {
    return keywordJoinRepository.findAll();
  }

  /**
   * Get KeywordJoin for given id.
   *
   * @param id of a KeywordJoin
   * @return KeywordJoin
   */
  @GetMapping("/{id}")
  public KeywordJoin getKeyword(@PathVariable Long id) {
    return keywordJoinRepository.findById(id).orElseThrow(RuntimeException::new);
  }
}
