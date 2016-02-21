/*
 * Copyright (C) 2016 Mobile Jazz
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

package com.mobilejazz.cacheio.manager.entity;

import android.text.TextUtils;
import com.mobilejazz.cacheio.internal.helper.Preconditions;

import java.util.*;

public class CacheEntryBuilder<T> {

  public static final long NON_EXPIRY_MILLIS = -1;

  private String key;
  private Class<T> type;
  private T value;
  private long expiryMillis;

  public CacheEntryBuilder<T> setKey(String key) {
    this.key = key;
    return this;
  }

  public CacheEntryBuilder<T> setType(Class<T> type) {
    this.type = type;
    return this;
  }

  public CacheEntryBuilder<T> setValue(T value) {
    this.value = value;
    return this;
  }

  public CacheEntryBuilder<T> setExpiryMillis(long expiryMillis) {
    this.expiryMillis = expiryMillis;
    return this;
  }

  public CacheEntry<T> build() {
    checkArguments(key, type, value, expiryMillis);

    if (value instanceof List) {
      this.type = (Class<T>) value.getClass();
    }

    return new CacheEntry<>(key, type, value, expiryMillis);
  }

  private void checkArguments(String key, Class<T> type, T value, long expiryMillis) {
    Preconditions.checkArgument(type, "type == null");
    Preconditions.checkArgument(value, "value == null");

    if (TextUtils.isEmpty(key)) {
      throw new IllegalArgumentException("key == null OR empty");
    }

    if (expiryMillis <= NON_EXPIRY_MILLIS) {
      throw new IllegalArgumentException("expiry millis < -1");
    }
  }
}