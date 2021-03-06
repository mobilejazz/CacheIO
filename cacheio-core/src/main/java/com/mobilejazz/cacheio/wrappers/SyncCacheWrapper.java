package com.mobilejazz.cacheio.wrappers;

import com.mobilejazz.cacheio.FutureCache;
import com.mobilejazz.cacheio.SyncCache;

import java.util.*;
import java.util.concurrent.*;

import static com.mobilejazz.cacheio.helper.Preconditions.checkArgument;

public class SyncCacheWrapper<K, V> implements SyncCache<K, V> {

  private final Builder<K, V> config;

  private SyncCacheWrapper(Builder<K, V> config) {
    this.config = new Builder<>(config);
  }

  private <T> T blocking(Future<T> future) {
    try {
      return future.get();
    } catch (Throwable t) {
      throw new RuntimeException(t);
    }
  }

  @Override public V get(K key) {
    return blocking(config.delegate.get(key));
  }

  @Override public V put(K key, V value, long expiry, TimeUnit unit) {
    return blocking(config.delegate.put(key, value, expiry, unit));
  }

  @Override public K remove(K key) {
    return blocking(config.delegate.remove(key));
  }

  @Override public Map<K, V> getAll(Collection<K> keys) {
    return blocking(config.delegate.getAll(keys));
  }

  @Override public Map<K, V> putAll(Map<K, V> map, long expiry, TimeUnit unit) {
    return blocking(config.delegate.putAll(map, expiry, unit));
  }

  @Override public Collection<K> removeAll(Collection<K> keys) {
    return blocking(config.delegate.removeAll(keys));
  }

  public static <K, V> Builder<K, V> newBuilder(Class<K> keyType, Class<V> valueType) {
    return new Builder<K, V>().setKeyType(keyType).setValueType(valueType);
  }

  public static final class Builder<K, V> {

    private FutureCache<K, V> delegate;

    private Class<K> keyType;
    private Class<V> valueType;

    private Builder() {
    }

    private Builder(Builder<K, V> proto) {
      this.delegate = proto.delegate;
      this.keyType = proto.keyType;
      this.valueType = proto.valueType;
    }

    public Builder<K, V> setDelegate(FutureCache<K, V> delegate) {
      this.delegate = delegate;
      return this;
    }

    protected Builder<K, V> setKeyType(Class<K> keyType) {
      this.keyType = keyType;
      return this;
    }

    protected Builder<K, V> setValueType(Class<V> valueType) {
      this.valueType = valueType;
      return this;
    }

    public SyncCacheWrapper<K, V> build() {

      checkArgument(keyType, "Key type cannot be null");
      checkArgument(valueType, "Value type cannot be null");
      checkArgument(delegate, "Delegate cannot be null");

      return new SyncCacheWrapper<>(this);
    }
  }
}
