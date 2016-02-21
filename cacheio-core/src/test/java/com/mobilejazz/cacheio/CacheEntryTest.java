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

import android.support.annotation.NonNull;
import com.mobilejazz.cacheio.manager.entity.CacheEntry;
import com.mobilejazz.cacheio.model.UserTestModel;
import org.assertj.core.api.Assertions;
import org.junit.Test;

public class CacheEntryTest extends ApplicationTestCase {

  public static final String FAKE_KEY = "fake.key";
  public static final int FAKE_EXPIRY_MILLIS = 1000;

  @Test public void shouldCreateTheCacheEntryProperly() throws Exception {
    UserTestModel userTestModel = fakeUserTestModel();
    CacheEntry<UserTestModel> cacheEntry =
        CacheEntry.create(FAKE_KEY, UserTestModel.class, userTestModel, FAKE_EXPIRY_MILLIS);

    Assertions.assertThat(cacheEntry).isNotNull();
    Assertions.assertThat(cacheEntry.getKey()).isEqualTo(FAKE_KEY);
    Assertions.assertThat(cacheEntry.getType()).isEqualTo(UserTestModel.class);
    Assertions.assertThat(cacheEntry.getValue()).isEqualTo(userTestModel);
    Assertions.assertThat(cacheEntry.getExpiryMillis()).isEqualTo(FAKE_EXPIRY_MILLIS);
    Assertions.assertThat(cacheEntry.getTypeCannonicalName())
        .isEqualTo(UserTestModel.class.getCanonicalName());
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowIllegalArgumentWhenKeyIsNull() throws Exception {
    UserTestModel userTestModel = fakeUserTestModel();

    CacheEntry<UserTestModel> cacheEntry =
        CacheEntry.create(null, UserTestModel.class, userTestModel, FAKE_EXPIRY_MILLIS);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowIllegalArgumentWhenTypeIsNull() throws Exception {
    UserTestModel userTestModel = fakeUserTestModel();

    CacheEntry<UserTestModel> cacheEntry =
        CacheEntry.create(FAKE_KEY, null, userTestModel, FAKE_EXPIRY_MILLIS);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowIllegalArgumentWhenValueIsNull() throws Exception {
    CacheEntry<UserTestModel> cacheEntry =
        CacheEntry.create(FAKE_KEY, UserTestModel.class, null, FAKE_EXPIRY_MILLIS);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowIllegalArgumentWhenExpiryMillisIsInvalid() throws Exception {
    UserTestModel userTestModel = fakeUserTestModel();

    CacheEntry<UserTestModel> cacheEntry =
        CacheEntry.create(FAKE_KEY, UserTestModel.class, userTestModel, -23);
  }

  @NonNull private UserTestModel fakeUserTestModel() {
    return new UserTestModel();
  }
}
