package com.mobilejazz.cacheio.detector.cacheentry;

import com.mobilejazz.cacheio.manager.entity.CacheEntry;
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

      CacheEntry<T> cacheEntry = new CacheEntry<>();
      cacheEntry.setKey(storeObject.getKey());
      cacheEntry.setType(classType);
      cacheEntry.setValue(object);
      cacheEntry.setExpiryMillis(storeObject.getExpiredMillis());

      return cacheEntry;
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
      // TODO: 24/11/15 We should not return a null pointer we should make some cast to a CacheEntry or throw a exception
      return null;
    }
  }
}
