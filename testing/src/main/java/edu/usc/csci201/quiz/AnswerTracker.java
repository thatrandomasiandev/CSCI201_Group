package edu.usc.csci201.quiz;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Tracks one accepted answer per player for the active question.
 * First submit inserts; later submits replace without increasing the answered count.
 */
public final class AnswerTracker {

  private final Map<Integer, String> answersByPlayer = new HashMap<>();
  private final Set<Integer> answeredPlayers = new HashSet<>();
  private boolean frozen;

  public synchronized boolean submit(int playerId, String choice) {
    Objects.requireNonNull(choice, "choice");
    if (frozen) {
      return false;
    }
    if (choice.isBlank()) {
      throw new IllegalArgumentException("choice must be non-blank");
    }
    boolean first = !answeredPlayers.contains(playerId);
    answersByPlayer.put(playerId, choice);
    answeredPlayers.add(playerId);
    return first;
  }

  public synchronized void freeze() {
    frozen = true;
  }

  public synchronized boolean isFrozen() {
    return frozen;
  }

  public synchronized int answeredCount() {
    return answeredPlayers.size();
  }

  public synchronized String answerOf(int playerId) {
    return answersByPlayer.get(playerId);
  }

  public synchronized Set<Integer> answeredPlayerIds() {
    return Collections.unmodifiableSet(new HashSet<>(answeredPlayers));
  }
}
