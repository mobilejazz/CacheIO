package com.mobilejazz.cacheio.persistence;

import android.content.ContentValues;
import android.database.Cursor;
import com.mobilejazz.cacheio.exceptions.CacheErrorException;
import com.mobilejazz.cacheio.exceptions.CacheNotFoundException;
import com.mobilejazz.cacheio.exceptions.ExpiredCacheException;
import com.mobilejazz.cacheio.manager.entity.StoreObject;
import com.mobilejazz.cacheio.manager.entity.StoreObjectBuilder;
import com.mobilejazz.cacheio.manager.table.CacheTableMeta;
import com.mobilejazz.cacheio.strategy.CachingStrategy;
import com.mobilejazz.cacheio.strategy.CachingStrategyObject;
import com.squareup.sqlbrite.BriteDatabase;
import java.util.ArrayList;
import java.util.List;

public class PersistenceSqlLite implements Persistence {

  private BriteDatabase db;

  public PersistenceSqlLite(BriteDatabase briteDatabase) {
    this.db = briteDatabase;
  }

  @Override public List<StoreObject> obtain(String key)
      throws CacheNotFoundException, CacheErrorException {
    Cursor entry = db.query(
        "SELECT * FROM " + CacheTableMeta.TABLE + " WHERE " + CacheTableMeta.COLUMN_KEY + " = ?",
        key);

    List<StoreObject> result = new ArrayList<>(entry.getCount());

    while (entry.moveToNext()) {
      int keyColumn = entry.getColumnIndex(CacheTableMeta.COLUMN_KEY);
      int typeColumn = entry.getColumnIndex(CacheTableMeta.COLUMN_TYPE);
      int valueColumn = entry.getColumnIndex(CacheTableMeta.COLUMN_VALUE);
      int expiryMillisColumn = entry.getColumnIndex(CacheTableMeta.COLUMN_EXPIRY_MILLIS);
      int metadataTypeColumn = entry.getColumnIndex(CacheTableMeta.COLUMN_METATYPE);
      int indexColumn = entry.getColumnIndex(CacheTableMeta.COLUMN_INDEX);
      int timestampColumn = entry.getColumnIndex(CacheTableMeta.COLUMN_TIMESTAMP);

      String keyValue = entry.getString(keyColumn);
      String type = entry.getString(typeColumn);
      byte[] value = entry.getBlob(valueColumn);
      long expiryMillis = entry.getLong(expiryMillisColumn);
      String metatype = entry.getString(metadataTypeColumn);
      String index = entry.getString(indexColumn);
      long timestamp = entry.getLong(timestampColumn);

      StoreObject storeObject = new StoreObjectBuilder().setIndex(keyValue)
          .setType(type)
          .setValue(value)
          .setExpiryMillis(expiryMillis)
          .setIndex(index)
          .setMetaType(metatype)
          .setTimestamp(timestamp)
          .build();

      result.add(storeObject);
    }

    if (result.isEmpty()) {
      throw new CacheNotFoundException();
    }

    return result;
  }

  @Override public boolean persist(List<StoreObject> value) throws CacheErrorException {
    try {

      if (value == null) {
        throw new IllegalArgumentException("value == null");
      }

      if (value.size() == 0) {
        throw new IllegalArgumentException("value.size() == 0");
      }

      boolean isSingleStoreObject = value.size() == 1 && value.get(0).getMetaType() == null;

      if (isSingleStoreObject) {
        // Single object
        StoreObject storeObjectToPersist = value.get(0);

        ContentValues contentValues = StoreObject.toContentValues(storeObjectToPersist);

        db.delete(CacheTableMeta.TABLE, CacheTableMeta.COLUMN_KEY + " = ?",
            storeObjectToPersist.getKey());
        long result = db.insert(CacheTableMeta.TABLE, contentValues);

        @SuppressWarnings("unused")
        boolean isPersisted = result > 0;

        return isPersisted;
      } else {
        // List object

        BriteDatabase.Transaction transaction = db.newTransaction();

        String key = value.get(0).getKey();

        // Get all the objects associated with the key to know if we need to delete all this objects
        // for cleaning purposes
        Cursor query = db.query("SELECT * FROM "
            + CacheTableMeta.TABLE
            + " WHERE "
            + CacheTableMeta.COLUMN_KEY
            + " = ?", key);

        // Clean all the old objects
        while (query.moveToNext()) {
          int columnIndex = query.getColumnIndex(CacheTableMeta.COLUMN_INDEX);
          String index = query.getString(columnIndex);

          db.delete(CacheTableMeta.TABLE,
              CacheTableMeta.COLUMN_KEY + " = ? AND " + CacheTableMeta.COLUMN_INDEX + " = ?", key,
              index);
        }

        // Insert new objects
        for (StoreObject storeObject : value) {
          db.insert(CacheTableMeta.TABLE, StoreObject.toContentValues(storeObject));
        }

        transaction.markSuccessful();
        transaction.end();

        // TODO: 22/11/15 Fix this!!!!!!!
        return true;
      }
    } catch (Exception e) {
      throw new CacheErrorException(e);
    }
  }

  @Override public boolean delete(String key) throws CacheErrorException {
    try {
      long rowsAffected = db.delete(CacheTableMeta.TABLE, CacheTableMeta.COLUMN_KEY + " = ?", key);

      @SuppressWarnings("unused")
      boolean isDeleted = rowsAffected > 0;

      return isDeleted;
    } catch (Exception e) {
      throw new CacheErrorException(e);
    }
  }

  @SuppressWarnings("unchecked") @Override
  public <T extends CachingStrategyObject> void executeValidation(StoreObject storeObject,
      CachingStrategy<T> strategy) throws ExpiredCacheException, CacheNotFoundException {
    if (storeObject == null) {
      throw new CacheNotFoundException();
    } else {
      if (!strategy.isValid((T) storeObject)) {
        throw new ExpiredCacheException();
      }
    }
  }
}
