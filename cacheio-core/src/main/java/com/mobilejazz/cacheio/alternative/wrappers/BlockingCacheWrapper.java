package com.mobilejazz.cacheio.alternative.wrappers;

import com.mobilejazz.cacheio.alternative.AsyncCache;
import com.mobilejazz.cacheio.alternative.BlockingCache;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static com.mobilejazz.cacheio.internal.helper.Preconditions.checkNotNull;


public class BlockingCacheWrapper<K, V> implements BlockingCache<K, V> {

    private final Builder<K, V> config;

    private BlockingCacheWrapper(Builder<K, V> config) {
        this.config = config;
    }

    private <T> T blocking(Future<T> future){
        try {
            return future.get();
        } catch (Throwable t){
            throw new RuntimeException(t);
        }
    }

    @Override
    public V get(K key) {
        return blocking(config.delegate.get(key));
    }

    @Override
    public V put(K key, V value, long expiry, TimeUnit unit) {
        return blocking(config.delegate.put(key, value, expiry, unit));
    }

    @Override
    public K remove(K key) {
        return blocking(config.delegate.remove(key));
    }

    @Override
    public Map<K, V> getAll(Collection<K> keys) {
        return blocking(config.delegate.getAll(keys));
    }

    @Override
    public Map<K, V> putAll(Map<K, V> map, long expiry, TimeUnit unit) {
        return blocking(config.delegate.putAll(map, expiry, unit));
    }

    @Override
    public Collection<K> removeAll(Collection<K> keys) {
        return blocking(config.delegate.removeAll(keys));
    }

    public static <K, V> Builder<K, V> newBuilder(Class<K> keyType, Class<V> valueType){
        return new Builder<K, V>()
                .setKeyType(keyType)
                .setValueType(valueType);
    }

    private static final class Builder<K, V> {

        private AsyncCache<K, V> delegate;

        private Class<K> keyType;
        private Class<V> valueType;

        private Builder(){
        }

        private Builder(Builder<K, V> proto){
            this.delegate = proto.delegate;
            this.keyType = proto.keyType;
            this.valueType = proto.valueType;
        }


        public Builder<K, V> setDelegate(AsyncCache<K, V> delegate) {
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

        public BlockingCacheWrapper<K, V> build(){

            checkNotNull(delegate, "Delegate cannot be null");

            checkNotNull(keyType, "Key type cannot be null");
            checkNotNull(valueType, "Value type cannot be null");

            return new BlockingCacheWrapper<>(this);
        }
    }
}
