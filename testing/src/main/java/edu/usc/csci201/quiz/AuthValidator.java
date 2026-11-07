package edu.usc.csci201.quiz;

import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

/** Registration and login field rules used by the account servlets. */
public final class AuthValidator {

  private static final Pattern EMAIL =
      Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

  private AuthValidator() {}

  public static String normalizeUsername(String username) {
    Objects.requireNonNull(username, "username");
    return username.trim().toLowerCase(Locale.ROOT);
  }

  public static boolean isValidUsername(String username) {
    if (username == null) {
      return false;
    }
    String n = normalizeUsername(username);
    return n.length() >= 3 && n.length() <= 32 && n.matches("[a-z0-9_]+");
  }

  public static boolean isValidEmail(String email) {
    if (email == null) {
      return false;
    }
    String trimmed = email.trim();
    return EMAIL.matcher(trimmed).matches();
  }

  public static boolean isValidPassword(String password) {
    return password != null && password.length() >= 8 && password.length() <= 72;
  }

  public static boolean credentialsMatch(String storedHash, String candidateHash) {
    Objects.requireNonNull(storedHash, "storedHash");
    Objects.requireNonNull(candidateHash, "candidateHash");
    return storedHash.equals(candidateHash);
  }
}
