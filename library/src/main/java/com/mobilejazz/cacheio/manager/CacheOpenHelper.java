package com.mobilejazz.cacheio.manager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import com.mobilejazz.cacheio.manager.table.CacheTableMeta;

public class CacheOpenHelper extends SQLiteOpenHelper {

  private static final int DB_VERSION = 1;

  public CacheOpenHelper(@NonNull Context context, String databaseName) {
    super(context, databaseName, null, DB_VERSION);
  }

  ///////////////////////////////////////////////////////////////////////////
  // Private methods
  ///////////////////////////////////////////////////////////////////////////

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
        + CacheTableMeta.COLUMN_INDEX
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

  @Override public void onCreate(@NonNull SQLiteDatabase db) {
    db.execSQL(getCreateCacheTableQuery());
  }

  @Override public void onUpgrade(@NonNull SQLiteDatabase db, int oldVersion, int newVersion) {
    //TODO: Implement
  }
}
