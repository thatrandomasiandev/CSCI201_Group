package edu.usc.csci201.quiz;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@DisplayName("Scoring parameterized black-box table")
class ScoringParameterizedTest {

  @ParameterizedTest(name = "correct={0}, ms={1}, k={2} => {3}")
  @CsvSource({
    "false, 0, 200, 0",
    "false, 9999, 200, 0",
    "true, 0, 200, 100",
    "true, 199, 200, 100",
    "true, 200, 200, 99",
    "true, 399, 200, 99",
    "true, 400, 200, 98",
    "true, 5000, 200, 75",
    "true, 20000, 200, 0",
    "true, 1000, 100, 90",
    "true, 1000, 500, 98"
  })
  void tableDrivenPoints(boolean correct, long ms, int k, int expected) {
    assertEquals(expected, Scoring.pointsForAnswer(correct, ms, k));
  }
}
