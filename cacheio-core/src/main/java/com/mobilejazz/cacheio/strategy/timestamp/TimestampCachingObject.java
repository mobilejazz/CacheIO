package com.mobilejazz.cacheio.strategy.timestamp;

import com.mobilejazz.cacheio.strategy.CachingStrategyObject;

public interface TimestampCachingObject extends CachingStrategyObject {
  long getTimestamp();

  long getExpiredMillis();
}
