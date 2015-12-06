package com.mobilejazz.cacheio;

import com.mobilejazz.cacheio.exceptions.CacheErrorException;
import com.mobilejazz.cacheio.exceptions.CacheNotFoundException;
import com.mobilejazz.cacheio.exceptions.ExpiredCacheException;
import com.mobilejazz.cacheio.manager.entity.StoreObject;
import com.mobilejazz.cacheio.strategy.CachingStrategy;
import com.mobilejazz.cacheio.strategy.CachingStrategyObject;
import java.util.List;

public interface Persistence {

  List<StoreObject> obtain(String key) throws CacheNotFoundException, CacheErrorException;

  boolean persist(List<StoreObject> value) throws CacheErrorException;

  boolean delete(String key) throws CacheErrorException;

  <T extends CachingStrategyObject> void executeValidation(StoreObject storeObject,
      CachingStrategy<T> strategy) throws ExpiredCacheException, CacheNotFoundException;
}
