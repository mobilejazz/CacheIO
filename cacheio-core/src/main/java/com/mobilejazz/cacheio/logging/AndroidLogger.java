package com.mobilejazz.cacheio.logging;

import android.util.Log;

public class AndroidLogger implements Logger {
  @Override public void v(String tag, String message, Object... args) {
    Log.v(tag, message);
  }

  @Override public void v(Throwable t, String tag, String message, Object... args) {
    Log.v(tag, message, t);
  }

  @Override public void d(String tag, String message, Object... args) {
    Log.d(tag, message);
  }

  @Override public void d(Throwable t, String tag, String message, Object... args) {
    Log.d(tag, message, t);
  }

  @Override public void i(String tag, String message, Object... args) {
    Log.i(tag, message);
  }

  @Override public void i(Throwable t, String tag, String message, Object... args) {
    Log.i(tag, message, t);
  }

  @Override public void w(String tag, String message, Object... args) {
    Log.w(tag, message);
  }

  @Override public void w(Throwable t, String tag, String message, Object... args) {
    Log.w(tag, message, t);
  }

  @Override public void e(String tag, final String message, Object... args) {
    Log.e(tag, message);
  }

  @Override public void e(Throwable t, String tag, String message, Object... args) {
    Log.e(tag, message, t);
  }
}
