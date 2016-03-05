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

package com.mobilejazz.cacheio.alternative.caches;

import android.content.Context;
import com.mobilejazz.cacheio.ApplicationTestCase;
import com.mobilejazz.cacheio.alternative.RxCache;
import com.mobilejazz.cacheio.alternative.mappers.KeyMapper;
import com.mobilejazz.cacheio.alternative.mappers.ValueMapper;
import com.mobilejazz.cacheio.alternative.mappers.VersionMapper;
import com.mobilejazz.cacheio.alternative.mappers.defaults.NoOpVersionMapper;
import com.mobilejazz.cacheio.alternative.mappers.defaults.StringKeyMapper;
import com.mobilejazz.cacheio.alternative.wrappers.FutureCacheWrapper;
import com.mobilejazz.cacheio.alternative.wrappers.SyncCacheWrapper;
import org.junit.Test;
import org.robolectric.RuntimeEnvironment;

import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class SQLiteRxCacheTest extends ApplicationTestCase {

  @Test(expected = IllegalArgumentException.class) @SuppressWarnings("unchecked")
  public void shouldThrowIllegalArgumentExceptionIfContextIsNull() {
    SQLiteRxCache.newBuilder(String.class, String.class)
        .setDatabaseName("foo")
        .setKeyMapper(mock(KeyMapper.class))
        .setValueMapper(mock(ValueMapper.class))
        .setVersionMapper(mock(VersionMapper.class))
        .setExecutor(mock(Executor.class))
        .setTableName("table")
        .build();
  }

  @Test(expected = IllegalArgumentException.class) @SuppressWarnings("unchecked")
  public void shouldThrowIllegalArgumentExceptionIfDatabaseNameIsNull() {
    SQLiteRxCache.newBuilder(String.class, String.class)
        .setContext(mock(Context.class))
        .setKeyMapper(mock(KeyMapper.class))
        .setValueMapper(mock(ValueMapper.class))
        .setVersionMapper(mock(VersionMapper.class))
        .setExecutor(mock(Executor.class))
        .setTableName("table")
        .build();
  }

  @Test(expected = IllegalArgumentException.class) @SuppressWarnings("unchecked")
  public void shouldThrowIllegalArgumentExceptionIfKeyMapperIsNull() {
    SQLiteRxCache.newBuilder(String.class, String.class)
        .setContext(mock(Context.class))
        .setDatabaseName("db")
        .setValueMapper(mock(ValueMapper.class))
        .setVersionMapper(mock(VersionMapper.class))
        .setExecutor(mock(Executor.class))
        .setTableName("table")
        .build();
  }

  @Test(expected = IllegalArgumentException.class) @SuppressWarnings("unchecked")
  public void shouldThrowIllegalArgumentExceptionIfValueMapperIsNull() {
    SQLiteRxCache.newBuilder(String.class, String.class)
        .setContext(mock(Context.class))
        .setDatabaseName("db")
        .setKeyMapper(mock(KeyMapper.class))
        .setVersionMapper(mock(VersionMapper.class))
        .setExecutor(mock(Executor.class))
        .setTableName("table")
        .build();
  }

  @Test(expected = IllegalArgumentException.class) @SuppressWarnings("unchecked")
  public void shouldThrowIllegalArgumentExceptionIfVersionMapperIsNull() {
    SQLiteRxCache.newBuilder(String.class, String.class)
        .setContext(mock(Context.class))
        .setDatabaseName("db")
        .setKeyMapper(mock(KeyMapper.class))
        .setValueMapper(mock(ValueMapper.class))
        .setExecutor(mock(Executor.class))
        .setTableName("table")
        .build();
  }

  @Test(expected = IllegalArgumentException.class) @SuppressWarnings("unchecked")
  public void shouldThrowIllegalArgumentExceptionIfExecutorIsNull() {
    SQLiteRxCache.newBuilder(String.class, String.class)
        .setContext(mock(Context.class))
        .setDatabaseName("db")
        .setKeyMapper(mock(KeyMapper.class))
        .setValueMapper(mock(ValueMapper.class))
        .setVersionMapper(mock(VersionMapper.class))
        .setTableName("table")
        .build();
  }

  @Test public void testBasicCRUDWithNoVersioning() {

    final TestValueMapper valueMapper = new TestValueMapper();
    final ExecutorService executor = Executors.newSingleThreadExecutor();

    final RxCache<String, TestUser> rxCache = SQLiteRxCache.newBuilder(String.class, TestUser.class)
        .setContext(RuntimeEnvironment.application)
        .setDatabaseName("rxcache_test")
        .setKeyMapper(new StringKeyMapper())
        .setValueMapper(valueMapper)
        .setVersionMapper(new TestUserVersionMapper())
        .setExecutor(executor)
        .setTableName("TestUser")
        .build();

    final FutureCacheWrapper<String, TestUser> futureCache =
        FutureCacheWrapper.newBuilder(String.class, TestUser.class).setDelegate(rxCache).build();

    final SyncCacheWrapper<String, TestUser> cache =
        SyncCacheWrapper.newBuilder(String.class, TestUser.class).setDelegate(futureCache).build();

    // insert

    final TestUser mal =
        new TestUser().setEmail("mal@email.com").setFirstName("Malcolm").setLastName("Reynolds");

    final TestUser castle =
        new TestUser().setEmail("castle@email.com").setFirstName("Richard").setLastName("Castle");

    final TestUser bruce =
        new TestUser().setEmail("bruce@email.com").setFirstName("Bruce").setLastName("Banner");

    assertThat(cache.put(mal.getEmail(), mal, Long.MAX_VALUE, TimeUnit.SECONDS)).isEqualTo(mal);
    assertThat(cache.put(castle.getEmail(), castle, Long.MAX_VALUE, TimeUnit.SECONDS)).isEqualTo(castle);
    assertThat(cache.put(bruce.getEmail(), bruce, Long.MAX_VALUE, TimeUnit.SECONDS)).isEqualTo(bruce);

    assertThat(cache.get(mal.getEmail())).isEqualTo(mal);
    assertThat(cache.get(castle.getEmail())).isEqualTo(castle);
    assertThat(cache.get(bruce.getEmail())).isEqualTo(bruce);

    // update

    mal.setLastName("Foo");

    assertThat(cache.put(mal.getEmail(), mal, Long.MAX_VALUE, TimeUnit.SECONDS)).isEqualTo(mal);
    assertThat(cache.get(mal.getEmail())).isEqualTo(mal);

    // delete

    assertThat(cache.remove(mal.getEmail())).isEqualTo(mal.getEmail());
    assertThat(cache.get(mal.getEmail())).isNull();

  }

  @Test public void testExpiryOfValuesWithNoVersioning() throws InterruptedException {

    final TestValueMapper valueMapper = new TestValueMapper();
    final ExecutorService executor = Executors.newSingleThreadExecutor();

    final RxCache<String, TestUser> rxCache = SQLiteRxCache.newBuilder(String.class, TestUser.class)
        .setContext(RuntimeEnvironment.application)
        .setDatabaseName("rxcache_test")
        .setKeyMapper(new StringKeyMapper())
        .setValueMapper(valueMapper)
        .setVersionMapper(new TestUserVersionMapper())
        .setExecutor(executor)
        .setTableName("TestUser")
        .build();

    final FutureCacheWrapper<String, TestUser> futureCache =
        FutureCacheWrapper.newBuilder(String.class, TestUser.class).setDelegate(rxCache).build();

    final SyncCacheWrapper<String, TestUser> cache =
        SyncCacheWrapper.newBuilder(String.class, TestUser.class).setDelegate(futureCache).build();

    // insert

    final TestUser mal =
        new TestUser().setEmail("mal@email.com").setFirstName("Malcolm").setLastName("Reynolds");

    assertThat(cache.put(mal.getEmail(), mal, 2, TimeUnit.SECONDS)).isEqualTo(mal);
    assertThat(cache.get(mal.getEmail())).isEqualTo(mal);

    // Wait to see it's still returning after a little while
    Thread.sleep(1000);
    assertThat(cache.get(mal.getEmail())).isEqualTo(mal);

    // Wait again and check it is no longer being returned as it should have expired
    Thread.sleep(2000);
    assertThat(cache.get(mal.getEmail())).isNull();

  }

  @Test public void testPutsWithAnOlderVersionAreIgnored() {

    final TestValueMapper valueMapper = new TestValueMapper();
    final ExecutorService executor = Executors.newSingleThreadExecutor();

    final RxCache<String, TestUser> rxCache = SQLiteRxCache.newBuilder(String.class, TestUser.class)
        .setContext(RuntimeEnvironment.application)
        .setDatabaseName("rxcache_test")
        .setKeyMapper(new StringKeyMapper())
        .setValueMapper(valueMapper)
        .setVersionMapper(new TestUserVersionMapper())
        .setExecutor(executor)
        .setTableName("TestUser")
        .build();

    final FutureCacheWrapper<String, TestUser> futureCache =
        FutureCacheWrapper.newBuilder(String.class, TestUser.class).setDelegate(rxCache).build();

    final SyncCacheWrapper<String, TestUser> cache =
        SyncCacheWrapper.newBuilder(String.class, TestUser.class).setDelegate(futureCache).build();

    //

    final TestUser mal = new TestUser()
        .setEmail("mal@email.com")
        .setFirstName("Malcolm")
        .setLastName("Reynolds")
        .setVersion(3L);

    assertThat(cache.put(mal.getEmail(), mal, Long.MAX_VALUE, TimeUnit.SECONDS)).isEqualTo(mal);

    // attempt to write an older version
    mal.setVersion(2L);
    assertThat(cache.put(mal.getEmail(), mal, Long.MAX_VALUE, TimeUnit.SECONDS)).isNull();

    // attempt to write a newer version
    mal.setVersion(4L);
    assertThat(cache.put(mal.getEmail(), mal, Long.MAX_VALUE, TimeUnit.SECONDS)).isEqualTo(mal);
  }

}
