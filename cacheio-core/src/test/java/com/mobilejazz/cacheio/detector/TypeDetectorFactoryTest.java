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
import com.mobilejazz.cacheio.detector.storeobject.ListTypeValueStrategy;
import com.mobilejazz.cacheio.detector.storeobject.ObjectTypeValueStrategy;
import com.mobilejazz.cacheio.detector.storeobject.TypeDetectorFactory;
import com.mobilejazz.cacheio.detector.storeobject.TypeValueStrategy;
import com.mobilejazz.cacheio.model.UserTestModelNotSerializable;
import java.util.ArrayList;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

public class TypeDetectorFactoryTest extends ApplicationTestCase {

  @Before public void setUp() throws Exception {
  }

  @Test public void shouldReturnAListTypeValueStrategy() throws Exception {
    List<UserTestModelNotSerializable> users = fakeListOfUsers();

    TypeValueStrategy valueStrategy = TypeDetectorFactory.obtain(users);

    Assertions.assertThat(valueStrategy).isInstanceOf(ListTypeValueStrategy.class);
  }

  @Test public void shouldReturnAObjectTypeValueStrategy() throws Exception {
    UserTestModelNotSerializable user = fakeUser();

    TypeValueStrategy valueStrategy = TypeDetectorFactory.obtain(user);

    Assertions.assertThat(valueStrategy).isInstanceOf(ObjectTypeValueStrategy.class);
  }

  @Test public void shouldReturnANullTypeValueStrategy() throws Exception {
    UserTestModelNotSerializable user = null;

    TypeValueStrategy valueStrategy = TypeDetectorFactory.obtain(user);

    Assertions.assertThat(valueStrategy).isInstanceOf(TypeValueStrategy.NullTypeValueStrategy.class);
  }

  private List<UserTestModelNotSerializable> fakeListOfUsers() {
    List<UserTestModelNotSerializable> collection = new ArrayList<>();

    for (int i = 0; i < 5; i++) {
      collection.add(fakeUser());
    }

    return collection;
  }

  private UserTestModelNotSerializable fakeUser() {
    UserTestModelNotSerializable user = new UserTestModelNotSerializable();
    user.setId(1);
    user.setName("Jose Luis");

    return user;
  }
}
