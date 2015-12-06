package com.mobilejazz.cacheio.detector.storeobject;

import android.support.annotation.NonNull;
import com.mobilejazz.cacheio.serializer.Serializer;
import com.mobilejazz.cacheio.manager.entity.CacheEntry;
import com.mobilejazz.cacheio.manager.entity.StoreObject;
import java.util.Collections;
import java.util.List;

public interface TypeValueStrategy {

  class NullTypeValueStrategy implements TypeValueStrategy {

    @Override @NonNull public List<StoreObject> convert(Serializer serializer, CacheEntry cacheEntry) {
      return Collections.emptyList();
    }
  }

  List<StoreObject> convert(Serializer serializer, CacheEntry cacheEntry);

}
