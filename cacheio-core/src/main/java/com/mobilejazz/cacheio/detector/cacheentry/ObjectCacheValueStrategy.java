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

package com.mobilejazz.cacheio.detector.cacheentry;

import com.mobilejazz.cacheio.manager.entity.CacheEntry;
import com.mobilejazz.cacheio.manager.entity.CacheEntryBuilder;
import com.mobilejazz.cacheio.manager.entity.StoreObject;
import com.mobilejazz.cacheio.serializer.Serializer;

import java.util.List;

public class ObjectCacheValueStrategy implements CacheValueStrategy {

  @SuppressWarnings("unchecked") @Override
  public <T> CacheEntry<T> convert(Serializer serializer, List<StoreObject> storeObjects) {
    StoreObject storeObject = storeObjects.get(0);

    String type = storeObject.getType();

    try {
      Class classType = Class.forName(type);

      T object = (T) serializer.fromBytes(storeObject.getValue(), classType);

      CacheEntry<T> cacheEntry = new CacheEntryBuilder().setKey(storeObject.getKey())
          .setType(classType)
          .setValue(object)
          .setExpiryMillis(storeObject.getExpiredMillis())
          .build();
      //cacheEntry.setKey(storeObject.getKey());
      //cacheEntry.setType(classType);
      //cacheEntry.setValue(object);
      //cacheEntry.setExpiryMillis(storeObject.getExpiredMillis());

      return cacheEntry;
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
      // TODO: 24/11/15 We should not return a null pointer we should make some cast to a CacheEntry or throw a exception
      return null;
    }
  }
}
