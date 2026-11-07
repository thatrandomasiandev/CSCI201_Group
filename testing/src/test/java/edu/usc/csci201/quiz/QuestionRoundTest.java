package edu.usc.csci201.quiz;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("QuestionRound")
class QuestionRoundTest {

  private QuestionRound round;

  @BeforeEach
  void setUp() {
    round = new QuestionRound("B", Set.of("A", "B", "C", "D"), 0L, 20_000L, 200);
  }

  @Test
  void acceptsOnTimeValidChoice() {
    assertTrue(round.submit(1, "A", 1_000L));
    assertEquals(1, round.answeredCount());
  }

  @Test
  void rejectsAfterDeadline() {
    assertFalse(round.submit(1, "A", 20_001L));
    assertEquals(0, round.answeredCount());
  }

  @Test
  void rejectsIllegalChoice() {
    assertThrows(IllegalArgumentException.class, () -> round.submit(1, "Z", 100L));
  }

  @Test
  void replaceDoesNotInflateCount() {
    round.submit(1, "A", 100L);
    assertFalse(round.submit(1, "B", 200L));
    assertEquals(1, round.answeredCount());
  }

  @Test
  void closeScoresCorrectIncorrectAndMissing() {
    round.submit(1, "B", 500L);
    round.submit(2, "A", 500L);
    Map<Integer, QuestionRound.ScoreResult> results =
        round.closeAndScore(Map.of(1, 500L, 2, 500L), Set.of(1, 2, 3));

    assertEquals(100 - (500 / 200), results.get(1).points());
    assertTrue(results.get(1).correct());
    assertEquals(0, results.get(2).points());
    assertFalse(results.get(2).correct());
    assertFalse(results.get(3).answered());
    assertEquals(0, results.get(3).points());
  }

  @Test
  void cannotCloseTwice() {
    round.closeAndScore(Set.of(1));
    assertThrows(IllegalStateException.class, () -> round.closeAndScore(Set.of(1)));
  }

  @Test
  void submitAfterCloseFails() {
    round.submit(1, "B", 100L);
    round.closeAndScore(Set.of(1));
    assertFalse(round.submit(2, "A", 200L));
  }
}
