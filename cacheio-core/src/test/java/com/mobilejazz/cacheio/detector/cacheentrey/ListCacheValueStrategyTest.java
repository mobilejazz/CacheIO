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

import android.support.annotation.NonNull;
import com.mobilejazz.cacheio.ApplicationTestCase;
import com.mobilejazz.cacheio.detector.cacheentry.ListCacheValueStrategy;
import com.mobilejazz.cacheio.manager.entity.CacheEntry;
import com.mobilejazz.cacheio.manager.entity.StoreObject;
import com.mobilejazz.cacheio.manager.entity.StoreObjectBuilder;
import com.mobilejazz.cacheio.model.UserTestModelSerializable;
import com.mobilejazz.cacheio.serializer.Serializer;
import java.util.ArrayList;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.when;

public class ListCacheValueStrategyTest extends ApplicationTestCase {

  public static final String FAKE_KEY = "test.fake";
  public static final long FAKE_EXPIRY_MILLIS = 1000L;
  public static final byte[] FAKE_BYTE_VALUE = new byte[] {};
  public static final int FAKE_NUMBER_OF_STORE_OBJECTS = 5;

  @Mock Serializer serializer;

  private ListCacheValueStrategy valueStrategyToTest;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);

    valueStrategyToTest = new ListCacheValueStrategy();
  }

  @Test public void shouldReturnAListWithTheProperlySize() throws Exception {
    // Given
    List<StoreObject> collection = givenAListOfStoreObjects();

    // When
    CacheEntry<List<UserTestModelSerializable>> cacheEntryExpected =
        valueStrategyToTest.convert(serializer, collection);

    List<UserTestModelSerializable> listExpected = cacheEntryExpected.getValue();

    // Then
    Assertions.assertThat(listExpected).isNotNull();
    Assertions.assertThat(listExpected).isNotEmpty();
    Assertions.assertThat(listExpected).hasSize(FAKE_NUMBER_OF_STORE_OBJECTS);
  }

  @Test public void shouldReturnACacheEntryProperlyConverted() throws Exception {
    // Given
    List<StoreObject> storeObjects = givenAListOfStoreObjects();

    // When
    CacheEntry<List<UserTestModelSerializable>> cacheEntryExpected =
        valueStrategyToTest.convert(serializer, storeObjects);

    // Then
    Assertions.assertThat(cacheEntryExpected.getKey()).isEqualTo(FAKE_KEY);
    Assertions.assertThat(cacheEntryExpected.getExpiryMillis()).isEqualTo(FAKE_EXPIRY_MILLIS);
    Assertions.assertThat(cacheEntryExpected.getType()).isSameAs(UserTestModelSerializable.class);
    Assertions.assertThat(cacheEntryExpected.getValue()).isNotEmpty();
    Assertions.assertThat(cacheEntryExpected.getValue()).isNotNull();
  }

  @NonNull private List<StoreObject> givenAListOfStoreObjects() {
    StoreObject storeObject = new StoreObjectBuilder().setIndex(FAKE_KEY)
        .setKey(FAKE_KEY)
        .setExpiryMillis(FAKE_EXPIRY_MILLIS)
        .setType(UserTestModelSerializable.class.getCanonicalName())
        .setMetaType(List.class.getSimpleName())
        .setTimestamp(System.currentTimeMillis())
        .setValue(FAKE_BYTE_VALUE)
        .build();

    List<StoreObject> collection = new ArrayList<>(FAKE_NUMBER_OF_STORE_OBJECTS);
    for (int position = 0; position < FAKE_NUMBER_OF_STORE_OBJECTS; position++) {
      collection.add(storeObject);
    }

    when(serializer.fromBytes(FAKE_BYTE_VALUE, UserTestModelSerializable.class)).thenReturn(
        new UserTestModelSerializable());
    return collection;
  }
}
