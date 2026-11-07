package edu.usc.csci201.quiz;

/**
 * Round scoring for the real-time multiplayer quiz.
 * Correct answers earn a time-adjusted score; incorrect or missing answers earn zero.
 */
public final class Scoring {

  /** Milliseconds of response time that subtract one point from a correct answer. */
  public static final int DEFAULT_K = 200;

  private Scoring() {}

  /**
   * Computes points for a single question.
   *
   * @param correct whether the final accepted answer matches the key
   * @param responseMs time from question start to accepted answer; ignored if incorrect
   * @param k time-penalty constant; must be positive
   * @return points in {@code [0, 100]}
   */
  public static int pointsForAnswer(boolean correct, long responseMs, int k) {
    if (!correct) {
      return 0;
    }
    if (k <= 0) {
      throw new IllegalArgumentException("k must be positive");
    }
    if (responseMs < 0) {
      throw new IllegalArgumentException("responseMs must be non-negative");
    }
    long penalty = responseMs / k;
    long raw = 100L - penalty;
    return (int) Math.max(0L, raw);
  }

  public static int pointsForAnswer(boolean correct, long responseMs) {
    return pointsForAnswer(correct, responseMs, DEFAULT_K);
  }
}
