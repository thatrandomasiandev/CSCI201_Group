package edu.usc.csci201.quiz;

/** Server-side deadline check for answer acceptance. */
public final class DeadlineGate {

  private final long deadlineEpochMs;

  public DeadlineGate(long deadlineEpochMs) {
    this.deadlineEpochMs = deadlineEpochMs;
  }

  /** Accepts answers whose receipt time is strictly at or before the deadline. */
  public boolean accepts(long receiptEpochMs) {
    return receiptEpochMs <= deadlineEpochMs;
  }

  public long deadlineEpochMs() {
    return deadlineEpochMs;
  }
}
