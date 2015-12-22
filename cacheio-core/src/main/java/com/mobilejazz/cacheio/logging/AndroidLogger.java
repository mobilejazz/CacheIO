/*
 * Copyright (C) 2015 Mobile Jazz
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

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
