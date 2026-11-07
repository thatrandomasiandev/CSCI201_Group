package edu.usc.csci201.quiz;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("RoomCodes (unit / white-box collision path)")
class RoomCodesTest {

  @Test
  void generatedCodeHasConfiguredLengthAndAlphabet() {
    RoomCodes codes = new RoomCodes(new SecureRandom(), 6);
    String code = codes.generateUnique(Set.of());
    assertEquals(6, code.length());
    assertTrue(code.matches("[A-Z0-9]{6}"));
  }

  @Test
  void skipsCodesAlreadyActive() {
    // Force first draw to collide: SecureRandom is hard to stub; instead seed a taken set
    // that includes many codes and verify the result is never in the taken set.
    Set<String> active = new HashSet<>();
    RoomCodes codes = new RoomCodes();
    for (int i = 0; i < 50; i++) {
      active.add(codes.generateUnique(active));
    }
    String next = codes.generateUnique(active);
    assertFalse(active.contains(next));
    assertEquals(6, next.length());
  }
}
