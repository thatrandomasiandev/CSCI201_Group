package edu.usc.csci201.quiz;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("AnswerTracker concurrency (white-box stress)")
class AnswerTrackerConcurrencyTest {

  @Test
  void manyPlayersFirstSubmitEachCountOnce() throws Exception {
    AnswerTracker tracker = new AnswerTracker();
    int players = 50;
    ExecutorService pool = Executors.newFixedThreadPool(16);
    CountDownLatch start = new CountDownLatch(1);
    CountDownLatch done = new CountDownLatch(players);
    AtomicInteger firsts = new AtomicInteger();

    for (int i = 0; i < players; i++) {
      final int playerId = i;
      pool.submit(
          () -> {
            try {
              start.await();
              if (tracker.submit(playerId, "A")) {
                firsts.incrementAndGet();
              }
            } catch (InterruptedException e) {
              Thread.currentThread().interrupt();
            } finally {
              done.countDown();
            }
          });
    }

    start.countDown();
    assertTrue(done.await(5, TimeUnit.SECONDS));
    pool.shutdown();
    assertTrue(pool.awaitTermination(5, TimeUnit.SECONDS));

    assertEquals(players, firsts.get());
    assertEquals(players, tracker.answeredCount());
  }

  @Test
  void rapidReplacesFromOnePlayerKeepCountAtOne() throws Exception {
    AnswerTracker tracker = new AnswerTracker();
    int replaces = 200;
    ExecutorService pool = Executors.newFixedThreadPool(8);
    CountDownLatch start = new CountDownLatch(1);
    CountDownLatch done = new CountDownLatch(replaces);

    for (int i = 0; i < replaces; i++) {
      final String choice = (i % 2 == 0) ? "A" : "B";
      pool.submit(
          () -> {
            try {
              start.await();
              tracker.submit(1, choice);
            } catch (InterruptedException e) {
              Thread.currentThread().interrupt();
            } finally {
              done.countDown();
            }
          });
    }

    start.countDown();
    assertTrue(done.await(5, TimeUnit.SECONDS));
    pool.shutdown();
    assertTrue(pool.awaitTermination(5, TimeUnit.SECONDS));

    assertEquals(1, tracker.answeredCount());
    String answer = tracker.answerOf(1);
    assertTrue("A".equals(answer) || "B".equals(answer));
  }
}
