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

package com.mobilejazz.cacheio.detector.storeobject;

import com.mobilejazz.cacheio.serializer.Serializer;
import com.mobilejazz.cacheio.manager.entity.CacheEntry;
import com.mobilejazz.cacheio.manager.entity.StoreObject;
import com.mobilejazz.cacheio.manager.entity.StoreObjectBuilder;
import java.util.ArrayList;
import java.util.List;

public class ListTypeValueStrategy implements TypeValueStrategy {
  @Override public List<StoreObject> convert(Serializer serializer, CacheEntry cacheEntry) {
    List list = (List) cacheEntry.getValue();
    List<StoreObject> storeObjects = new ArrayList<>(list.size());
    byte[] bytes;
    String index;

    for (int position = 0; position < list.size(); position++) {
      Object object = list.get(position);

      bytes = serializer.toBytes(object);
      index = StoreObject.generateIndex(cacheEntry.getKey(), String.valueOf(position));

      StoreObject storeObject = new StoreObjectBuilder().setKey(cacheEntry.getKey())
          .setType(cacheEntry.getType().getCanonicalName())
          .setValue(bytes)
          .setIndex(index)
          .setExpiryMillis(cacheEntry.getExpiryMillis())
          .setMetaType(List.class.getSimpleName())
          .setTimestamp(System.currentTimeMillis())
          .build();

      storeObjects.add(storeObject);
    }

    return storeObjects;
  }
}
