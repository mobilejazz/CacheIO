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

package com.mobilejazz.cacheio;

import com.mobilejazz.cacheio.query.Query;
import rx.Single;
import rx.functions.Func1;

import java.util.*;
import java.util.concurrent.*;

import static com.mobilejazz.cacheio.helper.Preconditions.checkArgument;
import static com.mobilejazz.cacheio.helper.Preconditions.checkIsEmpty;

public class StringKeyedRxRepository<M extends HasId<String>, Q extends Query>
    implements RxRepository<String, M, Q> {

  private Builder<M, Q> proto;

  public StringKeyedRxRepository(Builder<M, Q> proto) {
    this.proto = proto;
  }

  @Override public Single<List<M>> find(Q query) {
    checkArgument(query, "Query == null");

    return proto.queryCache.get(query).flatMap(new Func1<StringList, Single<List<M>>>() {
      @Override public Single<List<M>> call(StringList stringIdList) {
        final List<String> ids =
            stringIdList == null ? Collections.<String>emptyList() : stringIdList.getIds();

        Single<Map<String, M>> valueLookup = proto.cache.getAll(ids);

        return valueLookup.map(new Func1<Map<String, M>, List<M>>() {
          @Override public List<M> call(Map<String, M> map) {
            List<M> values = new ArrayList<>(map.size());

            for (String id : ids) {
              values.add(map.get(id));
            }

            return values;
          }
        });
      }
    });
  }

  @Override public Single<M> findById(String id) {
    checkIsEmpty(id, "Id == null OR empty");
    return proto.cache.get(id);
  }

  @Override public Single<List<M>> put(Q query, List<M> models) {
    checkArgument(query, "Query == null");
    checkArgument(models, "Models == null");

    Map<String, M> values = new HashMap<>(models.size());
    final List<String> keys = new ArrayList<>(models.size());
    for (M model : models) {
      String id = model.getId();
      keys.add(id);

      values.put(id, model);
    }

    // Save the query with the ids
    StringList idList = new StringList(keys);
    proto.queryCache.put(query, idList, Long.MAX_VALUE, TimeUnit.DAYS)
        .toObservable()
        .toBlocking()
        .first();

    // Save the values with the ids
    Single<Map<String, M>> results = proto.cache.putAll(values, Long.MAX_VALUE, TimeUnit.DAYS);

    return results.map(new Func1<Map<String, M>, List<M>>() {
      @Override public List<M> call(Map<String, M> values) {
        List<M> models = new ArrayList<>(values.size());
        for (String key : keys) {
          models.add(values.get(key));
        }

        return models;
      }
    });
  }

  @Override public Single<M> put(M model) {
    checkArgument(model, "Model == null");
    return proto.cache.put(model.getId(), model, Long.MAX_VALUE, TimeUnit.DAYS);
  }

  @Override public Single<String> removeById(String id) {
    checkIsEmpty(id, "Id == null OR empty");
    return proto.cache.remove(id);
  }

  @Override public Single<Collection<String>> removeByQuery(Q query) {
    checkArgument(query, "Query == null");
    return proto.queryCache.get(query)
        .flatMap(new Func1<StringList, Single<? extends Collection<String>>>() {
          @Override public Single<? extends Collection<String>> call(StringList stringList) {
            return proto.cache.removeAll(stringList.getIds());
          }
        });
  }

  public static final class Builder<M extends HasId<String>, Q extends Query> {

    private RxCache<String, M> cache;
    private RxCache<Q, StringList> queryCache;

    public Builder() {
    }

    public Builder(Builder<M, Q> proto) {
      this.cache = proto.cache;
      this.queryCache = proto.queryCache;
    }

    public Builder<M, Q> setCache(RxCache<String, M> cache) {
      this.cache = cache;
      return this;
    }

    public Builder<M, Q> setQueryCache(RxCache<Q, StringList> queryCache) {
      this.queryCache = queryCache;
      return this;
    }

    public StringKeyedRxRepository<M, Q> build() {
      checkArgument(cache, "cache cannot be null");
      checkArgument(queryCache, "query cache cannot be null");

      return new StringKeyedRxRepository<>(new Builder<>(this));
    }
  }
}
