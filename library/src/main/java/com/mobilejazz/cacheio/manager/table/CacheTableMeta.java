package com.mobilejazz.cacheio.manager.table;

import android.support.annotation.NonNull;

public class CacheTableMeta {

  @NonNull public static final String TABLE = "cache";

  @NonNull public static final String COLUMN_KEY = "_key";

  @NonNull public static final String COLUMN_TYPE = "type";

  @NonNull public static final String COLUMN_VALUE = "value";

  @NonNull public static final String COLUMN_METADATA = "metadata";

  @NonNull public static final String COLUMN_INDEX = "_index";

  @NonNull public static final String COLUMN_TIMESTAMP = "timestamp";

  private CacheTableMeta() {
    throw new IllegalStateException("No instances for this class");
  }
}
