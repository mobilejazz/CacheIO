package com.mobilejazz.cacheio.detector.cacheentry;

import android.support.annotation.Nullable;
import com.mobilejazz.cacheio.Serializer;
import com.mobilejazz.cacheio.manager.entity.CacheEntry;
import com.mobilejazz.cacheio.manager.entity.StoreObject;
import java.util.List;

public interface CacheValueStrategy {

  class NullCacheValueStrategy implements CacheValueStrategy {

    // TODO: 23/11/15 Do not return a null, to avoid null checks in the contract
    @Override @Nullable public <T> CacheEntry<T> convert(Serializer serializer, List<StoreObject> storeObjects) {
      return null;
    }
  }

  <T> CacheEntry<T> convert(Serializer serializer, List<StoreObject> storeObjects);
}
