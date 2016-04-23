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

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import com.mobilejazz.cacheio.alternative.CacheIO;
import com.mobilejazz.cacheio.alternative.SyncCache;
import com.mobilejazz.cacheio.model.DummyUser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.*;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(AndroidJUnit4.class) public class CacheIOTests {

  public static final int FAKE_USER_ID = 1234;
  public static final String FAKE_USER_NAME = "Jose Luis";
  public static final String FAKE_DUMMY_USER_KEY = "test.dummy";

  private CacheIO cacheIO;

  @Before public void setUp() throws Exception {
    //cacheIO = new CacheIO.Builder(InstrumentationRegistry.getContext())
    //    .identifier("")
    cacheIO = CacheIO.with(InstrumentationRegistry.getContext())
        .identifier("cacheio_test")
        .executor(Executors.newSingleThreadExecutor())
        .build();
  }

  @Test public void shouldGetAObject() throws Exception {
    SyncCache<String, DummyUser> syncCache = givenADummyUserSyncCache();

    syncCache.put(FAKE_DUMMY_USER_KEY, givenDummyUser(), Long.MAX_VALUE, TimeUnit.SECONDS);

    DummyUser dummyUser = syncCache.get(FAKE_DUMMY_USER_KEY);

    assertThat(dummyUser).isNotNull();
    assertThat(dummyUser.getId()).isEqualTo(FAKE_USER_ID);
    assertThat(dummyUser.getName()).isEqualTo(FAKE_USER_NAME);
  }

  @Test public void shouldGetAllTheObjects() throws Exception {
    // Given
    SyncCache<String, DummyUser> syncCache = givenADummyUserSyncCache();

    DummyUser dummyUserOne = givenDummyUser();
    DummyUser dummyUserTwo = givenDummyUser();

    Map<String, DummyUser> dummyUserMap = new HashMap<>();
    dummyUserMap.put("fake.dummy.user.one", dummyUserOne);
    dummyUserMap.put("fake.dummy.user.two", dummyUserTwo);

    syncCache.putAll(dummyUserMap, Long.MAX_VALUE, TimeUnit.SECONDS);

    // When
    ArrayList<String> keys = new ArrayList<>();
    Collections.addAll(keys, "fake.dummy.user.one", "fake.dummy.user.two");

    Map<String, DummyUser> allDummyUsers = syncCache.getAll(keys);

    assertThat(allDummyUsers).isNotNull();
    assertThat(allDummyUsers).isNotEmpty();

    DummyUser fakeDummyUserOne = allDummyUsers.get("fake.dummy.user.one");
    DummyUser fakeDummyUserTwo = allDummyUsers.get("fake.dummy.user.two");

    assertThat(fakeDummyUserOne).isNotNull();
    assertThat(fakeDummyUserTwo).isNotNull();

    assertThat(fakeDummyUserOne.getId()).isEqualTo(dummyUserOne.getId());
    assertThat(fakeDummyUserOne.getName()).isEqualTo(dummyUserOne.getName());

    assertThat(fakeDummyUserTwo.getId()).isEqualTo(fakeDummyUserTwo.getId());
    assertThat(fakeDummyUserTwo.getName()).isEqualTo(fakeDummyUserTwo.getName());
  }

  private SyncCache<String, DummyUser> givenADummyUserSyncCache() {
    return cacheIO.newSyncCache(String.class, DummyUser.class);
  }

  private DummyUser givenDummyUser() {
    return DummyUser.create(FAKE_USER_ID, FAKE_USER_NAME);
  }
}
