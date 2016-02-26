package com.mobilejazz.cacheio.alternative;

import com.mobilejazz.cacheio.alternative.Cache.Mapper;
import com.mobilejazz.cacheio.exceptions.SerializerException;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.IdentityHashMap;
import java.util.Map;

import static com.mobilejazz.cacheio.internal.helper.Preconditions.checkNotNull;

public class MappingContext implements Mapper {

    private final Builder config;

    public MappingContext(Builder proto) {
        this.config = new Builder(proto);
    }

    @Override
    public void write(Object value, OutputStream out) throws SerializerException {
        final Class<?> type = value.getClass();
        final Mapper mapper = checkNotNull(config.mappers.get(type), "Mapper not found for type = " + type);
        mapper.write(value, out);
    }

    @Override
    public <T> T read(Class<T> type, InputStream in) throws SerializerException {
        final Mapper mapper = checkNotNull(config.mappers.get(type), "Mapper not found for type = " + type);
        return mapper.read(type, in);
    }

    public static Builder newBuilder(){
        return new Builder();
    }

    public static final class Builder {

        private final Map<Class<?>, Mapper> mappers;

        private Builder() {
            this.mappers = new IdentityHashMap<>();
        }

        private Builder(Builder proto) {
            this.mappers = new IdentityHashMap<>(proto.mappers);
        }

        public Builder register(Class<?> type, Mapper mapper) {
            mappers.put(type, mapper);
            return this;
        }

        public MappingContext build() {
            return new MappingContext(this);
        }
    }
}
