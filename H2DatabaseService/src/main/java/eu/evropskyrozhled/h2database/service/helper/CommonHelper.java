package eu.evropskyrozhled.h2database.service.helper;

import eu.evropskyrozhled.h2database.service.model.keyword.Keyword;
import jakarta.persistence.criteria.Predicate;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import lombok.NonNull;
import org.springframework.data.jpa.domain.Specification;

/**
 * Helper class for all common tasks.
 */
public class CommonHelper {

  private CommonHelper() {
    throw new IllegalStateException("Utility class");
  }

  /**
   * Check if given url is still valid.
   *
   * @param url Url to rss feed
   * @return true if url is valid
   */
  public static boolean isValidUri(@NonNull String url) {
    boolean isValidUri;
    try {
      HttpURLConnection huc = (HttpURLConnection) (new URI(url).toURL()).openConnection();
      isValidUri = HttpURLConnection.HTTP_OK == huc.getResponseCode();
      return isValidUri;
    } catch (IOException | URISyntaxException e) {
      return false;
    }
  }

  /**
   * Get tag list and decode it.
   *
   * @param keyword that contains decoded tags
   * @return list of tags in string type
   */
  public static List<String> getTagList(Keyword keyword) {
    return Arrays.stream(new String(keyword.getTags()).split("%2C"))
        .map(i -> URLDecoder.decode(i, StandardCharsets.UTF_8))
        .map(String::toLowerCase)
        .toList();
  }

  /**
   * Find string value in all columns.
   *
   * @param text is search value
   * @param <T>  is abstract return type
   * @return Specification for JPA repository input parameter
   */
  public static <T> Specification<T> textInAllColumns(String text) {
    if (!text.contains("%")) {
      text = "%" + text + "%";
    }
    final String finalText = text.toLowerCase();

    return (root, cq, builder) ->
        builder
            .or(root.getModel()
                .getDeclaredSingularAttributes()
                .stream()
                .filter(a -> a.getJavaType()
                    .getSimpleName().equalsIgnoreCase("string"))
                .map(a -> builder.like(builder.lower(root.get(a.getName())), finalText)
                )
                .distinct()
                .toArray(Predicate[]::new)
            );
  }

}
