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

package com.mobilejazz.cacheio.detector.storeobject;

import com.mobilejazz.cacheio.ApplicationTestCase;
import com.mobilejazz.cacheio.manager.entity.CacheEntry;
import com.mobilejazz.cacheio.manager.entity.CacheEntryBuilder;
import com.mobilejazz.cacheio.manager.entity.StoreObject;
import com.mobilejazz.cacheio.model.UserTestModelSerializable;
import com.mobilejazz.cacheio.serializer.JavaSerializer;
import com.mobilejazz.cacheio.serializer.Serializer;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

public class ListTypeValueStrategyTest extends ApplicationTestCase {

  public static final long FAKE_EXPIRY_MILLIS = 1000L;
  public static final int NUMBER_OF_STORE_OBJECTS = 5;

  private Serializer serializer;
  private ListTypeValueStrategy listTypeValueStrategy;
  public static final String FAKE_KEY = "test.list";

  @Before public void setUp() throws Exception {
    serializer = new JavaSerializer();
    listTypeValueStrategy = new ListTypeValueStrategy();
  }

  @Test public void shouldReturnAListOfStoreObjects() throws Exception {
    // Given
    List<UserTestModelSerializable> userTestModelSerializables =
        fakeListOfUserTestModelSerializables();
    //CacheEntry<UserTestModelSerializable> cacheEntry =
    //    CacheEntry.create("test.list", UserTestModelSerializable.class, userTestModelSerializables,
    //        FAKE_EXPIRY_MILLIS);

    CacheEntry<UserTestModelSerializable> cacheEntry =
        new CacheEntryBuilder<UserTestModelSerializable>().setKey(FAKE_KEY)
            .setType(UserTestModelSerializable.class)
            .setValue(userTestModelSerializables)
            .setExpiryMillis(FAKE_EXPIRY_MILLIS)
            .build();

    // When
    List<StoreObject> storeObjects = listTypeValueStrategy.convert(serializer, cacheEntry);

    // Then
    Assertions.assertThat(storeObjects).hasSize(NUMBER_OF_STORE_OBJECTS);
  }

  @Test public void shouldReturnAListOfStoreObjectsProperlyConverted() throws Exception {
    // Given
    List<UserTestModelSerializable> userTestModelSerializables =
        fakeListOfUserTestModelSerializables();
    CacheEntry<UserTestModelSerializable> cacheEntry =
        CacheEntry.create(FAKE_KEY, UserTestModelSerializable.class, userTestModelSerializables,
            FAKE_EXPIRY_MILLIS);

    // When
    List<StoreObject> storeObjects = listTypeValueStrategy.convert(serializer, cacheEntry);

    // Then
    for (int position = 0; position < storeObjects.size(); position++) {
      StoreObject storeObject = storeObjects.get(position);

      Assertions.assertThat(storeObject.getKey()).isEqualTo(cacheEntry.getKey());
      Assertions.assertThat(storeObject.getExpiredMillis()).isEqualTo(1000);
      Assertions.assertThat(storeObject.getType())
          .isSameAs(UserTestModelSerializable.class.getCanonicalName());
      Assertions.assertThat(storeObject.getMetaType()).isEqualTo(List.class.getSimpleName());
      Assertions.assertThat(storeObject.getValue()).isNotEmpty();
      Assertions.assertThat(storeObject.getValue()).isNotNull();
      Assertions.assertThat(storeObject.getIndex()).isNotEmpty();
      Assertions.assertThat(storeObject.getIndex()).isNotNull();
    }
  }

  private List<UserTestModelSerializable> fakeListOfUserTestModelSerializables() {
    List<UserTestModelSerializable> collection = new ArrayList<>();

    for (int i = 0; i < NUMBER_OF_STORE_OBJECTS; i++) {
      collection.add(fakeUserTestModel(i));
    }

    return collection;
  }

  private UserTestModelSerializable fakeUserTestModel(int id) {
    UserTestModelSerializable userTestModel = new UserTestModelSerializable();
    userTestModel.setId(id);
    userTestModel.setName("Jose Luis Franconetti");

    return userTestModel;
  }
}
