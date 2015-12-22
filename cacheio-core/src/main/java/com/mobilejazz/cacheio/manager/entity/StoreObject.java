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

import android.content.ContentValues;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.mobilejazz.cacheio.manager.table.CacheTableMeta;
import com.mobilejazz.cacheio.strategy.timestamp.TimestampCachingObject;
import java.util.Arrays;

public class StoreObject implements TimestampCachingObject {

  private String key;

  private String type;

  private byte[] value;

  private long expiryMillis;

  private String index;

  private String metaType;

  private long timestamp;

  /**
   * Constructor
   */
  StoreObject(String key, String type, byte[] value, long expiryMillis, String index,
      String metaType, long timestamp) {
    this.key = key;
    this.type = type;
    this.value = value;
    this.expiryMillis = expiryMillis;
    this.index = index;
    this.metaType = metaType;
    this.timestamp = timestamp;

    if (index == null) {
      this.index = key;
    } else {
      this.index = index;
    }
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

  @Override public long getExpiredMillis() {
    return expiryMillis;
  }

  public String getMetaType() {
    return metaType;
  }

  // Public methods

  public static String generateIndex(String key, String index) {
    StringBuilder builder = new StringBuilder(key.length() + index.length() + 1);
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
    contentValues.put(CacheTableMeta.COLUMN_EXPIRY_MILLIS, storeObject.getExpiredMillis());
    contentValues.put(CacheTableMeta.COLUMN_INDEX, storeObject.getIndex());
    contentValues.put(CacheTableMeta.COLUMN_METATYPE, storeObject.getMetaType());
    contentValues.put(CacheTableMeta.COLUMN_TIMESTAMP, storeObject.getTimestamp());

    return contentValues;
  }

  @Override public String toString() {
    return "StoreObject{" +
        "key='" + key + '\'' +
        ", type='" + type + '\'' +
        ", value=" + Arrays.toString(value) +
        ", expiryMillis=" + expiryMillis +
        ", index='" + index + '\'' +
        ", metaType='" + metaType + '\'' +
        ", timestamp=" + timestamp +
        '}';
  }
}
