package com.mobilejazz.cacheio.strategy.ttl;

import com.mobilejazz.cacheio.strategy.CachingStrategyObject;

public interface TtlCachingObject extends CachingStrategyObject {
  long getPersistedTime();
}
