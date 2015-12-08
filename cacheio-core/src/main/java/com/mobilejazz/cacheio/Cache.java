package com.mobilejazz.cacheio;

import com.mobilejazz.cacheio.exceptions.CacheErrorException;
import com.mobilejazz.cacheio.exceptions.CacheNotFoundException;
import com.mobilejazz.cacheio.exceptions.ExpiredCacheException;
import com.mobilejazz.cacheio.manager.entity.CacheEntry;

public interface Cache {

  /**
   * Obtain a @link CacheEntry class with the data
   *
   * @param key - Key of the @link CacheEntry
   * @return @link CacheEntry
   */
  <T> CacheEntry<T> obtain(String key)
      throws CacheNotFoundException, ExpiredCacheException, CacheErrorException;

  /**
   * Save a @link CacheEntry into the data base
   *
   * @param entry - @link CacheEntry to save
   */
  <T> boolean persist(CacheEntry<T> entry) throws CacheErrorException;

  /**
   * Remove the @link CacheEntry of the data base
   *
   * @param key - Key of the @link CacheEntry
   */
  boolean delete(String key) throws CacheErrorException;

/*  *//**
   * Check if the cache object is valid or not depending of the @CachingStrategy and if is not
   * valid
   * we throw a @link InvalidCacheException and we remove the current @link CacheEntry of the data
   * base.
   *
   * @param storeObject Object to check if is valid or not
   * @param strategy Type of strategy to validate the cache
   * @param <T> Should be a object of @CachingStrategyObject
   * @throws InvalidCacheException - If the @link CacheEntry is not valid and we need to refresh
   *//*
  <T extends CachingStrategyObject> void executeValidation(StoreObject storeObject,
      CachingStrategy<T> strategy) throws InvalidCacheException;*/
}
