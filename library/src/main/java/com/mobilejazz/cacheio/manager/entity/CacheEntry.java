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
  private CacheEntry(String key, Class<T> type, T value, long expiryMillis) {
    this.key = key;
    this.type = type;
    this.value = value;
    this.expiryMillis = expiryMillis;
  }

  public CacheEntry() {
  }

  @SuppressWarnings("unchecked")
  public static <T, V> CacheEntry create(String key, Class<T> type, V value, long expiryMillis) {
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
