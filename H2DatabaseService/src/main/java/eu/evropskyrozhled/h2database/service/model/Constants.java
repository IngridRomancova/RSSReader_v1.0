package eu.evropskyrozhled.h2database.service.model;

/**
 * Constants class.
 */
public class Constants {

  private Constants() {
    throw new IllegalStateException("Constants class");
  }

  /**
   * Rest constants.
   */
  public static class Rest {

    private Rest() {
      throw new IllegalStateException("Rest constant class");
    }

    /**
     * RepositoryMgmtController Endpoints.
     */
    public static class Endpoints {

      private Endpoints() {
        throw new IllegalStateException("Endpoints class");
      }

      public static final String CHANNELS = "/channels";
      public static final String CHANNEL_VIEW = "/channel-view";
      public static final String KEYWORDS = "/keywords";
      public static final String ARTICLE_VIEW = "/article-view";
      public static final String KEYWORD_VIEW = "/keyword-view";
      public static final String SEARCH_VIEW = "/search-view";
      public static final String KEYWORD_JOIN = "/keyword-join";
    }
  }
}
