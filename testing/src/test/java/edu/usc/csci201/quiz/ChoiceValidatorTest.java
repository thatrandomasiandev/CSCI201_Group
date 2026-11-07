package edu.usc.csci201.quiz;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("ChoiceValidator")
class ChoiceValidatorTest {

  private final ChoiceValidator validator = new ChoiceValidator(Set.of("A", "B", "C", "D"));

  @ParameterizedTest
  @ValueSource(strings = {"A", "a", " A ", "b", "D"})
  void acceptsListedChoicesIgnoringCaseAndTrim(String choice) {
    assertTrue(validator.isAllowed(choice));
  }

  @ParameterizedTest
  @ValueSource(strings = {"E", "1", "", "  ", "AB"})
  void rejectsUnlistedOrBlank(String choice) {
    assertFalse(validator.isAllowed(choice));
  }

  @Test
  void rejectsNull() {
    assertFalse(validator.isAllowed(null));
  }

  @Test
  void emptyAllowedSetRejected() {
    assertThrows(IllegalArgumentException.class, () -> new ChoiceValidator(Set.of()));
  }
}
