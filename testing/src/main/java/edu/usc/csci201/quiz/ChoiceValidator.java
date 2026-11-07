package edu.usc.csci201.quiz;

import java.util.Locale;
import java.util.Objects;
import java.util.Set;

/** Rejects answer choices that are not in the active question's option list. */
public final class ChoiceValidator {

  private final Set<String> allowedNormalized;

  public ChoiceValidator(Set<String> allowedChoices) {
    Objects.requireNonNull(allowedChoices, "allowedChoices");
    if (allowedChoices.isEmpty()) {
      throw new IllegalArgumentException("allowedChoices must not be empty");
    }
    java.util.HashSet<String> normalized = new java.util.HashSet<>();
    for (String choice : allowedChoices) {
      String key = normalize(choice);
      if (!key.isEmpty()) {
        normalized.add(key);
      }
    }
    if (normalized.isEmpty()) {
      throw new IllegalArgumentException("allowedChoices must contain a non-blank choice");
    }
    this.allowedNormalized = Set.copyOf(normalized);
  }

  public boolean isAllowed(String choice) {
    if (choice == null) {
      return false;
    }
    return allowedNormalized.contains(normalize(choice));
  }

  static String normalize(String choice) {
    return choice.trim().toUpperCase(Locale.ROOT);
  }
}
