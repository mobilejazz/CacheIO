package com.mobilejazz.cacheio.alternative.wrappers;

import com.mobilejazz.cacheio.alternative.AsyncCache;
import com.mobilejazz.cacheio.alternative.RxCache;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import rx.Scheduler;
import rx.Single;
import rx.schedulers.Schedulers;

import static com.mobilejazz.cacheio.internal.helper.Preconditions.checkNotNull;


public class AsyncCacheWrapper<K, V> implements AsyncCache<K, V> {

    private final Builder<K, V> config;

    private AsyncCacheWrapper(Builder<K, V> config) {
        this.config = config;
    }

    private <T> Future<T> future(Single<T> single){
        return single.toObservable().observeOn(config.scheduler).toBlocking().toFuture();
    }

    @Override
    public Future<V> get(K key) {
        return future(config.delegate.get(key));
    }

    @Override
    public Future<V> put(K key, V value, long expiry, TimeUnit unit) {
        return future(config.delegate.put(key, value, expiry, unit));
    }

    @Override
    public Future<K> remove(K key) {
        return future(config.delegate.remove(key));
    }

    @Override
    public Future<Map<K, V>> getAll(Collection<K> keys) {
        return future(config.delegate.getAll(keys));
    }

    @Override
    public Future<Map<K, V>> putAll(Map<K, V> map, long expiry, TimeUnit unit) {
        return future(config.delegate.putAll(map, expiry, unit));
    }

    @Override
    public Future<Collection<K>> removeAll(Collection<K> keys) {
        return future(config.delegate.removeAll(keys));
    }

    public static <K, V> Builder<K, V> newBuilder(Class<K> keyType, Class<V> valueType){
        return new Builder<K, V>()
                .setKeyType(keyType)
                .setValueType(valueType);
    }

    private static final class Builder<K, V> {

        private Executor executor;
        private RxCache<K, V> delegate;

        private Class<K> keyType;
        private Class<V> valueType;

        private Scheduler scheduler;

        private Builder(){
        }

        private Builder(Builder<K, V> proto){
            this.executor = proto.executor;
            this.delegate = proto.delegate;
            this.keyType = proto.keyType;
            this.valueType = proto.valueType;
            this.scheduler = proto.scheduler;
        }

        public Builder<K, V> setExecutor(Executor executor) {
            this.executor = executor;
            return this;
        }

        public Builder<K, V> setDelegate(RxCache<K, V> delegate) {
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

        public AsyncCacheWrapper<K, V> build(){

            checkNotNull(delegate, "Delegate cannot be null");

            checkNotNull(keyType, "Key type cannot be null");
            checkNotNull(valueType, "Value type cannot be null");

            if(executor == null){
                scheduler = Schedulers.immediate();
            } else {
                scheduler = Schedulers.from(executor);
            }

            return new AsyncCacheWrapper<>(this);
        }
    }
}
