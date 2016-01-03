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
import com.mobilejazz.cacheio.manager.entity.StoreObject;
import com.mobilejazz.cacheio.model.UserTestModelSerializable;
import com.mobilejazz.cacheio.serializer.JavaSerializer;
import com.mobilejazz.cacheio.serializer.Serializer;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

public class ObjectTypeValueStrategyTest extends ApplicationTestCase {

  public static final long FAKE_EXPIRY_MILLIS = 1000L;
  private Serializer serializer;
  private ObjectTypeValueStrategy objectTypeValueStrategy;

  @Before public void setUp() throws Exception {
    serializer = new JavaSerializer();
    objectTypeValueStrategy = new ObjectTypeValueStrategy();
  }

  @Test public void shouldReturnASingleStoreObject() throws Exception {
    CacheEntry<UserTestModelSerializable> cacheEntry =
        CacheEntry.create("test.user", UserTestModelSerializable.class, fakeUserTestModel(),
            FAKE_EXPIRY_MILLIS);

    List<StoreObject> storeObjects = objectTypeValueStrategy.convert(serializer, cacheEntry);

    Assertions.assertThat(storeObjects).hasSize(1);
  }

  @Test public void shouldReturnAStoreObjectProperlyConverted() throws Exception {
    // Given
    UserTestModelSerializable userTestModel = fakeUserTestModel();
    CacheEntry<UserTestModelSerializable> cacheEntry =
        CacheEntry.create("test.user", UserTestModelSerializable.class, userTestModel,
            FAKE_EXPIRY_MILLIS);

    // When
    List<StoreObject> storeObjects = objectTypeValueStrategy.convert(serializer, cacheEntry);
    StoreObject storeObjectExpected = storeObjects.get(0);

    Class<?> classType = Class.forName(storeObjectExpected.getType());

    // Then
    Assertions.assertThat(storeObjectExpected.getKey()).isSameAs(cacheEntry.getKey());
    Assertions.assertThat(storeObjectExpected.getType())
        .isSameAs(UserTestModelSerializable.class.getCanonicalName());
    Assertions.assertThat(storeObjectExpected.getExpiredMillis()).isEqualTo(1000);
    Assertions.assertThat(storeObjectExpected.getIndex()).isNotNull();
    Assertions.assertThat(storeObjectExpected.getIndex()).isNotEmpty();
    Assertions.assertThat(storeObjectExpected.getValue()).isNotNull();
    Assertions.assertThat(storeObjectExpected.getValue()).isNotEmpty();
    Assertions.assertThat(storeObjectExpected.getMetaType()).isNotNull();
    Assertions.assertThat(storeObjectExpected.getMetaType()).isEqualTo(Object.class.getSimpleName());
  }

  private UserTestModelSerializable fakeUserTestModel() {
    UserTestModelSerializable userTestModel = new UserTestModelSerializable();
    userTestModel.setId(1);
    userTestModel.setName("Jose Luis Franconetti");

    return userTestModel;
  }
}
