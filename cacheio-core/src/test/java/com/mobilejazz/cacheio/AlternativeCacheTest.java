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

import android.content.Context;

import com.mobilejazz.cacheio.logging.LogLevel;
import com.mobilejazz.cacheio.serializer.JavaSerializer;
import com.mobilejazz.cacheio.serializer.Serializer;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;

public class AlternativeCacheTest extends ApplicationTestCase {

  public static final String FAKE_IDENTIFIER = "fake.identifier";
  private Context context;

  private Serializer serializer;

  @Before public void setUp() throws Exception {
    context = mock(Context.class);
    serializer = mock(JavaSerializer.class);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowExceptionIfSerializerIsNull() throws Exception {
    CacheIO cacheIO = CacheIO.with(context)
        .identifier(FAKE_IDENTIFIER)
        .logLevel(LogLevel.FULL)
        .serializer(null)
        .build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowExceptionIfIdentifierIsNull() throws Exception {
    CacheIO cacheIO = CacheIO.with(context)
        .identifier(null)
        .logLevel(LogLevel.FULL)
        .serializer(serializer)
        .build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowExceptionIfIdentifierIsEmpty() throws Exception {
    CacheIO cacheIO =
        CacheIO.with(context).identifier("").logLevel(LogLevel.FULL).serializer(serializer).build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowExceptionIfLogLevelIsNull() throws Exception {
    CacheIO cacheIO = CacheIO.with(context)
        .identifier(FAKE_IDENTIFIER)
        .logLevel(null)
        .serializer(serializer)
        .build();
  }

  @Test(expected = IllegalArgumentException.class) public void shouldThrowExceptionIfContextIsNull()
      throws Exception {
    CacheIO cacheIO = CacheIO.with(null)
        .identifier(FAKE_IDENTIFIER)
        .logLevel(LogLevel.FULL)
        .serializer(serializer)
        .build();
  }

  @Test public void shouldCreateAValidCacheObject() throws Exception {
    CacheIO cacheIO = CacheIO.with(context)
        .identifier(FAKE_IDENTIFIER)
        .logLevel(LogLevel.FULL)
        .serializer(serializer)
        .build();

    Cache cache = cacheIO.cacheDataSource();

    Assertions.assertThat(cache).isNotNull();
  }

  @Test public void shouldCreateDiferentsCacheIOInstanceWithBuildMethod() throws Exception {
    CacheIO.Builder builder = new CacheIO.Builder(context);
    builder.identifier(FAKE_IDENTIFIER);
    builder.logLevel(LogLevel.FULL);
    builder.serializer(serializer);

    CacheIO cacheIOOne = builder.build();
    cacheIOOne.cacheDataSource();

    CacheIO cacheIOTwo = builder.build();
    cacheIOOne.cacheDataSource();

    Assertions.assertThat(cacheIOOne).isNotEqualTo(cacheIOTwo);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowExceptionIfCallToBuildWithoutArguments() throws Exception {
    CacheIO.with(context).build();
  }
}
