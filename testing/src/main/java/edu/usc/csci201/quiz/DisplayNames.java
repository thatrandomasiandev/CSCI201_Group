package edu.usc.csci201.quiz;

import java.util.Locale;
import java.util.Objects;
import java.util.Set;

/** Case-insensitive display-name rules for a single room lobby. */
public final class DisplayNames {

  private DisplayNames() {}

  /** Trims surrounding whitespace; does not alter internal spacing. */
  public static String normalize(String raw) {
    Objects.requireNonNull(raw, "display name");
    return raw.trim();
  }

  /** Folded key used for uniqueness checks inside one room. */
  public static String uniquenessKey(String raw) {
    return normalize(raw).toLowerCase(Locale.ROOT);
  }

  /**
   * @return true if {@code candidate} may join given {@code takenKeys}
   *     (keys already produced by {@link #uniquenessKey})
   */
  public static boolean isAvailable(String candidate, Set<String> takenKeys) {
    Objects.requireNonNull(takenKeys, "takenKeys");
    String key = uniquenessKey(candidate);
    if (key.isEmpty()) {
      return false;
    }
    return !takenKeys.contains(key);
  }
}
