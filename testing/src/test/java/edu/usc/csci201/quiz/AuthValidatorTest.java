package edu.usc.csci201.quiz;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("AuthValidator")
class AuthValidatorTest {

  @ParameterizedTest
  @ValueSource(strings = {"alice", "bob_1", "user123"})
  void acceptsValidUsernames(String username) {
    assertTrue(AuthValidator.isValidUsername(username));
  }

  @ParameterizedTest
  @ValueSource(strings = {"ab", "", "Bad Name", "has-dash", "has space", "!!!", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"})
  void rejectsInvalidUsernames(String username) {
    assertFalse(AuthValidator.isValidUsername(username));
  }

  @Test
  void normalizesUsername() {
    assertEquals("alice", AuthValidator.normalizeUsername("  Alice  "));
  }

  @Test
  void validatesEmail() {
    assertTrue(AuthValidator.isValidEmail("jjt_373@usc.edu"));
    assertFalse(AuthValidator.isValidEmail("not-an-email"));
    assertFalse(AuthValidator.isValidEmail(""));
  }

  @Test
  void validatesPasswordLength() {
    assertTrue(AuthValidator.isValidPassword("password1"));
    assertFalse(AuthValidator.isValidPassword("short"));
    assertFalse(AuthValidator.isValidPassword(null));
  }

  @Test
  void credentialsMatchIsExact() {
    assertTrue(AuthValidator.credentialsMatch("hash", "hash"));
    assertFalse(AuthValidator.credentialsMatch("hash", "other"));
  }
}
