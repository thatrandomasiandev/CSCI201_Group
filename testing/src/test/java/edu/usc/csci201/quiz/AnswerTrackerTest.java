package edu.usc.csci201.quiz;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("AnswerTracker (unit / white-box replace vs insert)")
class AnswerTrackerTest {

  @Test
  void firstSubmitIncrementsAnsweredCount() {
    AnswerTracker tracker = new AnswerTracker();
    assertTrue(tracker.submit(1, "A"));
    assertEquals(1, tracker.answeredCount());
    assertEquals("A", tracker.answerOf(1));
  }

  @Test
  void replaceDoesNotIncrementAnsweredCount() {
    AnswerTracker tracker = new AnswerTracker();
    tracker.submit(1, "A");
    assertFalse(tracker.submit(1, "B"));
    assertEquals(1, tracker.answeredCount());
    assertEquals("B", tracker.answerOf(1));
  }

  @Test
  void distinctPlayersEachCountOnce() {
    AnswerTracker tracker = new AnswerTracker();
    tracker.submit(1, "A");
    tracker.submit(2, "C");
    assertEquals(2, tracker.answeredCount());
  }

  @Test
  void frozenTrackerRejectsFurtherSubmits() {
    AnswerTracker tracker = new AnswerTracker();
    tracker.submit(1, "A");
    tracker.freeze();
    assertFalse(tracker.submit(2, "B"));
    assertEquals(1, tracker.answeredCount());
    assertTrue(tracker.isFrozen());
  }
}
