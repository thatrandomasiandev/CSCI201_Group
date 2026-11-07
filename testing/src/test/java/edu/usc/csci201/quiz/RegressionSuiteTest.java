package edu.usc.csci201.quiz;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Regression suite: end-to-end style checks of previously fixed behaviors.
 * Re-run this class after each change so known bugs do not return.
 */
@DisplayName("Regression suite")
class RegressionSuiteTest {

  @Test
  void REG_001_changingAnswerDoesNotInflateLiveCount() {
    AnswerTracker tracker = new AnswerTracker();
    tracker.submit(1, "A");
    tracker.submit(1, "B");
    tracker.submit(1, "C");
    assertEquals(1, tracker.answeredCount());
  }

  @Test
  void REG_002_lateAnswerRejectedThenFreezeKeepsPriorAnswer() {
    DeadlineGate gate = new DeadlineGate(5_000L);
    AnswerTracker tracker = new AnswerTracker();
    assertTrue(gate.accepts(4_000L));
    tracker.submit(1, "A");
    assertFalse(gate.accepts(5_001L));
    tracker.freeze();
    assertFalse(tracker.submit(1, "B"));
    assertEquals("A", tracker.answerOf(1));
  }

  @Test
  void REG_003_tieBreakUsesJoinOrderNotSubmissionOrder() {
    List<Ranking.Entry> ordered =
        Ranking.order(
            List.of(
                new Ranking.Entry(2, "Second", 100, 1),
                new Ranking.Entry(1, "First", 100, 0)));
    assertEquals(1, ordered.get(0).playerId());
  }

  @Test
  void REG_004_duplicateDisplayNamesBlockedCaseInsensitively() {
    Set<String> taken = new HashSet<>();
    taken.add(DisplayNames.uniquenessKey("Sam"));
    assertFalse(DisplayNames.isAvailable("sam", taken));
    assertFalse(DisplayNames.isAvailable("SAM", taken));
    assertTrue(DisplayNames.isAvailable("Sam2", taken));
  }

  @Test
  void REG_005_incorrectStillZeroAfterFastResponse() {
    assertEquals(0, Scoring.pointsForAnswer(false, 1));
  }
}
