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

package com.mobilejazz.cacheio.manager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import com.mobilejazz.cacheio.manager.table.CacheTableMeta;

public class CacheOpenHelper extends SQLiteOpenHelper {

  private static final int DB_VERSION = 1;

  private Context context;
  private String databaseName;

  public CacheOpenHelper(@NonNull Context context, String databaseName) {
    super(context, databaseName, null, DB_VERSION);
    this.context = context;
    this.databaseName = databaseName;
  }

  @Override public void onCreate(@NonNull SQLiteDatabase db) {
    db.execSQL(getCreateCacheTableQuery());
  }

  @Override public void onUpgrade(@NonNull SQLiteDatabase db, int oldVersion, int newVersion) {
    if (newVersion > oldVersion) {
      context.deleteDatabase(databaseName);
    }
  }

  @NonNull private static String getCreateCacheTableQuery() {
    return "CREATE TABLE "
        + CacheTableMeta.TABLE
        + "("
        + CacheTableMeta.COLUMN_KEY
        + " TEXT NOT NULL, "
        + CacheTableMeta.COLUMN_TYPE
        + " TEXT NOT NULL, "
        + CacheTableMeta.COLUMN_VALUE
        + " BLOB NOT NULL, "
        + CacheTableMeta.COLUMN_EXPIRY_MILLIS
        + " INTEGER NOT NULL, "
        + CacheTableMeta.COLUMN_INDEX
        + " TEXT NOT NULL, "
        + CacheTableMeta.COLUMN_METATYPE
        + " TEXT NOT NULL, "
        + CacheTableMeta.COLUMN_TIMESTAMP
        + " INTEGER NOT NULL,"
        + " PRIMARY KEY ("
        + CacheTableMeta.COLUMN_KEY
        + ", "
        + CacheTableMeta.COLUMN_INDEX
        + ")"
        + ");";
  }
}
