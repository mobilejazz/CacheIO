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

import com.mobilejazz.cacheio.detector.cacheentry.CacheEntryDetectorFactory;
import com.mobilejazz.cacheio.detector.cacheentry.ObjectCacheValueStrategy;
import com.mobilejazz.cacheio.detector.storeobject.ObjectTypeValueStrategy;
import com.mobilejazz.cacheio.detector.storeobject.TypeDetectorFactory;
import com.mobilejazz.cacheio.exceptions.CacheErrorException;
import com.mobilejazz.cacheio.exceptions.CacheNotFoundException;
import com.mobilejazz.cacheio.logging.AndroidLogger;
import com.mobilejazz.cacheio.logging.LogLevel;
import com.mobilejazz.cacheio.logging.Logger;
import com.mobilejazz.cacheio.manager.entity.CacheEntry;
import com.mobilejazz.cacheio.model.UserTestModel;
import com.mobilejazz.cacheio.persistence.Persistence;
import com.mobilejazz.cacheio.persistence.sqlbrite.PersistenceSQLBrite;
import com.mobilejazz.cacheio.serializer.JavaSerializer;
import com.mobilejazz.cacheio.serializer.Serializer;
import java.util.Collections;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@PrepareForTest({ CacheEntryDetectorFactory.class, TypeDetectorFactory.class })
@RunWith(PowerMockRunner.class) public class CacheManagerTest extends ApplicationTestCase {

  public static final String FAKE_KEY = "fake.key";
  public static final byte[] FAKE_VALUE = new byte[] {};
  public static final String FAKE_TYPE = "fake.type";
  public static final long FAKE_EXPIRY_MILLIS = 1000;
  public static final String FAKE_INDEX = "fake.index";
  public static final String FAKE_METATYPE = "fake.metatype";
  public static final long FAKE_TIMESTAMP = 10000;
  public static final String FAKE_QUERY = "fake.query";

  private Cache cache;

  private Persistence persistence;
  private Serializer serializer;

  @Before public void setUp() throws Exception {
    persistence = mock(PersistenceSQLBrite.class);
    serializer = mock(JavaSerializer.class);
    Logger logger = mock(AndroidLogger.class);

    cache = new CacheManager(persistence, serializer, logger, LogLevel.NONE);
  }

  @Test(expected = CacheNotFoundException.class) public void shouldThrowACacheNotFoundException()
      throws Exception {
    when(persistence.obtain(FAKE_KEY)).thenThrow(CacheNotFoundException.class);

    CacheEntry<Object> cacheEntry = cache.obtain(FAKE_KEY);
  }

  @Test(expected = CacheErrorException.class) public void shouldThrowACacheErrorException()
      throws Exception {
    when(persistence.obtain(FAKE_KEY)).thenThrow(CacheErrorException.class);

    CacheEntry<Object> cacheEntry = cache.obtain(FAKE_KEY);
  }

  @Test public void shouldCallToPersistenceObtainWhenCacheObtainIsCalled() throws Exception {
    // Given
    when(persistence.obtain(FAKE_KEY)).thenReturn(Collections.EMPTY_LIST);

    ObjectCacheValueStrategy objectCacheValueStrategy = mock(ObjectCacheValueStrategy.class);
    PowerMockito.mockStatic(CacheEntryDetectorFactory.class);
    PowerMockito.when(CacheEntryDetectorFactory.obtain(anyList()))
        .thenReturn(objectCacheValueStrategy);

    // When
    cache.obtain(FAKE_KEY);

    // Then
    verify(persistence).obtain(FAKE_KEY);
  }

  @Test public void shouldCallToPersistenceDeleteWhenCacheDeleteIsCalled() throws Exception {
    // Given
    when(persistence.delete(FAKE_KEY)).thenReturn(true);

    // When
    cache.delete(FAKE_KEY);

    // Then
    verify(persistence).delete(FAKE_KEY);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowIllegalArgumentExceptionWhenCacheEntryIsNull() throws Exception {
    // When
    cache.persist(null);
  }

  @Test public void shouldCallToPersistencePersistWhenTheCachePersistMethodIsCalled()
      throws Exception {
    // Given
    CacheEntry<UserTestModel> cacheEntry = provideCacheEntry();

    ObjectTypeValueStrategy objectCacheValueStrategy = mock(ObjectTypeValueStrategy.class);
    PowerMockito.mockStatic(TypeDetectorFactory.class);
    PowerMockito.when(TypeDetectorFactory.obtain(cacheEntry.getValue()))
        .thenReturn(objectCacheValueStrategy);

    when(persistence.persist(anyList())).thenReturn(true);

    // When
    boolean result = cache.persist(cacheEntry);

    // Then
    verify(persistence).persist(anyList());
    Assertions.assertThat(result).isTrue();
  }

  @Test public void shouldNotPersistACacheEntryIfPersistencePersitMethodThrowException()
      throws Exception {
    // Given
    when(persistence.persist(anyList())).thenThrow(CacheErrorException.class);

    // When
    boolean result = cache.persist(provideCacheEntry());

    // Then
    Assertions.assertThat(result).isFalse();
  }

  private CacheEntry<UserTestModel> provideCacheEntry() {
    return CacheEntry.create(FAKE_KEY, UserTestModel.class, new UserTestModel(),
        FAKE_EXPIRY_MILLIS);
  }
}
