package edu.usc.csci201.quiz;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("DisplayNames (unit / black-box)")
class DisplayNamesTest {

  @Test
  void normalizeTrimsWhitespace() {
    assertEquals("Alex", DisplayNames.normalize("  Alex  "));
  }

  @Test
  void uniquenessIgnoresCase() {
    assertEquals("alex", DisplayNames.uniquenessKey("Alex"));
    assertEquals("alex", DisplayNames.uniquenessKey("ALEX"));
  }

  @Test
  void rejectsDuplicateIgnoringCase() {
    Set<String> taken = Set.of("alex");
    assertFalse(DisplayNames.isAvailable("Alex", taken));
    assertFalse(DisplayNames.isAvailable(" alex ", taken));
  }

  @Test
  void acceptsUnusedName() {
    assertTrue(DisplayNames.isAvailable("Blake", Set.of("alex")));
  }

  @Test
  void rejectsBlankAfterTrim() {
    assertFalse(DisplayNames.isAvailable("   ", Set.of()));
  }
}
