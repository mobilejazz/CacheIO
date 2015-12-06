package com.mobilejazz.cacheio;

import android.content.Context;
import com.mobilejazz.cacheio.internal.helper.Preconditions;
import com.mobilejazz.cacheio.logging.EmptyLogger;
import com.mobilejazz.cacheio.logging.Logger;
import com.mobilejazz.cacheio.persistence.Persistence;
import com.mobilejazz.cacheio.serializer.Serializer;

public class CacheIO {

  private final Cache cache;
  private final Logger logger;

  private CacheIO(Persistence persistence, Serializer serializer, Logger logger) {
    this.cache = new CacheManager(persistence, serializer);
    this.logger = logger;
  }

  public Cache cacheDataSource() {
    Preconditions.checkNotNull(cache, "cache == null! Create one using Builder instance!");
    return cache;
  }

  public static Builder with(Context context) {
    return new Builder(context);
  }

  public static final class Builder {

    private Context context;
    private Persistence persistence;
    private Serializer serializer;
    private Logger logger;

    public Builder(Context context) {
      this.context = context.getApplicationContext();
    }

    public Builder serializer(Serializer serializer) {
      this.serializer = Preconditions.checkNotNull(serializer, "serializer == null");
      return this;
    }

    public Builder persistence(Persistence persistence) {
      this.persistence = Preconditions.checkNotNull(persistence, "persistence == null");
      return this;
    }

    public Builder logger(Logger logger) {
      this.logger = Preconditions.checkNotNull(logger, "logger == null");
      return this;
    }

    public CacheIO build() {
      if (persistence == null) {
        throw new IllegalStateException("peristence == null");
      }

      if (serializer == null) {
        throw new IllegalStateException("serializer == null");
      }

      if (logger == null) {
        this.logger = new EmptyLogger();
      }

      return new CacheIO(persistence, serializer, logger);
    }
  }
}
