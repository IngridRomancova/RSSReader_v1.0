package eu.evropskyrozhled.h2database.service.exception;

/**
 * Custom exception for incorrect connection to rss source.
 */
public class RssException extends Exception {

  public RssException(String errorMessage) {
    super(errorMessage);
  }
}
