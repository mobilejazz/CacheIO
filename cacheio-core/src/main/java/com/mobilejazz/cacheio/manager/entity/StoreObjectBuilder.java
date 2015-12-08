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