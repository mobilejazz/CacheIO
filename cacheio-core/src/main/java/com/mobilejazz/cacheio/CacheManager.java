/*
 * Copyright (C) 2015 Mobile Jazz
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
import com.mobilejazz.cacheio.detector.cacheentry.CacheValueStrategy;
import com.mobilejazz.cacheio.detector.storeobject.TypeDetectorFactory;
import com.mobilejazz.cacheio.detector.storeobject.TypeValueStrategy;
import com.mobilejazz.cacheio.exceptions.CacheErrorException;
import com.mobilejazz.cacheio.exceptions.CacheNotFoundException;
import com.mobilejazz.cacheio.logging.LogLevel;
import com.mobilejazz.cacheio.logging.Logger;
import com.mobilejazz.cacheio.manager.entity.CacheEntry;
import com.mobilejazz.cacheio.manager.entity.StoreObject;
import com.mobilejazz.cacheio.persistence.Persistence;
import com.mobilejazz.cacheio.serializer.Serializer;
import java.util.List;

public class CacheManager implements Cache {

  private static final String TAG = "CacheIO";

  private final Persistence persistence;
  private final Serializer serializer;
  private final Logger logger;
  private final LogLevel logLevel;

  public CacheManager(Persistence persistence, Serializer serializer, Logger logger,
      LogLevel logLevel) {
    this.persistence = persistence;
    this.serializer = serializer;
    this.logger = logger;
    this.logLevel = logLevel;
  }

  @SuppressWarnings("unchecked") @Override public <T> CacheEntry<T> obtain(String key) {
    try {
      List<StoreObject> storeObjects = persistence.obtain(key);
      CacheValueStrategy strategy = CacheEntryDetectorFactory.obtain(storeObjects);

      if (logLevel == LogLevel.FULL) {
        logger.d(TAG, storeObjects.toString());
      }

      return strategy.convert(serializer, storeObjects);
    } catch (CacheNotFoundException e) {
      throw new CacheNotFoundException();
    } catch (CacheErrorException e) {
      throw new CacheErrorException(e);
    }
  }

  @Override public boolean persist(CacheEntry cacheEntry) {
    if (cacheEntry == null) {
      throw new IllegalArgumentException("cacheEntry == null");
    }

    TypeValueStrategy strategy = TypeDetectorFactory.obtain(cacheEntry.getValue());

    List<StoreObject> storeObjects = strategy.convert(serializer, cacheEntry);
    try {

      if (logLevel == LogLevel.FULL) {
        logger.d(TAG, storeObjects.toString());
      }

      return persistence.persist(storeObjects);
    } catch (CacheErrorException e) {
      e.printStackTrace();
      return false;
    }
  }

  @Override public boolean delete(String key) {
    try {

      if (logLevel == LogLevel.FULL) {
        logger.d(TAG, "Delete object by key: " + key);
      }

      return persistence.delete(key);
    } catch (CacheErrorException e) {
      e.printStackTrace();
      return false;
    }
  }
}
