package com.mobilejazz.cacheio;

import java.util.*;
import java.util.concurrent.*;

public interface SyncCache<K, V> {

  V get(K key);

  V put(K key, V value, long expiry, TimeUnit unit);

  K remove(K key);

  Map<K, V> getAll(Collection<K> keys);

  Map<K, V> putAll(Map<K, V> map, long expiry, TimeUnit unit);

  Collection<K> removeAll(Collection<K> keys);

}

