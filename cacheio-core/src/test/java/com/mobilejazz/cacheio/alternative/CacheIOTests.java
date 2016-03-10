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

package com.mobilejazz.cacheio.alternative;

import com.mobilejazz.cacheio.ApplicationTestCase;
import org.junit.Test;

public class CacheIOTests extends ApplicationTestCase {

  @Test public void testName() throws Exception {

    //CacheIO cacheIO = CacheIO.with(RuntimeEnvironment.application)
    //    .identifier("test")
    //    .mapper(String.class, TestUser.class, new StringKeyMapper(), new TestValueMapper())
    //    .executor(Executors.newSingleThreadExecutor())
    //    .build();
    //
    //RxCache<String, TestUser> stringTestUserRxCache = cacheIO.rxCache(TestUser.class);
    //
    //Single<TestUser> jose = stringTestUserRxCache.get("jose");
    //
    //jose.subscribe(new Action1<TestUser>() {
    //  @Override public void call(TestUser testUser) {
    //    Assertions.assertThat(testUser).isNull();
    //  }
    //});
    //
    //Thread.sleep(1000);
    //
    //Assertions.assertThat(jose).isNotNull();
  }
}
