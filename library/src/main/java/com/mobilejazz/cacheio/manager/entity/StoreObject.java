package com.mobilejazz.cacheio.manager.entity;

import android.content.ContentValues;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.mobilejazz.cacheio.manager.table.CacheTableMeta;
import com.mobilejazz.cacheio.strategy.timestamp.TimestampCachingObject;

public class StoreObject implements TimestampCachingObject {

  private String key;

  private String type;

  private byte[] value;

  private String index;

  private long timestamp;

  public StoreObject() {
  }

  public StoreObject(String key, @NonNull String type, @NonNull byte[] value, String index,
      long timestamp) {
    this.key = key;
    this.type = type;
    this.value = value;
    this.timestamp = timestamp;

    if (index == null) {
      this.index = key;
    } else {
      this.index = index;
    }
  }

  public static StoreObject create(@NonNull String key, @NonNull String type, byte[] value,
      String index) {
    return new StoreObject(key, type, value, index, System.currentTimeMillis());
  }

  public static String generateIndex(String key, String index) {
    StringBuilder builder = new StringBuilder();
    builder.append(key);
    builder.append("-");
    builder.append(index);
    return builder.toString();
  }

  public static ContentValues toContentValues(StoreObject storeObject) {
    ContentValues contentValues = new ContentValues();
    contentValues.put(CacheTableMeta.COLUMN_KEY, storeObject.getKey());
    contentValues.put(CacheTableMeta.COLUMN_TYPE, storeObject.getType());
    contentValues.put(CacheTableMeta.COLUMN_VALUE, storeObject.getValue());
    contentValues.put(CacheTableMeta.COLUMN_INDEX, storeObject.getIndex());
    contentValues.put(CacheTableMeta.COLUMN_TIMESTAMP, storeObject.getTimestamp());

    return contentValues;
  }

  @Nullable public String getKey() {
    return key;
  }

  @NonNull public String getType() {
    return type;
  }

  @NonNull public byte[] getValue() {
    return value;
  }

  @Nullable public String getIndex() {
    return index;
  }

  @Override public long getTimestamp() {
    return timestamp;
  }
}
