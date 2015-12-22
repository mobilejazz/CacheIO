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
import com.mobilejazz.cacheio.manager.entity.StoreObject;
import com.mobilejazz.cacheio.serializer.Serializer;
import java.util.ArrayList;
import java.util.List;

public class ListCacheValueStrategy implements CacheValueStrategy {

  @SuppressWarnings("unchecked") @Override
  public <T> CacheEntry<T> convert(Serializer serializer, List<StoreObject> storeObjects) {

    List values = new ArrayList(storeObjects.size());

    String key = null;
    Class type = null;
    long expiryMillis = -1;

    for (StoreObject storeObject : storeObjects) {

      try {
        String typeName = storeObject.getType();
        byte[] value = storeObject.getValue();

        if (key == null) {
          key = storeObject.getKey();
        }

        if (type == null) {
          type = Class.forName(typeName);
        }

        if (expiryMillis == -1) {
          expiryMillis = storeObject.getExpiredMillis();
        }

        T object = (T) serializer.fromBytes(value, type);

        values.add(object);
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
        // TODO: 24/11/15 We should not return a null pointer we should make some cast to a CacheEntry or throw a exception
        return null;
      }
    }

    CacheEntry<T> cacheEntry = new CacheEntry<>();
    cacheEntry.setKey(key);
    cacheEntry.setType(type);
    cacheEntry.setValue((T) values);
    cacheEntry.setExpiryMillis(expiryMillis);

    return cacheEntry;
  }
}
