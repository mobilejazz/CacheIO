package com.mobilejazz.cacheio.strategy;

public interface CachingStrategy<T extends CachingStrategyObject> {
  boolean isValid(T data);
}
