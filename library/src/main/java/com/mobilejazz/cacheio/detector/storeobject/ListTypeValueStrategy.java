package com.mobilejazz.cacheio.detector.storeobject;

import com.mobilejazz.cacheio.Serializer;
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
