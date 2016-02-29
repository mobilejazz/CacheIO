package com.mobilejazz.cacheio.alternative;

import java.util.Collection;
import java.util.Map;

import rx.Scheduler;
import rx.Single;

public interface Repository<Id, M> {

    KeyExtractor<Id, M> getKeyExtractor();

    Single<M> findById(Id id);

    Single<M> findById(Scheduler scheduler, Id id);

    Single<M> update(M model);

    Single<M> update(Scheduler scheduler, M model);

    Single<M> remove(M model);

    Single<M> remove(Scheduler scheduler, M model);

    Single<Id> removeById(Id id);

    Single<Id> removeById(Scheduler scheduler, Id id);


    interface KeyExtractor<A, B> {

        A getKey(B model);

    }

}
