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

package com.mobilejazz.cacheio;

import com.mobilejazz.cacheio.manager.entity.StoreObject;
import com.mobilejazz.cacheio.manager.entity.StoreObjectBuilder;
import org.assertj.core.api.Assertions;
import org.junit.Test;

public class StoreObjectTest extends ApplicationTestCase {

  public static final String FAKE_KEY = "fake.key";
  public static final String FAKE_INDEX = "fake.index";
  public static final String FAKE_TYPE = "fake.type";
  public static final String FAKE_METATYPE = "fake.metatype";
  public static final byte[] FAKE_VALUE = new byte[123];
  public static final int FAKE_EXPIRY_MILLIS = 5400;
  public static final int FAKE_TIMESTAMP = 1234;

  @Test public void shouldCreateTheStoreObjectProperly() throws Exception {
    StoreObject storeObject = new StoreObjectBuilder().setKey(FAKE_KEY)
        .setIndex(FAKE_INDEX)
        .setType(FAKE_TYPE)
        .setMetaType(FAKE_METATYPE)
        .setValue(FAKE_VALUE)
        .setExpiryMillis(FAKE_EXPIRY_MILLIS)
        .setTimestamp(FAKE_TIMESTAMP)
        .build();

    Assertions.assertThat(storeObject).isNotNull();
    Assertions.assertThat(storeObject.getKey()).isNotEmpty();
    Assertions.assertThat(storeObject.getIndex()).isNotEmpty();
    Assertions.assertThat(storeObject.getMetaType()).isNotEmpty();
    Assertions.assertThat(storeObject.getValue()).isNotNull();
    Assertions.assertThat(storeObject.getExpiredMillis()).isNotNull();
    Assertions.assertThat(storeObject.getExpiredMillis()).isGreaterThan(-1);
    Assertions.assertThat(storeObject.getTimestamp()).isNotNull();
    Assertions.assertThat(storeObject.getTimestamp()).isGreaterThan(-1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowIllegalArgumentWhenKeyIsNull() throws Exception {
    StoreObject storeObject = new StoreObjectBuilder().setKey(null)
        .setIndex(FAKE_INDEX)
        .setType(FAKE_TYPE)
        .setMetaType(FAKE_METATYPE)
        .setValue(FAKE_VALUE)
        .setExpiryMillis(FAKE_EXPIRY_MILLIS)
        .setTimestamp(FAKE_TIMESTAMP)
        .build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowIllegalArgumentWhenKeyIsEmpty() throws Exception {
    StoreObject storeObject = new StoreObjectBuilder().setKey("")
        .setIndex(FAKE_INDEX)
        .setType(FAKE_TYPE)
        .setMetaType(FAKE_METATYPE)
        .setValue(FAKE_VALUE)
        .setExpiryMillis(FAKE_EXPIRY_MILLIS)
        .setTimestamp(FAKE_TIMESTAMP)
        .build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowIllegalArgumentWhenIndexIsNull() throws Exception {
    StoreObject storeObject = new StoreObjectBuilder().setKey(FAKE_KEY)
        .setIndex(null)
        .setType(FAKE_TYPE)
        .setMetaType(FAKE_METATYPE)
        .setValue(FAKE_VALUE)
        .setExpiryMillis(FAKE_EXPIRY_MILLIS)
        .setTimestamp(FAKE_TIMESTAMP)
        .build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowIllegalArgumentWhenIndexIsEmpty() throws Exception {
    StoreObject storeObject = new StoreObjectBuilder().setKey(FAKE_KEY)
        .setIndex("")
        .setType(FAKE_TYPE)
        .setMetaType(FAKE_METATYPE)
        .setValue(FAKE_VALUE)
        .setExpiryMillis(FAKE_EXPIRY_MILLIS)
        .setTimestamp(FAKE_TIMESTAMP)
        .build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowIllegalArgumentWhenTypeIsNull() throws Exception {
    StoreObject storeObject = new StoreObjectBuilder().setKey(FAKE_KEY)
        .setIndex(FAKE_INDEX)
        .setType(null)
        .setMetaType(FAKE_METATYPE)
        .setValue(FAKE_VALUE)
        .setExpiryMillis(FAKE_EXPIRY_MILLIS)
        .setTimestamp(FAKE_TIMESTAMP)
        .build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowIllegalArgumentWhenTypeIsEmpty() throws Exception {
    StoreObject storeObject = new StoreObjectBuilder().setKey(FAKE_KEY)
        .setIndex(FAKE_INDEX)
        .setType("")
        .setMetaType(FAKE_METATYPE)
        .setValue(FAKE_VALUE)
        .setExpiryMillis(FAKE_EXPIRY_MILLIS)
        .setTimestamp(FAKE_TIMESTAMP)
        .build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowIllegalArgumentWhenValueIsNull() throws Exception {
    StoreObject storeObject = new StoreObjectBuilder().setKey(FAKE_KEY)
        .setIndex(FAKE_INDEX)
        .setType(FAKE_TYPE)
        .setMetaType(FAKE_METATYPE)
        .setValue(null)
        .setExpiryMillis(FAKE_EXPIRY_MILLIS)
        .setTimestamp(FAKE_TIMESTAMP)
        .build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowIllegalArgumentWhenExpiryMillisIsInvalid() throws Exception {
    StoreObject storeObject = new StoreObjectBuilder().setKey(FAKE_KEY)
        .setIndex(FAKE_INDEX)
        .setType(FAKE_TYPE)
        .setMetaType(FAKE_METATYPE)
        .setValue(FAKE_VALUE)
        .setExpiryMillis(-3)
        .setTimestamp(FAKE_TIMESTAMP)
        .build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowIllegalArgumentWhenTimestampIsInvalid() throws Exception {
    StoreObject storeObject = new StoreObjectBuilder().setKey(FAKE_KEY)
        .setIndex(FAKE_INDEX)
        .setType(FAKE_TYPE)
        .setMetaType(FAKE_METATYPE)
        .setValue(FAKE_VALUE)
        .setExpiryMillis(FAKE_EXPIRY_MILLIS)
        .setTimestamp(-23)
        .build();
  }
}
