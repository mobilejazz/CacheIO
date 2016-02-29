package com.mobilejazz.cacheio.alternative;


import java.util.concurrent.TimeUnit;

import rx.Scheduler;
import rx.Single;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static com.mobilejazz.cacheio.internal.helper.Preconditions.checkNotNull;

public class CachingRepository<Id, M> implements Repository<Id, M> {

    private final Builder<Id, M> config;

    private CachingRepository(Builder<Id, M> config) {
        this.config = new Builder<>(config);
    }

    @Override
    public KeyExtractor<Id, M> getKeyExtractor() {
        return config.delegate.getKeyExtractor();
    }

    @Override
    public Single<M> findById(Id id) {
        return findById(config.scheduler, id);
    }

    @Override
    public Single<M> findById(final Scheduler scheduler, final Id id) {

        return config.delegate.findById(scheduler, id)
                .onErrorReturn(new Func1<Throwable, M>() {
                    @Override
                    public M call(Throwable throwable) {
                        return null;
                    }
                })
                .flatMap(new Func1<M, Single<M>>() {
                    @Override
                    public Single<M> call(M model) {

                        if (model == null) {
                            // return cached version
                            return config.cache.get(scheduler, id);
                        } else {
                            // put in cache and return
                            return config.cache.put(scheduler, id, model, config.expiry, config.expiryUnit);
                        }

                    }
                });

    }

    @Override
    public Single<M> update(M model) {
        return update(config.scheduler, model);
    }

    @Override
    public Single<M> update(final Scheduler scheduler, M model) {

        final KeyExtractor<Id, M> keyExtractor = getKeyExtractor();

        return config.delegate.update(scheduler, model)
                .flatMap(new Func1<M, Single<M>>() {
                    @Override
                    public Single<M> call(M model) {
                        final Id id = keyExtractor.getKey(model);
                        return config.cache.put(scheduler, id, model, config.expiry, config.expiryUnit);
                    }
                });

    }


    @Override
    public Single<Id> removeById(Id id) {
        return removeById(config.scheduler, id);
    }

    @Override
    public Single<Id> removeById(final Scheduler scheduler, Id id) {

        return config.delegate.removeById(scheduler, id)
                .flatMap(new Func1<Id, Single<Id>>() {
                    @Override
                    public Single<Id> call(final Id id) {

                        return config.cache.remove(scheduler, id)
                                .map(new Func1<Id, Id>() {
                                    @Override
                                    public Id call(Id id) {
                                        return id;
                                    }
                                });

                    }
                });
    }

    @Override
    public Single<M> remove(M model) {
        return remove(config.scheduler, model);
    }

    @Override
    public Single<M> remove(final Scheduler scheduler, M model) {

        final KeyExtractor<Id, M> keyExtractor = getKeyExtractor();

        return config.delegate.remove(scheduler, model)
                .flatMap(new Func1<M, Single<M>>() {
                    @Override
                    public Single<M> call(final M model) {

                        final Id id = keyExtractor.getKey(model);

                        return config.cache.remove(id)
                                .map(new Func1<Id, M>() {
                                    @Override
                                    public M call(Id id) {
                                        return model;
                                    }
                                });

                    }
                });

    }

    public static <Id, M> Builder<Id, M> newBuilder(Class<Id> idType, Class<M> modelType) {
        return new Builder<Id, M>()
                .setIdType(idType)
                .setModelType(modelType);
    }

    public static final class Builder<Id, M> {

        private Repository<Id, M> delegate;
        private Scheduler scheduler = Schedulers.immediate();
        private SQLiteCache.Builder<Id, M> cacheBuilder;

        private Cache<Id, M> cache;

        private Class<Id> idType;
        private Class<M> modelType;

        private long expiry = -1;
        private TimeUnit expiryUnit;

        private Builder() {
        }

        private Builder(Builder<Id, M> proto) {
            this.delegate = proto.delegate;
            this.scheduler = proto.scheduler;
            this.cacheBuilder = proto.cacheBuilder;
            this.cache = proto.cache;
            this.idType = proto.idType;
            this.modelType = proto.modelType;
            this.expiry = proto.expiry;
            this.expiryUnit = proto.expiryUnit;
        }

        public Builder<Id, M> setIdType(Class<Id> idType) {
            this.idType = idType;
            return this;
        }

        public Builder<Id, M> setModelType(Class<M> modelType) {
            this.modelType = modelType;
            return this;
        }

        public Builder<Id, M> setExpiry(long expiry, TimeUnit expiryUnit) {
            this.expiry = expiry;
            this.expiryUnit = expiryUnit;
            return this;
        }

        public Builder<Id, M> setDelegate(Repository<Id, M> delegate) {
            this.delegate = delegate;
            return this;
        }

        public Builder<Id, M> setScheduler(Scheduler scheduler) {
            this.scheduler = scheduler;
            return this;
        }

        public Builder<Id, M> setCacheBuilder(SQLiteCache.Builder<Id, M> cacheBuilder) {
            this.cacheBuilder = cacheBuilder;
            return this;
        }

        public CachingRepository<Id, M> build() {

            if (expiry <= 0) {
                expiry = Long.MAX_VALUE;
                expiryUnit = TimeUnit.SECONDS;
            }

            checkNotNull(delegate, "delegate cannot be null");
            checkNotNull(scheduler, "scheduler cannot be null");
            checkNotNull(cacheBuilder, "cacheBuilder cannot be null");

            this.cache = cacheBuilder.build();

            return new CachingRepository<>(this);
        }
    }
}
