package com.mobilejazz.cacheio;

import android.content.ContentValues;
import android.database.Cursor;
import com.google.gson.Gson;
import com.mobilejazz.cacheio.exceptions.InvalidCacheException;
import com.mobilejazz.cacheio.manager.entity.CacheEntry;
import com.mobilejazz.cacheio.manager.entity.StoreObject;
import com.mobilejazz.cacheio.manager.table.CacheTableMeta;
import com.mobilejazz.cacheio.strategy.CachingStrategy;
import com.mobilejazz.cacheio.strategy.CachingStrategyObject;
import com.squareup.sqlbrite.BriteDatabase;
import java.util.ArrayList;
import java.util.List;

public class CacheSqliteManager implements CacheDataSource {

  private Gson gson;
  private BriteDatabase db;

  public CacheSqliteManager(Gson gson, BriteDatabase briteDatabase) {
    this.gson = gson;
    this.db = briteDatabase;
  }

  @SuppressWarnings("unchecked") @Override public <T> CacheEntry obtain(String key) {
    Cursor result = db.query(
        "SELECT * FROM "
+ CacheTableMeta.TABLE + " WHERE " + CacheTableMeta.COLUMN_KEY + " = ?",
        key);

    if (result.getCount() > 1) {
      List values = new ArrayList(result.getCount());
      String keyDb = null;
      Class<?> type = null;

      while (result.moveToNext()) {
        int keyColumnIndex = result.getColumnIndex(CacheTableMeta.COLUMN_KEY);
        int typeColumnIndex = result.getColumnIndex(CacheTableMeta.COLUMN_TYPE);
        int valueColumnIndex = result.getColumnIndex(CacheTableMeta.COLUMN_VALUE);

        try {
          keyDb = result.getString(keyColumnIndex);
          String typeStringDb = result.getString(typeColumnIndex);
          byte[] value = result.getBlob(valueColumnIndex);

          type = Class.forName(typeStringDb);
          String jsonValue = new String(value);
          Object object = gson.fromJson(jsonValue, type);

          values.add((T) type.cast(object));
        } catch (ClassNotFoundException e) {
          // TODO: 17/09/15 Handle this error
          e.printStackTrace();
        }
      }

      return CacheEntry.create(keyDb, type, values);
    } else {
      if (result.moveToFirst()) {
        int keyColumnIndex = result.getColumnIndex(CacheTableMeta.COLUMN_KEY);
        int typeColumnIndex = result.getColumnIndex(CacheTableMeta.COLUMN_TYPE);
        int valueColumnIndex = result.getColumnIndex(CacheTableMeta.COLUMN_VALUE);

        try {
          String keyDb = result.getString(keyColumnIndex);
          String typeStringDb = result.getString(typeColumnIndex);
          byte[] value = result.getBlob(valueColumnIndex);

          Class<?> type = Class.forName(typeStringDb);
          String jsonValue = new String(value);
          Object object = gson.fromJson(jsonValue, type);

          return CacheEntry.create(keyDb, type, (T) type.cast(object));
        } catch (ClassNotFoundException e) {
          // TODO: 17/09/15 Handle this error
          e.printStackTrace();
        }
      }
    }

    return null;
  }

  @Override public void persist(CacheEntry cacheEntry) {
    if (cacheEntry.getValue() instanceof List) {
      persistListOfObjects(cacheEntry);
    } else {
      persistSingleObject(cacheEntry);
    }
  }

  @Override public void delete(String key) {
    throw new IllegalStateException("delete() is not implemented");
  }

  @Override public <T extends CachingStrategyObject> void executeValidation(StoreObject storeObject,
      CachingStrategy<T> strategy) throws InvalidCacheException {
    throw new IllegalStateException("executeValidation() is not implemented");
  }

  ///////////////////////////////////////////////////////////////////////////
  // Private methods
  ///////////////////////////////////////////////////////////////////////////

  private void persistListOfObjects(CacheEntry cacheEntry) {
    List<StoreObject> objectsToPersist = new ArrayList<>();

    List valuesToSerialize = (List) cacheEntry.getValue();

    for (int position = 0; position < valuesToSerialize.size(); position++) {
      Object objectToSerialize = valuesToSerialize.get(position);
      byte[] value = gson.toJson(objectToSerialize).getBytes();

      String index = StoreObject.generateIndex(cacheEntry.getKey(), String.valueOf(position));
      StoreObject storeObject =
          StoreObject.create(cacheEntry.getKey(), cacheEntry.getTypeCannonicalName(), value, index);
      objectsToPersist.add(storeObject);
    }

    BriteDatabase.Transaction transaction = db.newTransaction();

    Cursor query = db.query(
        "SELECT * FROM " + CacheTableMeta.TABLE + " WHERE " + CacheTableMeta.COLUMN_KEY + " = ?",
        cacheEntry.getKey());

    while (query.moveToNext()) {
      int columnIndex = query.getColumnIndex(CacheTableMeta.COLUMN_INDEX);
      String index = query.getString(columnIndex);

      db.delete(CacheTableMeta.TABLE,
          CacheTableMeta.COLUMN_KEY + " = ? AND " + CacheTableMeta.COLUMN_INDEX + " = ?",
          cacheEntry.getKey(), index);
    }

    for (StoreObject storeObject : objectsToPersist) {
      db.insert(CacheTableMeta.TABLE, StoreObject.toContentValues(storeObject));
    }

    transaction.markSuccessful();
    transaction.end();
  }

  private void persistSingleObject(CacheEntry cacheEntry) {
    byte[] value = gson.toJson(cacheEntry.getValue()).getBytes();
    StoreObject storeObject =
        StoreObject.create(cacheEntry.getKey(), cacheEntry.getType().getCanonicalName(), value, null /*index*/);

    ContentValues contentValues = StoreObject.toContentValues(storeObject);

    db.delete(CacheTableMeta.TABLE, CacheTableMeta.COLUMN_KEY + " = ?", cacheEntry.getKey());
    db.insert(CacheTableMeta.TABLE, contentValues);
  }
}
