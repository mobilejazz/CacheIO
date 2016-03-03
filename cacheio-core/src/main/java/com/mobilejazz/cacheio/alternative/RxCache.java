package com.mobilejazz.cacheio.alternative;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import rx.Single;

public interface RxCache<K, V> {

    Single<V> get(K key);

    Single<V> put(K key, V value, long expiry, TimeUnit unit);

    Single<K> remove(K key);

    Single<Map<K, V>> getAll(Collection<K> keys);

    Single<Map<K, V>> putAll(Map<K, V> map, long expiry, TimeUnit unit);

    Single<Collection<K>> removeAll(Collection<K> keys);

}
