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

package com.mobilejazz.cacheio.detector.cacheentrey;

import com.mobilejazz.cacheio.ApplicationTestCase;
import com.mobilejazz.cacheio.detector.cacheentry.ObjectCacheValueStrategy;
import com.mobilejazz.cacheio.manager.entity.CacheEntry;
import com.mobilejazz.cacheio.manager.entity.StoreObject;
import com.mobilejazz.cacheio.manager.entity.StoreObjectBuilder;
import com.mobilejazz.cacheio.model.UserTestModelSerializable;
import com.mobilejazz.cacheio.serializer.Serializer;
import java.util.Collections;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.when;

public class ObjectCacheValueStrategyTest extends ApplicationTestCase {

  public static final String FAKE_KEY = "test.fake";
  public static final long FAKE_EXPIRY_MILLIS = 1000L;
  public static final byte[] FAKE_BYTE_VALUE = new byte[] {};

  @Mock Serializer serializer;

  private ObjectCacheValueStrategy valueStrategyToTest;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);

    valueStrategyToTest = new ObjectCacheValueStrategy();
  }

  @Test public void shouldReturnACacheEntryProperlyConverted() throws Exception {
    // Given
    StoreObject storeObject = new StoreObjectBuilder().setIndex(FAKE_KEY)
        .setKey(FAKE_KEY)
        .setExpiryMillis(FAKE_EXPIRY_MILLIS)
        .setType(UserTestModelSerializable.class.getCanonicalName())
        .setMetaType(Object.class.getSimpleName())
        .setTimestamp(System.currentTimeMillis())
        .setValue(FAKE_BYTE_VALUE)
        .build();

    when(serializer.fromBytes(FAKE_BYTE_VALUE, UserTestModelSerializable.class)).thenReturn(
        new UserTestModelSerializable());

    // When
    CacheEntry<Object> cacheEntryExpected =
        valueStrategyToTest.convert(serializer, Collections.singletonList(storeObject));

    // Then
    Assertions.assertThat(cacheEntryExpected.getKey()).isEqualTo(storeObject.getKey());
    Assertions.assertThat(cacheEntryExpected.getValue()).isNotNull();
    Assertions.assertThat(cacheEntryExpected.getExpiryMillis()).isEqualTo(FAKE_EXPIRY_MILLIS);
    Assertions.assertThat(cacheEntryExpected.getType()).isSameAs(UserTestModelSerializable.class);
  }
}
