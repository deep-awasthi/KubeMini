package com.kubemini.common.util;

import java.security.SecureRandom;

public final class Ids {
  private static final SecureRandom RANDOM = new SecureRandom();
  private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz0123456789";

  private Ids() {}

  public static String prefixed(String prefix) {
    StringBuilder value = new StringBuilder(prefix).append("-");
    for (int i = 0; i < 10; i++) {
      value.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
    }
    return value.toString();
  }
}
