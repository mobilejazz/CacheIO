package com.mobilejazz.cacheio.alternative;

import com.mobilejazz.cacheio.exceptions.SerializerException;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Scheduler;
import rx.Single;

public interface Cache<K, V> {

    Single<V> put(K key, V value, long expiry, TimeUnit unit);

    Single<V> put(Scheduler scheduler, K key, V value, long expiry, TimeUnit unit);

    Single<V> get(K key);

    Single<V> get(Scheduler scheduler, K key);

    Single<K> remove(K key);

    Single<K> remove(Scheduler scheduler, K key);


    Single<Map<K, V>> getAll(Collection<K> keys);

    Single<Map<K, V>> getAll(Scheduler scheduler, Collection<K> keys);

    Single<Map<K, V>> putAll(Map<K, V> map, long expiry, TimeUnit unit);

    Single<Map<K, V>> putAll(Scheduler scheduler, Map<K, V> map, long expiry, TimeUnit unit);

    Single<Collection<K>> removeAll(Collection<K> keys);

    Single<Collection<K>> removeAll(Scheduler scheduler, Collection<K> keys);

    interface Mapper {

        void write(Object value, OutputStream out) throws SerializerException;

        <T> T read(Class<T> type, InputStream in) throws SerializerException;

    }

    interface KeyMapper<T> {

        String toString(T key);

        T fromString(String str);

    }
}
