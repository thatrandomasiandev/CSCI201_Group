package edu.usc.csci201.quiz;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("DeadlineGate (unit / black-box)")
class DeadlineGateTest {

  @Test
  void acceptsOnOrBeforeDeadline() {
    DeadlineGate gate = new DeadlineGate(1_000L);
    assertTrue(gate.accepts(999L));
    assertTrue(gate.accepts(1_000L));
  }

  @Test
  void rejectsAfterDeadline() {
    DeadlineGate gate = new DeadlineGate(1_000L);
    assertFalse(gate.accepts(1_001L));
  }
}
