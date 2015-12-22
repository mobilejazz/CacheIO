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

package com.mobilejazz.cacheio;

import android.content.Context;
import android.text.TextUtils;
import com.mobilejazz.cacheio.internal.helper.Preconditions;
import com.mobilejazz.cacheio.logging.AndroidLogger;
import com.mobilejazz.cacheio.logging.LogLevel;
import com.mobilejazz.cacheio.logging.Logger;
import com.mobilejazz.cacheio.persistence.Persistence;
import com.mobilejazz.cacheio.persistence.sqlbrite.PersistenceSQLBrite;
import com.mobilejazz.cacheio.serializer.Serializer;

public class CacheIO {

  private final Cache cache;

  private CacheIO(Persistence persistence, Serializer serializer, Logger logger,
      LogLevel logLevel) {
    this.cache = new CacheManager(persistence, serializer, logger, logLevel);
  }

  public Cache cacheDataSource() {
    Preconditions.checkNotNull(cache, "cache == null! Create one using Builder instance!");
    return cache;
  }

  public static Builder with(Context context) {
    return new Builder(context);
  }

  public static final class Builder {

    private Persistence persistence;
    private Serializer serializer;
    private Logger logger;
    private LogLevel logLevel;
    private String identifier;
    private Context context;

    public Builder(Context context) {
      this.context = context;
    }

    public Builder serializer(Serializer serializer) {
      this.serializer = Preconditions.checkNotNull(serializer, "serializer == null");
      return this;
    }

    public Builder identifier(String identifier) {
      if (TextUtils.isEmpty(identifier)) {
        throw new IllegalArgumentException("identifier == null OR database == empty");
      }

      this.identifier = identifier;
      return this;
    }

    public Builder logLevel(LogLevel logLevel) {
      this.logLevel = Preconditions.checkNotNull(logLevel, "LogLevel logLevel");
      return this;
    }

    public CacheIO build() {
      if (persistence == null) {
        persistence = new PersistenceSQLBrite(PersistenceSQLBrite.generate(context, identifier));
      }

      if (serializer == null) {
        throw new IllegalStateException("serializer == null");
      }

      if (logger == null) {
        this.logger = new AndroidLogger();
      }

      return new CacheIO(persistence, serializer, logger, logLevel);
    }
  }
}
