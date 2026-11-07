package edu.usc.csci201.quiz;

import java.security.SecureRandom;
import java.util.Objects;
import java.util.Set;

/** Generates short alphanumeric join codes unique among active rooms. */
public final class RoomCodes {

  public static final int DEFAULT_LENGTH = 6;
  private static final char[] ALPHABET =
      "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();

  private final SecureRandom random;
  private final int length;

  public RoomCodes() {
    this(new SecureRandom(), DEFAULT_LENGTH);
  }

  public RoomCodes(SecureRandom random, int length) {
    if (length < 4) {
      throw new IllegalArgumentException("length must be at least 4");
    }
    this.random = Objects.requireNonNull(random, "random");
    this.length = length;
  }

  /** Returns a new code not present in {@code activeCodes}. */
  public String generateUnique(Set<String> activeCodes) {
    Objects.requireNonNull(activeCodes, "activeCodes");
    for (int attempt = 0; attempt < 10_000; attempt++) {
      String code = generateOnce();
      if (!activeCodes.contains(code)) {
        return code;
      }
    }
    throw new IllegalStateException("unable to generate unique room code");
  }

  String generateOnce() {
    char[] buf = new char[length];
    for (int i = 0; i < length; i++) {
      buf[i] = ALPHABET[random.nextInt(ALPHABET.length)];
    }
    return new String(buf);
  }
}
