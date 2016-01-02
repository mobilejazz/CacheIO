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

package com.mobilejazz.cacheio.detector;

import com.mobilejazz.cacheio.ApplicationTestCase;
import com.mobilejazz.cacheio.detector.cacheentry.CacheEntryDetectorFactory;
import com.mobilejazz.cacheio.detector.cacheentry.CacheValueStrategy;
import com.mobilejazz.cacheio.detector.cacheentry.ListCacheValueStrategy;
import com.mobilejazz.cacheio.detector.cacheentry.ObjectCacheValueStrategy;
import com.mobilejazz.cacheio.manager.entity.StoreObject;
import com.mobilejazz.cacheio.manager.entity.StoreObjectBuilder;
import java.util.Arrays;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

public class CacheEntryDetectorFactoryTest extends ApplicationTestCase {

  @Before public void setUp() throws Exception {
    // Nothing to set up
  }

  @Test public void shouldReturnAListCacheValueStrategy() throws Exception {
    StoreObject storeObject =
        new StoreObjectBuilder().setMetaType(List.class.getSimpleName()).build();

    List<StoreObject> storeObjects = Arrays.asList(storeObject, storeObject);

    CacheValueStrategy cacheValueStrategy = CacheEntryDetectorFactory.obtain(storeObjects);

    Assertions.assertThat(cacheValueStrategy).isInstanceOf(ListCacheValueStrategy.class);
  }

  @Test public void shouldReturnAObjectCacheValueStrategy() throws Exception {
    StoreObject storeObject = new StoreObjectBuilder().setMetaType(Object.class.getSimpleName()).build();

    List<StoreObject> storeObjects = Arrays.asList(storeObject);

    CacheValueStrategy cacheValueStrategy = CacheEntryDetectorFactory.obtain(storeObjects);

    Assertions.assertThat(cacheValueStrategy).isInstanceOf(ObjectCacheValueStrategy.class);
  }

  @Test public void shouldReturnANullCacheValueStrategy() throws Exception {
    StoreObject storeObject = new StoreObjectBuilder().setMetaType(null).build();

    List<StoreObject> storeObjects = Arrays.asList(storeObject);

    CacheValueStrategy cacheValueStrategy = CacheEntryDetectorFactory.obtain(storeObjects);

    Assertions.assertThat(cacheValueStrategy).isInstanceOf(CacheValueStrategy.NullCacheValueStrategy.class);
  }
}
