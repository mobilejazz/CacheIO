package com.mobilejazz.cacheio.strategy.timestamp;

import com.mobilejazz.cacheio.strategy.CachingStrategy;

public class TimestampCachingStrategy<T extends TimestampCachingObject>
    implements CachingStrategy<T> {

  @Override public boolean isValid(T data) {
    return data != null && validate(data);
  }

  private boolean validate(T data) {
    long difference = System.currentTimeMillis() - data.getTimestamp();
    return difference < data.getExpiredMillis();
  }
}
