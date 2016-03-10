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

package com.mobilejazz.cacheio.alternative;

import android.content.Context;
import com.mobilejazz.cacheio.alternative.caches.SQLiteRxCache;
import com.mobilejazz.cacheio.alternative.mappers.KeyMapper;
import com.mobilejazz.cacheio.alternative.mappers.ValueMapper;
import com.mobilejazz.cacheio.alternative.mappers.VersionMapper;
import com.mobilejazz.cacheio.alternative.mappers.defaults.IntegerKeyMapper;
import com.mobilejazz.cacheio.alternative.mappers.defaults.LongKeyMapper;
import com.mobilejazz.cacheio.alternative.mappers.defaults.StringKeyMapper;
import com.mobilejazz.cacheio.alternative.wrappers.FutureCacheWrapper;

import java.util.*;
import java.util.concurrent.*;

import static com.mobilejazz.cacheio.internal.helper.Preconditions.checkArgument;
import static com.mobilejazz.cacheio.internal.helper.Preconditions.checkIsEmpty;

public class CacheIO {

  private Builder config;

  private CacheIO(Builder config) {
    this.config = config;
  }

  @SuppressWarnings("unchecked")
  public <K, V> RxCache<K, V> newRxCache(Class<K> keyType, Class<V> valueType) {

    final KeyMapper<K> keyMapper = (KeyMapper<K>) config.keyMappers.get(keyType);
    checkArgument(keyMapper, "A key mapper was not found for type = " + keyMapper.getClass() + "."
        + " You must register a key mapper for this type when building CacheIO");

    return SQLiteRxCache.newBuilder(keyType, valueType)
        .setExecutor(config.executor)
        .setKeyMapper(keyMapper)
        .setVersionMapper((VersionMapper<V>) config.versionMappers.get(valueType))
        .setValueMapper(config.valueMapper)
        .setDatabase(null)
        .build();

  }

  public <K, V> FutureCache<K, V> newFutureCache(Class<K> keyType, Class<V> valueType) {
    return FutureCacheWrapper.newBuilder(keyType, valueType)
        .setDelegate(newRxCache(keyType, valueType))
        .build();
  }

  public static Builder with(Context context) {
    return new Builder(context);
  }

  public static class Builder {

    private final Context context;

    private Map<Class<?>, KeyMapper<?>> keyMappers = new HashMap<>();
    private Map<Class<?>, VersionMapper<?>> versionMappers = new HashMap<>();

    private ValueMapper valueMapper;

    private String identifier;
    private Executor executor;

    public Builder(Context context) {
      this.context = context;
    }

    private Builder(Builder proto) {
      this.context = proto.context;
      this.keyMappers = new HashMap<>(proto.keyMappers);
      this.valueMapper = proto.valueMapper;
      this.versionMappers = new HashMap<>(proto.versionMappers);
      this.identifier = proto.identifier;
      this.executor = proto.executor;
    }

    public <T> Builder setKeyMapper(Class<T> type, KeyMapper<T> keyMapper) {
      keyMappers.put(type, keyMapper);
      return this;
    }

    public <T> Builder setVersionMapper(Class<T> type, VersionMapper<T> versionMapper) {
      versionMappers.put(type, versionMapper);
      return this;
    }

    public Builder setValueMapper(ValueMapper valueMapper) {
      this.valueMapper = valueMapper;
      return this;
    }

    public Builder identifier(String identifier) {
      this.identifier = identifier;
      return this;
    }

    public Builder executor(Executor executor) {
      this.executor = executor;
      return this;
    }

    public CacheIO build() {

      // defaults

      setKeyMapper(String.class, new StringKeyMapper());
      setKeyMapper(Integer.class, new IntegerKeyMapper());
      setKeyMapper(Long.class, new LongKeyMapper());

      // assertions
      checkArgument(executor, "Executor cannot be null");
      checkIsEmpty(identifier, "Identifier cannot be null or empty");
      checkArgument(context, "Context cannot be null");

      // create database

      return new CacheIO(new Builder(this));
    }
  }

}
