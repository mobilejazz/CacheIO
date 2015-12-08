package com.mobilejazz.cacheio.internal.helper;

public class Preconditions {

  private Preconditions() {
    throw new AssertionError("No instances of this class are allowed!");
  }

  public static <T> T checkNotNull(T object, String message) {
    if (object == null) {
      throw new NullPointerException(message);
    }
    return object;
  }
}
