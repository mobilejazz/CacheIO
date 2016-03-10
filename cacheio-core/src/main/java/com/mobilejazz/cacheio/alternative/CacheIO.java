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
import com.mobilejazz.cacheio.alternative.mappers.defaults.NoOpVersionMapper;

import java.util.*;
import java.util.concurrent.*;

public class CacheIO {

  private Map<Class<?>, RxCache<?, ?>> rxCaches;
  private Map<Class<?>, SyncCache<?, ?>> syncCaches;
  private Map<Class<?>, FutureCache<?, ?>> futureCaches;

  private Context context;
  private final String identifier;
  private final Executor executor;
  private final Map<Class<?>, Model<?, ?>> models;

  public CacheIO(Context context, String identifier, Executor executor,
      Map<Class<?>, Model<?, ?>> models) {
    this.context = context;
    this.identifier = identifier;
    this.executor = executor;
    this.models = models;

    rxCaches = new HashMap<>();
  }

  public <K, V> RxCache<K, V> rxCache(Class<V> valueType) {
    if (rxCaches.containsKey(valueType)) {
      return (RxCache<K, V>) rxCaches.get(valueType);
    } else {
      Model<K, V> model = (Model<K, V>) models.get(valueType);

      KeyMapper<K> keyMapper = model.getKeyMapper();
      ValueMapper valueMapper = model.getValueMapper();
      Class<K> keyType = model.getKeyType();

      RxCache<K, V> rxCache = SQLiteRxCache.newBuilder(keyType, valueType)
          .setContext(context)
          .setDatabaseName(identifier)
          .setExecutor(executor)
          .setKeyMapper(keyMapper)
          .setValueMapper(valueMapper)
          .setVersionMapper(new NoOpVersionMapper<V>())
          .build();

      rxCaches.put(valueType, rxCache);
      return rxCache;
    }
  }

  public <K, V> SyncCache<K, V> syncCache() {
    return null;
  }

  public <K, V> FutureCache<K, V> futureCache() {
    return null;
  }

  public static Builder with(Context context) {
    return new Builder(context);
  }

  public static class Builder {

    private final Context context;
    private String identifier;
    private Executor executor;

    private Map<Class<?>, Model<?, ?>> models;

    public Builder(Context context) {
      models = new HashMap<>();

      this.context = context;
    }

    public Builder identifier(String identifier) {
      this.identifier = identifier;
      return this;
    }

    public <K, V> Builder mapper(Class<K> keyType, Class<V> valueType, KeyMapper<K> keyMapper,
        ValueMapper valueMapper) {
      Model<K, V> kModel = new Model<>(keyType, valueType, keyMapper, valueMapper);
      models.put(valueType, kModel);
      return this;
    }

    public Builder executor(Executor executor) {
      this.executor = executor;
      return this;
    }

    public CacheIO build() {
      return new CacheIO(context, identifier, executor, models);
    }
  }

  public static class Model<K, V> {

    private final Class<K> keyType;
    private final Class<V> valueType;
    private KeyMapper<K> keyMapper;
    private ValueMapper valueMapper;

    public Model(Class<K> keyType, Class<V> valueType, KeyMapper<K> keyMapper,
        ValueMapper valueMapper) {
      this.keyType = keyType;
      this.valueType = valueType;
      this.keyMapper = keyMapper;
      this.valueMapper = valueMapper;
    }

    public Class<K> getKeyType() {
      return keyType;
    }

    public Class<V> getValueType() {
      return valueType;
    }

    public KeyMapper<K> getKeyMapper() {
      return keyMapper;
    }

    public ValueMapper getValueMapper() {
      return valueMapper;
    }
  }
}
