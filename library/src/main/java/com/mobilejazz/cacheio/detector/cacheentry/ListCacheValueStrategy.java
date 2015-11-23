package com.mobilejazz.cacheio.detector.cacheentry;

import com.mobilejazz.cacheio.Serializer;
import com.mobilejazz.cacheio.manager.entity.CacheEntry;
import com.mobilejazz.cacheio.manager.entity.StoreObject;
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
