package com.mobilejazz.cacheio.alternative;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public interface FutureCache<K, V> {

    Future<V> get(K key);

    Future<V> put(K key, V value, long expiry, TimeUnit unit);

    Future<K> remove(K key);

    Future<Map<K, V>> getAll(Collection<K> keys);

    Future<Map<K, V>> putAll(Map<K, V> map, long expiry, TimeUnit unit);

    Future<Collection<K>> removeAll(Collection<K> keys);

}

