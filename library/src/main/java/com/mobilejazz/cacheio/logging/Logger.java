package com.mobilejazz.cacheio.logging;

public interface Logger {

  void v(String tag, String message, Object... args);

  void v(Throwable t, String tag, String message, Object... args);

  void d(String tag, String message, Object... args);

  void d(Throwable t, String tag, String message, Object... args);

  void i(String tag, String message, Object... args);

  void i(Throwable t, String tag, String message, Object... args);

  void w(String tag, String message, Object... args);

  void w(Throwable t, String tag, String message, Object... args);

  void e(String tag, String message, Object... args);

  void e(Throwable t, String tag, String message, Object... args);
}
