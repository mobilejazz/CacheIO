package com.mobilejazz.cacheio.detector.storeobject;

import com.mobilejazz.cacheio.Serializer;
import com.mobilejazz.cacheio.manager.entity.CacheEntry;
import com.mobilejazz.cacheio.manager.entity.StoreObject;
import com.mobilejazz.cacheio.manager.entity.StoreObjectBuilder;
import java.util.Collections;
import java.util.List;

public class ObjectTypeValueStrategy implements TypeValueStrategy {

  @Override public List<StoreObject> convert(Serializer serializer, CacheEntry cacheEntry) {
    byte[] bytes = serializer.toBytes(cacheEntry.getValue());

    StoreObject storeObject = new StoreObjectBuilder()
        .setKey(cacheEntry.getKey())
        .setType(cacheEntry.getType().getCanonicalName())
        .setValue(bytes)
        .setExpiryMillis(cacheEntry.getExpiryMillis())
        .setMetaType(Object.class.getSimpleName())
        .setTimestamp(System.currentTimeMillis())
        .build();

    return Collections.singletonList(storeObject);
  }
}
