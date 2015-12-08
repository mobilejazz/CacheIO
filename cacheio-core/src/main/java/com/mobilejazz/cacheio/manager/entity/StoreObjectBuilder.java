package com.mobilejazz.cacheio.manager.entity;

public class StoreObjectBuilder {
  private String key;
  private String type;
  private byte[] value;
  private long expiryMillis;
  private String index;
  private String metaType;
  private long timestamp;

  public StoreObjectBuilder setKey(String key) {
    this.key = key;
    return this;
  }

  public StoreObjectBuilder setType(String type) {
    this.type = type;
    return this;
  }

  public StoreObjectBuilder setValue(byte[] value) {
    this.value = value;
    return this;
  }

  public StoreObjectBuilder setExpiryMillis(long expiryMillis) {
    this.expiryMillis = expiryMillis;
    return this;
  }

  public StoreObjectBuilder setIndex(String index) {
    this.index = index;
    return this;
  }

  public StoreObjectBuilder setMetaType(String metaType) {
    this.metaType = metaType;
    return this;
  }

  public StoreObjectBuilder setTimestamp(long timestamp) {
    this.timestamp = timestamp;
    return this;
  }

  public StoreObject build() {
    return new StoreObject(key, type, value, expiryMillis, index, metaType, timestamp);
  }
}