package edu.usc.csci201.quiz;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/** Final leaderboard ordering: higher score first, then earlier join order. */
public final class Ranking {

  public record Entry(int playerId, String displayName, int totalScore, int joinOrder) {}

  private Ranking() {}

  public static List<Entry> order(List<Entry> players) {
    Objects.requireNonNull(players, "players");
    List<Entry> copy = new ArrayList<>(players);
    copy.sort(
        Comparator.comparingInt(Entry::totalScore)
            .reversed()
            .thenComparingInt(Entry::joinOrder));
    return List.copyOf(copy);
  }
}
