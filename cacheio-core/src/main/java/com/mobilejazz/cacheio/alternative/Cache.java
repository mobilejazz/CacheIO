package com.mobilejazz.cacheio.alternative;

import com.mobilejazz.cacheio.exceptions.SerializerException;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Map;

import rx.Observable;
import rx.Scheduler;
import rx.Single;

public interface Cache<K, V> {

    Single<Map<K, V>> getAll(K... keys);

    Single<Map<K, V>> getAll(Scheduler scheduler, K... keys);

    Single<Void> putAll(Map<K, V> map, Date expiresAt);

    Single<Void> putAll(Scheduler scheduler, Map<K, V> map, Date expiresAt);

    Single<Void> removeAll(K... keys);

    Single<Void> removeAll(Scheduler scheduler, K... keys);

    interface Mapper {

        void write(Object value, OutputStream out) throws SerializerException;

        <T> T read(Class<T> type, InputStream in) throws SerializerException;

    }

    interface KeyMapper<T> {

        String toString(T key);

        T fromString(String str);

    }
}
