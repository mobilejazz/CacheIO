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
import com.mobilejazz.cacheio.alternative.mappers.KeyMapper;
import com.mobilejazz.cacheio.alternative.mappers.ValueMapper;
import com.mobilejazz.cacheio.alternative.mappers.VersionMapper;
import org.junit.Test;

import java.util.concurrent.*;

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


}
