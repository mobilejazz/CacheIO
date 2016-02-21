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

package com.mobilejazz.cacheio.detector;

import com.mobilejazz.cacheio.ApplicationTestCase;
import com.mobilejazz.cacheio.detector.cacheentry.CacheEntryDetectorFactory;
import com.mobilejazz.cacheio.detector.cacheentry.CacheValueStrategy;
import com.mobilejazz.cacheio.detector.cacheentry.ListCacheValueStrategy;
import com.mobilejazz.cacheio.detector.cacheentry.ObjectCacheValueStrategy;
import com.mobilejazz.cacheio.manager.entity.StoreObject;
import com.mobilejazz.cacheio.manager.entity.StoreObjectBuilder;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

public class CacheEntryDetectorFactoryTest extends ApplicationTestCase {

  public static final String FAKE_KEY = "fake.key";
  public static final String FAKE_INDEX = "fake.index";
  public static final String FAKE_TYPE = "fake.type";
  public static final String FAKE_METATYPE = "fake.metatype";
  public static final byte[] FAKE_VALUE = new byte[123];
  public static final int FAKE_EXPIRY_MILLIS = 5400;
  public static final int FAKE_TIMESTAMP = 1234;

  @Before public void setUp() throws Exception {
    // Nothing to set up
  }

  @Test public void shouldReturnAListCacheValueStrategy() throws Exception {
    StoreObject storeObject = new StoreObjectBuilder().setKey(FAKE_KEY)
        .setIndex(FAKE_INDEX)
        .setType(FAKE_TYPE)
        .setMetaType(List.class.getSimpleName())
        .setValue(FAKE_VALUE)
        .setExpiryMillis(FAKE_EXPIRY_MILLIS)
        .setTimestamp(FAKE_TIMESTAMP)
        .build();

    List<StoreObject> storeObjects = Arrays.asList(storeObject, storeObject);

    CacheValueStrategy cacheValueStrategy = CacheEntryDetectorFactory.obtain(storeObjects);

    Assertions.assertThat(cacheValueStrategy).isInstanceOf(ListCacheValueStrategy.class);
  }

  @Test public void shouldReturnAObjectCacheValueStrategy() throws Exception {
    StoreObject storeObject = new StoreObjectBuilder().setKey(FAKE_KEY)
        .setIndex(FAKE_INDEX)
        .setType(FAKE_TYPE)
        .setMetaType(Object.class.getSimpleName())
        .setValue(FAKE_VALUE)
        .setExpiryMillis(FAKE_EXPIRY_MILLIS)
        .setTimestamp(FAKE_TIMESTAMP)
        .build();

    List<StoreObject> storeObjects = Collections.singletonList(storeObject);

    CacheValueStrategy cacheValueStrategy = CacheEntryDetectorFactory.obtain(storeObjects);

    Assertions.assertThat(cacheValueStrategy).isInstanceOf(ObjectCacheValueStrategy.class);
  }

  @Test public void shouldReturnANullCacheValueStrategy() throws Exception {
    StoreObject storeObject = new StoreObjectBuilder().setKey(FAKE_KEY)
        .setIndex(FAKE_INDEX)
        .setType(FAKE_TYPE)
        .setMetaType(null)
        .setValue(FAKE_VALUE)
        .setExpiryMillis(FAKE_EXPIRY_MILLIS)
        .setTimestamp(FAKE_TIMESTAMP)
        .build();

    List<StoreObject> storeObjects = Collections.singletonList(storeObject);

    CacheValueStrategy cacheValueStrategy = CacheEntryDetectorFactory.obtain(storeObjects);

    Assertions.assertThat(cacheValueStrategy)
        .isInstanceOf(CacheValueStrategy.NullCacheValueStrategy.class);
  }
}
