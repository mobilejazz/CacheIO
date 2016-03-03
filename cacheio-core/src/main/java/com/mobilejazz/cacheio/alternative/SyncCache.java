package com.mobilejazz.cacheio.alternative;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public interface SyncCache<K, V> {

    V get(K key);

    V put(K key, V value, long expiry, TimeUnit unit);

    K remove(K key);

    Map<K, V> getAll(Collection<K> keys);

    Map<K, V> putAll(Map<K, V> map, long expiry, TimeUnit unit);

    Collection<K> removeAll(Collection<K> keys);

}

