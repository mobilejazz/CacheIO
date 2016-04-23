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

import com.mobilejazz.cacheio.mappers.PaginatedQueryMapper;
import com.mobilejazz.cacheio.query.PaginatedQuery;
import com.mobilejazz.cacheio.query.Query;
import org.junit.Test;
import rx.*;

import java.util.*;
import java.util.concurrent.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {

  @Test public void addition_isCorrect() throws Exception {
    //RxRepository<String, TestModel, RxRepository.Query> testModelQueryCachingRxRepository =
    //    new CachingRxRepository<>();

    CacheIO cacheIO = CacheIO.with(null)
        .identifier("test.databse")
        .executor(Executors.newSingleThreadExecutor())
        .setKeyMapper(PaginatedQuery.class, new PaginatedQueryMapper())
        .build();

    //RxCache<String, TestModel> testModelCache = cacheIO.newRxCache(String.class, TestModel.class);
    //
    //IdList<String> idList = new IdList<>(Collections.<String>emptyList());
    //RxCache<RxRepository.Query, IdList<String>> queryCache =
    //    cacheIO.newRxCache(RxRepository.Query.class, idList.<String>get());
    //
    //
    //CachingRxRepository<String, TestModel, RxRepository.Query> cachingRxRepository =
    //    new CachingRxRepository.Builder<String, TestModel, RxRepository.Query>().setCache(
    //        testModelCache).setQueryCache(queryCache).build();
    //
    //Single<List<TestModel>> listSingle = cachingRxRepository.find(new TestQuery());

    RxCache<String, TestModel> cache = cacheIO.newRxCache(String.class, TestModel.class);

    RxCache<PaginatedQuery, StringList> queryCache =
        cacheIO.newRxCache(PaginatedQuery.class, StringList.class);

    RxRepository<String, TestModel, PaginatedQuery> rxRepository =
        new StringKeyedRxRepository.Builder<TestModel, PaginatedQuery>()
            .setCache(cache)
            .setQueryCache(queryCache)
            .build();

    PaginatedQuery query = new PaginatedQuery(0, 10);

    List<TestModel> models = new ArrayList<>();
    models.add(new TestModel());

    Single<List<TestModel>> put = rxRepository.put(query, models);

    Single<List<TestModel>> listSingle = rxRepository.find(query);
  }

  class TestModel implements HasId<String> {

    @Override public String getId() {
      return "test.id";
    }
  }

  class TestQuery implements Query {

  }
}