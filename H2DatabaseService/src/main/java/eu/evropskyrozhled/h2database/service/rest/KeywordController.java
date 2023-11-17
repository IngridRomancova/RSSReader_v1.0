package eu.evropskyrozhled.h2database.service.rest;


import static eu.evropskyrozhled.h2database.service.model.Constants.Rest.Endpoints.KEYWORDS;

import eu.evropskyrozhled.h2database.service.impl.KeywordService;
import eu.evropskyrozhled.h2database.service.impl.KeywordServiceImpl;
import eu.evropskyrozhled.h2database.service.model.keyword.Keyword;
import eu.evropskyrozhled.h2database.service.repository.KeywordRepository;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
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
 * Rest Controller for Keyword.
 */
@RestController
@RequestMapping(KEYWORDS)
public class KeywordController {

  private final KeywordService keywordService;
  private final KeywordRepository keywordRepository;

  @Autowired
  public KeywordController(KeywordRepository keywordRepository,
      KeywordServiceImpl keywordService) {
    this.keywordRepository = keywordRepository;
    this.keywordService = keywordService;
  }

  /**
   * Get all keywords.
   *
   * @return list of keywords
   */
  @GetMapping
  public List<Keyword> getKeywords() {
    return keywordRepository.findAll();
  }

  /**
   * Get keyword by id.
   *
   * @param id of a keyword
   * @return Keyword
   */
  @GetMapping("/{id}")
  public Keyword getKeyword(@PathVariable Long id) {
    return keywordRepository.findById(id).orElseThrow(RuntimeException::new);
  }

  /**
   * Save new Keyword.
   *
   * @param keyword that is saved
   * @return ResponseEntity with new keyword
   * @throws URISyntaxException if the creation of uri failed
   */
  @PostMapping
  public ResponseEntity<Keyword> createKeyword(@RequestBody Keyword keyword)
      throws URISyntaxException {
    Keyword savedKeyword = keywordRepository.save(keyword);
    return ResponseEntity.created(new URI("/keywords/" + savedKeyword.getId())).body(savedKeyword);
  }

  /**
   * Update a keyword.
   *
   * @param id      of a keyword
   * @param keyword that is updated
   * @return ResponseEntity with updated keyword
   */
  @PutMapping("/{id}")
  public ResponseEntity<Keyword> updateKeyword(@PathVariable Long id,
      @RequestBody Keyword keyword) {
    Keyword currentKeyword = keywordService.updateKeyword(id, keyword);
    return ResponseEntity.ok(currentKeyword);
  }

  /**
   * Set active/inactive state of a keyword.
   *
   * @param id      of a keyword
   * @param keyword with new active/inactive state
   * @return ResponseEntity with new values for keyword
   */
  @PutMapping("/active/{id}")
  public ResponseEntity<Keyword> activeKeyword(@PathVariable Long id,
      @RequestBody Keyword keyword) {
    keywordService.setActivationStatus(id, keyword);
    return ResponseEntity.ok(keyword);
  }

  /**
   * Delete a keyword.
   *
   * @param id of a keyword that is deleted
   * @return ResponseEntity with ok status.
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<Keyword> deleteKeyword(@PathVariable Long id) {
    keywordService.deleteKeyword(id);
    return ResponseEntity.ok().build();
  }

}
