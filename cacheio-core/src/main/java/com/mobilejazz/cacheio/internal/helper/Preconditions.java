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

package com.mobilejazz.cacheio.internal.helper;

import android.text.TextUtils;

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

  public static <T> T checkArgument(T object, String message) {
    if (object == null) {
      throw new IllegalArgumentException(message);
    }

    return object;
  }

  public static String checkIsEmpty(String object, String message) {
    if (TextUtils.isEmpty(object)) {
      throw new IllegalArgumentException(message);
    }

    return object;
  }
}
