/*
 * Copyright (C) 2015 Mobile Jazz
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

package com.mobilejazz.cacheio.detector.storeobject;

import android.support.annotation.NonNull;
import com.mobilejazz.cacheio.serializer.Serializer;
import com.mobilejazz.cacheio.manager.entity.CacheEntry;
import com.mobilejazz.cacheio.manager.entity.StoreObject;
import java.util.Collections;
import java.util.List;

public interface TypeValueStrategy {

  class NullTypeValueStrategy implements TypeValueStrategy {

    @Override @NonNull public List<StoreObject> convert(Serializer serializer, CacheEntry<?> cacheEntry) {
      return Collections.emptyList();
    }
  }

  List<StoreObject> convert(Serializer serializer, CacheEntry<?> cacheEntry);

}