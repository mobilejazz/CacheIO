package com.mobilejazz.cacheio.detector.cacheentry;

import com.mobilejazz.cacheio.manager.entity.CacheEntry;
import com.mobilejazz.cacheio.manager.entity.StoreObject;
import java.util.List;

public class ListCacheValueStrategy implements CacheValueStrategy {
  @Override public <T> CacheEntry<T> convert(List<StoreObject> storeObjects) {
    return null;
  }
}
