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

package com.mobilejazz.cacheio.manager.entity;

public class CacheEntry<T> {

  /**
   * String key that identifies this object in the storage
   */
  private String key;

  /**
   * Type of the object that you are saving in the storage
   */
  private Class<T> type;

  /**
   * Object to save
   */
  private T value;

  /**
   * Period of time that the object is live in the storage.
   */
  private long expiryMillis;

  /**
   * Private constructor
   */
  CacheEntry(String key, Class<T> type, T value, long expiryMillis) {
    this.key = key;
    this.type = type;
    this.value = value;
    this.expiryMillis = expiryMillis;
  }

  public CacheEntry() {
  }

  @Deprecated @SuppressWarnings("unchecked")
  public static <T, V> CacheEntry<T> create(String key, Class<T> type, V value, long expiryMillis) {
    return new CacheEntry(key, type, value, expiryMillis);
  }

  public String getKey() {
    return key;
  }

  public Class<T> getType() {
    return type;
  }

  public T getValue() {
    return value;
  }

  public long getExpiryMillis() {
    return expiryMillis;
  }

  public String getTypeCannonicalName() {
    return type.getCanonicalName();
  }

  public void setKey(String key) {
    this.key = key;
  }

  public void setType(Class<T> type) {
    this.type = type;
  }

  public void setValue(T value) {
    this.value = value;
  }

  public void setExpiryMillis(long expiryMillis) {
    this.expiryMillis = expiryMillis;
  }
}
