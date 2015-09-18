package com.mobilejazz.cacheio.strategy.ttl;

import com.mobilejazz.cacheio.strategy.CachingStrategy;
import java.util.concurrent.TimeUnit;

public class TtlCachingStrategy<T extends TtlCachingObject> implements CachingStrategy<T> {

  private final long ttlMillis;

  public TtlCachingStrategy(int ttl, TimeUnit timeUnit) {
    ttlMillis = timeUnit.toMillis(ttl);
  }

  @Override public boolean isValid(T data) {
    return (data.getPersistedTime() + ttlMillis) > System.currentTimeMillis();
  }
}
