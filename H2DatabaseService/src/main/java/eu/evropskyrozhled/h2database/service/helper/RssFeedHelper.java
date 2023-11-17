package eu.evropskyrozhled.h2database.service.helper;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import eu.evropskyrozhled.h2database.service.exception.RssException;
import eu.evropskyrozhled.h2database.service.model.article.Article;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.NonNull;

/**
 * Helper class for conversion to Articles from feed.
 */
public class RssFeedHelper {

  private RssFeedHelper() {
    throw new IllegalStateException("Utility class");
  }

  /**
   * Get RSS feed for given url.
   *
   * @param rssUrl      URL to rss feed
   * @param updatedFrom the date of a last update of e rss source
   * @param id          of a channel
   * @return List of new Articles
   */
  public static List<Article> getChannelRss(@NonNull final String rssUrl, final Date updatedFrom,
      @NonNull final Long id) throws RssException {
    List<Article> articles;

    try (XmlReader reader = new XmlReader(new URI(rssUrl).toURL().openStream())) {
      SyndFeed feed = new SyndFeedInput().build(reader);
      articles = RssFeedHelper.createArticle(feed, updatedFrom, id);
    } catch (IOException | FeedException | URISyntaxException e) {
      throw new RssException(e.getMessage());
    }
    return articles;
  }

  /**
   * Get RSS feed for given url.
   *
   * @param feed        contains items
   * @param updatedFrom the date of last update of a rss source
   * @param id          of a channel
   * @return List of new Articles
   */
  public static List<Article> createArticle(@NonNull final SyndFeed feed,
      @NonNull final Date updatedFrom, @NonNull final Long id) {
    List<Article> articles = new ArrayList<>();

    for (SyndEntry entry : feed.getEntries()) {
      if (entry.getPublishedDate() != null) {
        if (entry.getPublishedDate().after(updatedFrom)) {
          articles.add(buildArticle(entry, id, entry.getPublishedDate()));
        } else {
          break;
        }
      } else {
        articles.add(buildArticle(entry, id, new Date()));
      }
    }

    return articles;
  }

  private static Article buildArticle(@NonNull final SyndEntry entry,
      @NonNull final Long rssChannel, @NonNull final Date date) {
    return Article.builder()
        .link(entry.getLink() != null
            ? entry.getLink().substring(0, Math.min(255, entry.getLink().length()))
            : "")
        .uri(entry.getUri() != null
            ? entry.getUri().substring(0, Math.min(255, entry.getUri().length()))
            : "")
        .description(entry.getDescription() != null
            ? entry.getDescription().getValue()
            .substring(0, Math.min(4000, entry.getDescription().getValue().length()))
            : "")
        .title(entry.getTitle().substring(0, Math.min(500, entry.getTitle().length())))
        .date(date)
        .clicked(false)
        .favourite(false)
        .saved(false)
        .archived(false)
        .channel(rssChannel).build();
  }

  /**
   * Check if new article already exists in database.
   *
   * @param currentArticles already saved under channel
   * @param channelArticles are new articles to be saved
   * @return list of articles that are really new ones
   */
  public static List<Article> filterNewChannelArticles(List<Article> currentArticles,
      List<Article> channelArticles) {
    if (!currentArticles.isEmpty()) {
      List<String> currentTitles = currentArticles.stream().map(Article::getTitle)
          .toList();
      return channelArticles.stream()
          .filter(i -> !currentTitles.contains(i.getTitle()))
          .toList();
    } else {
      return new ArrayList<>(channelArticles);
    }
  }
}
