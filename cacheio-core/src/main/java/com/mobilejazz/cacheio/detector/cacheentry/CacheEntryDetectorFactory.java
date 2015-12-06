package com.mobilejazz.cacheio.detector.cacheentry;

import com.mobilejazz.cacheio.manager.entity.StoreObject;
import java.util.List;

public class CacheEntryDetectorFactory {

  public static CacheValueStrategy obtain(List<StoreObject> storeObjecs) {
    StoreObject storeObject = storeObjecs.get(0);

    if (storeObject.getMetaType().equals(List.class.getSimpleName())) {
      return new ListCacheValueStrategy();
    } else if (storeObject.getMetaType().equals(Object.class.getSimpleName())) {
      return new ObjectCacheValueStrategy();
    } else {
      return new CacheValueStrategy.NullCacheValueStrategy();
    }
  }

  private CacheEntryDetectorFactory() {
    throw new AssertionError("No instances");
  }
}
