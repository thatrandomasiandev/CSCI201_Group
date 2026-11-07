package edu.usc.csci201.quiz;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Ranking (unit / black-box)")
class RankingTest {

  @Test
  void higherScoreRanksFirst() {
    List<Ranking.Entry> ordered =
        Ranking.order(
            List.of(
                new Ranking.Entry(1, "A", 50, 0),
                new Ranking.Entry(2, "B", 90, 1)));
    assertEquals(2, ordered.get(0).playerId());
    assertEquals(1, ordered.get(1).playerId());
  }

  @Test
  void equalScoresBreakByJoinOrder() {
    List<Ranking.Entry> ordered =
        Ranking.order(
            List.of(
                new Ranking.Entry(10, "Late", 100, 2),
                new Ranking.Entry(11, "Early", 100, 0),
                new Ranking.Entry(12, "Mid", 100, 1)));
    assertEquals(List.of(11, 12, 10), ordered.stream().map(Ranking.Entry::playerId).toList());
  }
}
