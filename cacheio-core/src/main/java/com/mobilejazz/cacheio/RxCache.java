package com.mobilejazz.cacheio;

import rx.Single;

import java.util.*;
import java.util.concurrent.*;

public interface RxCache<K, V> {

  Single<V> get(K key);

  Single<V> put(K key, V value, long expiry, TimeUnit unit);

  Single<K> remove(K key);

  Single<Map<K, V>> getAll(Collection<K> keys);

  Single<Map<K, V>> putAll(Map<K, V> map, long expiry, TimeUnit unit);

  Single<Collection<K>> removeAll(Collection<K> keys);

}
