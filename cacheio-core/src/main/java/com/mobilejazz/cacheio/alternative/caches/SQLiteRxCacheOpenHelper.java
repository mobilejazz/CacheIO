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

package com.mobilejazz.cacheio.alternative.caches;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.*;

public class SQLiteRxCacheOpenHelper extends SQLiteOpenHelper {

  public static final int DATABASE_VERSION = 1;

  public SQLiteRxCacheOpenHelper(Context context, String databaseName) {
    super(context, databaseName, null, DATABASE_VERSION);
  }

  @Override public void onCreate(SQLiteDatabase db) {
    // caches create their own tables, the structure should not really change
  }

  @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    // nothing for now

  }
}
