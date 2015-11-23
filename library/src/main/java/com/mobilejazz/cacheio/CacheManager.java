package com.mobilejazz.cacheio;

import com.mobilejazz.cacheio.detector.cacheentry.CacheEntryDetectorFactory;
import com.mobilejazz.cacheio.detector.cacheentry.CacheValueStrategy;
import com.mobilejazz.cacheio.detector.storeobject.TypeDetectorFactory;
import com.mobilejazz.cacheio.detector.storeobject.TypeValueStrategy;
import com.mobilejazz.cacheio.exceptions.CacheErrorException;
import com.mobilejazz.cacheio.exceptions.CacheNotFoundException;
import com.mobilejazz.cacheio.manager.entity.CacheEntry;
import com.mobilejazz.cacheio.manager.entity.StoreObject;
import java.util.List;

public class CacheManager implements Cache {

  private final Persitence persitence;
  private final Serializer serializer;

  public CacheManager(Persitence persitence, Serializer serializer) {
    this.persitence = persitence;
    this.serializer = serializer;
  }

  @SuppressWarnings("unchecked") @Override public <T> CacheEntry<T> obtain(String key) {
    try {
      List<StoreObject> storeObjects = persitence.obtain(key);
      CacheValueStrategy strategy = CacheEntryDetectorFactory.obtain(storeObjects);

      return strategy.convert(serializer, storeObjects);
    } catch (CacheNotFoundException e) {
      throw new CacheNotFoundException();
    } catch (CacheErrorException e) {
      throw new CacheErrorException(e);
    }
  }

  @Override public boolean persist(CacheEntry cacheEntry) {
    if (cacheEntry == null) {
      throw new IllegalArgumentException("cacheEntry == null");
    }

    TypeValueStrategy strategy = TypeDetectorFactory.obtain(cacheEntry.getValue());

    List<StoreObject> storeObjects = strategy.convert(serializer, cacheEntry);
    try {
      return persitence.persist(storeObjects);
    } catch (CacheErrorException e) {
      e.printStackTrace();
      return false;
    }
  }

  @Override public boolean delete(String key) {
    try {
      return persitence.delete(key);
    } catch (CacheErrorException e) {
      e.printStackTrace();
      return false;
    }
  }
}
