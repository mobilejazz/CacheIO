package com.mobilejazz.cacheio.alternative;

import com.mobilejazz.cacheio.exceptions.SerializerException;

import java.io.InputStream;
import java.io.OutputStream;
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

    Single<Map<K, V>> getAll(K... keys);

    Single<Map<K, V>> getAll(Scheduler scheduler, K... keys);

    Single<Map<K, V>> putAll(Map<K, V> map, long expiry, TimeUnit unit);

    Single<Map<K, V>> putAll(Scheduler scheduler, Map<K, V> map, long expiry, TimeUnit unit);

    Single<K[]> removeAll(K... keys);

    Single<K[]> removeAll(Scheduler scheduler, K... keys);

    interface Mapper {

        void write(Object value, OutputStream out) throws SerializerException;

        <T> T read(Class<T> type, InputStream in) throws SerializerException;

    }

    interface KeyMapper<T> {

        String toString(T key);

        T fromString(String str);

    }
}
