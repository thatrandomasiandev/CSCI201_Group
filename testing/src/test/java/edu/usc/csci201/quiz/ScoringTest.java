package edu.usc.csci201.quiz;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Scoring (unit / black-box)")
class ScoringTest {

  @Nested
  @DisplayName("Black-box: inputs and expected outputs")
  class BlackBox {

    @Test
    void incorrectAnswerAlwaysZero() {
      assertEquals(0, Scoring.pointsForAnswer(false, 0));
      assertEquals(0, Scoring.pointsForAnswer(false, 5_000));
    }

    @Test
    void instantCorrectAnswerEarnsFullPoints() {
      assertEquals(100, Scoring.pointsForAnswer(true, 0));
    }

    @Test
    void correctAnswerLosesOnePointPerKMilliseconds() {
      assertEquals(99, Scoring.pointsForAnswer(true, 200, 200));
      assertEquals(75, Scoring.pointsForAnswer(true, 5_000, 200));
    }

    @Test
    void verySlowCorrectAnswerFloorsAtZero() {
      assertEquals(0, Scoring.pointsForAnswer(true, 100_000, 200));
    }
  }

  @Nested
  @DisplayName("White-box: branch and guard coverage")
  class WhiteBox {

    @Test
    void rejectsNonPositiveK() {
      assertThrows(IllegalArgumentException.class, () -> Scoring.pointsForAnswer(true, 100, 0));
      assertThrows(IllegalArgumentException.class, () -> Scoring.pointsForAnswer(true, 100, -1));
    }

    @Test
    void rejectsNegativeResponseTime() {
      assertThrows(IllegalArgumentException.class, () -> Scoring.pointsForAnswer(true, -1));
    }
  }
}
