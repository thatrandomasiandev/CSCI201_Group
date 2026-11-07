package edu.usc.csci201.quiz;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * One timed question: deadline gate, choice validation, answer tracking, and close-out scoring.
 */
public final class QuestionRound {

  public record ScoreResult(int playerId, boolean answered, boolean correct, int points) {}

  private final String correctChoiceNormalized;
  private final DeadlineGate gate;
  private final ChoiceValidator validator;
  private final AnswerTracker tracker = new AnswerTracker();
  private final long startEpochMs;
  private final int k;
  private boolean closed;

  public QuestionRound(
      String correctChoice,
      Set<String> allowedChoices,
      long startEpochMs,
      long deadlineEpochMs,
      int k) {
    Objects.requireNonNull(correctChoice, "correctChoice");
    this.validator = new ChoiceValidator(allowedChoices);
    if (!validator.isAllowed(correctChoice)) {
      throw new IllegalArgumentException("correctChoice must be in allowedChoices");
    }
    this.correctChoiceNormalized = ChoiceValidator.normalize(correctChoice);
    this.gate = new DeadlineGate(deadlineEpochMs);
    this.startEpochMs = startEpochMs;
    this.k = k;
    if (deadlineEpochMs < startEpochMs) {
      throw new IllegalArgumentException("deadline must be at or after start");
    }
  }

  /**
   * @return true if this was the player's first accepted answer for the round
   */
  public synchronized boolean submit(int playerId, String choice, long receiptEpochMs) {
    if (closed || tracker.isFrozen()) {
      return false;
    }
    if (!gate.accepts(receiptEpochMs)) {
      return false;
    }
    if (!validator.isAllowed(choice)) {
      throw new IllegalArgumentException("choice not allowed");
    }
    return tracker.submit(playerId, ChoiceValidator.normalize(choice));
  }

  public synchronized Map<Integer, ScoreResult> closeAndScore(Set<Integer> allPlayerIds) {
    Objects.requireNonNull(allPlayerIds, "allPlayerIds");
    if (closed) {
      throw new IllegalStateException("round already closed");
    }
    tracker.freeze();
    closed = true;
    Map<Integer, ScoreResult> results = new HashMap<>();
    for (int playerId : allPlayerIds) {
      String answer = tracker.answerOf(playerId);
      if (answer == null) {
        results.put(playerId, new ScoreResult(playerId, false, false, 0));
        continue;
      }
      boolean correct = correctChoiceNormalized.equals(answer);
      // receipt time is not stored separately yet; use 0 for instant when unknown — callers
      // that need time penalties should use closeAndScore(playerId, responseMs) overload path
      // via the map built below from stored answers only.
      int points = Scoring.pointsForAnswer(correct, 0L, k);
      results.put(playerId, new ScoreResult(playerId, true, correct, points));
    }
    return Map.copyOf(results);
  }

  /**
   * Closes the round and scores with explicit per-player response times (ms from start).
   */
  public synchronized Map<Integer, ScoreResult> closeAndScore(
      Map<Integer, Long> responseMsByPlayer, Set<Integer> allPlayerIds) {
    Objects.requireNonNull(responseMsByPlayer, "responseMsByPlayer");
    Objects.requireNonNull(allPlayerIds, "allPlayerIds");
    if (closed) {
      throw new IllegalStateException("round already closed");
    }
    tracker.freeze();
    closed = true;
    Map<Integer, ScoreResult> results = new HashMap<>();
    for (int playerId : allPlayerIds) {
      String answer = tracker.answerOf(playerId);
      if (answer == null) {
        results.put(playerId, new ScoreResult(playerId, false, false, 0));
        continue;
      }
      boolean correct = correctChoiceNormalized.equals(answer);
      long responseMs = responseMsByPlayer.getOrDefault(playerId, 0L);
      int points = Scoring.pointsForAnswer(correct, responseMs, k);
      results.put(playerId, new ScoreResult(playerId, true, correct, points));
    }
    return Map.copyOf(results);
  }

  public synchronized int answeredCount() {
    return tracker.answeredCount();
  }

  public synchronized boolean isClosed() {
    return closed;
  }

  public long startEpochMs() {
    return startEpochMs;
  }
}
