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
