/*
 * Copyright (C) 2016 Mobile Jazz
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.mobilejazz.cacheio.alternative.repositories;

import com.mobilejazz.cacheio.alternative.RxCache;
import com.mobilejazz.cacheio.alternative.RxRepository;
import rx.Single;
import rx.functions.Func1;

import java.util.*;
import java.util.concurrent.*;

import static com.mobilejazz.cacheio.internal.helper.Preconditions.checkArgument;

public class CachingRxRepository<Id, M extends HasId<Id>, Q extends RxRepository.Query> implements
    RxRepository<Id, M, Q> {

  private final Builder<Id, M, Q> config;

  private CachingRxRepository(Builder<Id, M, Q> config) {
    this.config = config;
  }

  /**
   * The basic here is the a Query maps to a list of models, each of which have an Id. When a query
   * is performed we cache the Query => IdLIst, and update the value cache for each model returned.
   *
   * @param query
   * @return
   */
  @Override public Single<List<M>> find(final Query query) {

    // execute the query on the delegate repository first

    return config.delegate.find(query)

        .flatMap(new Func1<List<M>, Single<List<M>>>() {
          @Override public Single<List<M>> call(final List<M> models) {

            // TODO need a better way of detecting this. I would have preferred onErrorReturn but
            // you can't return an Observable or Single, that would have been cleaner

            if(models == null){

              // lets assume the network connection is unavailable, so returned cache values for
              // the query

              final Single<IdList<Id>> idListLookup = config.queryCache.get(query);

              return idListLookup.flatMap(new Func1<IdList<Id>, Single<List<M>>>() {
                @Override public Single<List<M>> call(IdList<Id> idList) {

                  final List<Id> ids = idList.getIds();

                  final Single<Map<Id, M>> valueLookup = config.cache.getAll(ids);

                  return valueLookup.map(new Func1<Map<Id, M>, List<M>>() {
                    @Override public List<M> call(Map<Id, M> map) {

                      // need to preserve the order of return
                      final List<M> result = new ArrayList<M>(map.size());

                      for (Id id : ids) {
                        result.add(map.get(id));
                      }

                      return result;
                    }
                  });

                }
              });

            } else {

              // extract the ids and construct a map

              final List<Id> ids = new ArrayList<>(models.size());
              final Map<Id, M> map = new HashMap<>(models.size());

              for (M model : models) {
                final Id id = model.getId();

                ids.add(id);
                map.put(id, model);
              }

              final IdList<Id> idList = new IdList<>(ids);

              // add an entry into the query cache to record the id list for the query

              final Single<IdList<Id>> queryCacheUpdate =
                  config.queryCache.put(query, idList, Long.MAX_VALUE, TimeUnit.SECONDS);

              final Single<Map<Id, M>> cacheUpdate =
                  queryCacheUpdate.flatMap(new Func1<IdList<Id>, Single<Map<Id, M>>>() {
                    @Override public Single<Map<Id, M>> call(IdList<Id> idList) {
                      return config.cache.putAll(map, Long.MAX_VALUE, TimeUnit.SECONDS);
                    }
                  });

              return cacheUpdate.map(new Func1<Map<Id,M>, List<M>>() {
                @Override public List<M> call(Map<Id, M> idMMap) {
                  return models;
                }
              });

            }

          }
        });


  }

  @Override public Single<M> findById(Id id) {
    return null;
  }

  @Override public Single<M> update(M model) {
    return null;
  }

  @Override public Single<M> remove(M model) {
    return null;
  }

  @Override public Single<Id> removeById(Id id) {
    return null;
  }

  private static final class Builder<Id, M extends HasId<Id>, Q extends RxRepository.Query> {

    private RxCache<Id, M> cache;
    private RxCache<Query, IdList<Id>> queryCache;
    private RxRepository<Id, M, Q> delegate;

    public Builder() {
    }

    public Builder(Builder<Id, M, Q> proto){
      this.cache = proto.cache;
      this.queryCache = proto.queryCache;
    }

    public Builder<Id, M, Q> setCache(RxCache<Id, M> cache) {
      this.cache = cache;
      return this;
    }

    public Builder<Id, M, Q> setQueryCache(RxCache<Query, IdList<Id>> queryCache) {
      this.queryCache = queryCache;
      return this;
    }

    public Builder<Id, M, Q> setDelegate(RxRepository<Id, M, Q> delegate) {
      this.delegate = delegate;
      return this;
    }

    public CachingRxRepository<Id, M, Q> build(){

      // assertions

      checkArgument(cache, "cache cannot be null");
      checkArgument(queryCache, "query cache cannot be null");
      checkArgument(delegate, "delegate cannot be null");

      //

      return new CachingRxRepository<>(new Builder<>(this));
    }
  }
}
