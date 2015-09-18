package com.mobilejazz.cacheio.manager.entity;

import android.support.annotation.NonNull;

public class CacheEntry<T> {

  private String key; // key string
  private Class<T> type; // type string
  private T value; // bytes blob

  public CacheEntry(String key, Class<T> type, T value) {
    this.key = key;
    this.type = type;
    this.value = value;
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

  public String getTypeCannonicalName() {
    return type.getCanonicalName();
  }

  @SuppressWarnings("unchecked")
  public static <T> CacheEntry create(@NonNull String key, @NonNull Class<?> type, T value) {
    return new CacheEntry(key, type, value);
  }
}
