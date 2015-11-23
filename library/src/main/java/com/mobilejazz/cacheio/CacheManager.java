package com.mobilejazz.cacheio;

import com.mobilejazz.cacheio.detector.cacheentry.CacheEntryDetectorFactory;
import com.mobilejazz.cacheio.detector.cacheentry.CacheValueStrategy;
import com.mobilejazz.cacheio.detector.storeobject.TypeDetectorFactory;
import com.mobilejazz.cacheio.detector.storeobject.TypeValueStrategy;
import com.mobilejazz.cacheio.exceptions.CacheErrorException;
import com.mobilejazz.cacheio.exceptions.CacheNotFoundException;
import com.mobilejazz.cacheio.manager.entity.CacheEntry;
import com.mobilejazz.cacheio.manager.entity.StoreObject;
import java.util.List;

public class CacheManager implements Cache {

  private final Persitence persitence;
  private final Serializer serializer;

  public CacheManager(Persitence persitence, Serializer serializer) {
    this.persitence = persitence;
    this.serializer = serializer;
  }

  @SuppressWarnings("unchecked") @Override public <T> CacheEntry<T> obtain(String key) {
    // New
    try {
      List<StoreObject> storeObjects = persitence.obtain(key);
      CacheValueStrategy strategy = CacheEntryDetectorFactory.obtain(storeObjects);

      return strategy.convert(storeObjects);
    } catch (CacheNotFoundException e) {
      e.printStackTrace();
    } catch (CacheErrorException e) {
      e.printStackTrace();
    }

    // Old Implementation
    //Cursor result = db.query(
    //    "SELECT * FROM " + CacheTableMeta.TABLE + " WHERE " + CacheTableMeta.COLUMN_KEY + " = ?",
    //    key);
    //
    //if (result.moveToFirst()) {
    //  int metadataTypeColumnIndex = result.getColumnIndex(CacheTableMeta.COLUMN_METATYPE);
    //  String metadata = result.getString(metadataTypeColumnIndex);
    //
    //  if (metadata.equals(List.class.getSimpleName())) {
    //    // It's a list
    //    List values = new ArrayList(result.getCount());
    //    Object firstObject = getObjectFromCursor(result);
    //
    //    values.add(firstObject);
    //
    //    String dbKey = getKeyDbFromCursor(result);
    //    long expiryMillis = getExpiryMillisFromCursor(result);
    //
    //    Class type;
    //    try {
    //      type = getClassTypeFromCursor(result);
    //    } catch (ClassNotFoundException e) {
    //      type = null;
    //      // TODO: 05/10/15 Check this implementation
    //      e.printStackTrace();
    //    }
    //
    //    while (result.moveToNext()) {
    //      Object nextObject = getObjectFromCursor(result);
    //      values.add(nextObject);
    //    }
    //
    //    CacheEntry<T> cacheEntry = new CacheEntry<>();
    //    cacheEntry.setKey(dbKey);
    //    cacheEntry.setType(type);
    //    cacheEntry.setValue((T) values);
    //    cacheEntry.setExpiryMillis(expiryMillis);
    //
    //    return cacheEntry;
    //  } else if (metadata.equals(Object.class.getSimpleName())) {
    //    // It's a object
    //    int keyColumnIndex = result.getColumnIndex(CacheTableMeta.COLUMN_KEY);
    //    int typeColumnIndex = result.getColumnIndex(CacheTableMeta.COLUMN_TYPE);
    //    int valueColumnIndex = result.getColumnIndex(CacheTableMeta.COLUMN_VALUE);
    //    int expiryMillisColumnIndex = result.getColumnIndex(CacheTableMeta.COLUMN_EXPIRY_MILLIS);
    //
    //    try {
    //      String keyDb = result.getString(keyColumnIndex);
    //      String typeStringDb = result.getString(typeColumnIndex);
    //      byte[] value = result.getBlob(valueColumnIndex);
    //      long expiryMillis = result.getLong(expiryMillisColumnIndex);
    //
    //      Class type = Class.forName(typeStringDb);
    //      String jsonValue = new String(value);
    //      Object object = gson.fromJson(jsonValue, type);
    //
    //      CacheEntry<T> cacheEntry = new CacheEntry<>();
    //      cacheEntry.setKey(keyDb);
    //      cacheEntry.setType(type);
    //      cacheEntry.setValue((T) type.cast(object));
    //      cacheEntry.setExpiryMillis(expiryMillis);
    //
    //      return cacheEntry;
    //    } catch (ClassNotFoundException e) {
    //      // TODO: 17/09/15 Handle this error
    //      e.printStackTrace();
    //    }
    //  }
    //}
    return null;
  }

  @Override public boolean persist(CacheEntry cacheEntry) {
    if (cacheEntry == null) {
      throw new IllegalArgumentException("cacheEntry == null");
    }

    TypeValueStrategy strategy = TypeDetectorFactory.obtain(cacheEntry.getValue());

    List<StoreObject> storeObjects = strategy.convert(serializer, cacheEntry);
    try {
      return persitence.persist(storeObjects);
    } catch (CacheErrorException e) {
      e.printStackTrace();
      return false;
    }
  }

  @Override public boolean delete(String key) {
    try {
      return persitence.delete(key);
    } catch (CacheErrorException e) {
      e.printStackTrace();
      return false;
    }
  }

  ///////////////////////////////////////////////////////////////////////////
  // Private methods
  ///////////////////////////////////////////////////////////////////////////

  //@Deprecated
  //private void persistListOfObjects(CacheEntry cacheEntry) {
  //  List<StoreObject> objectsToPersist = new ArrayList<>();
  //
  //  List valuesToSerialize = (List) cacheEntry.getValue();
  //
  //  for (int position = 0; position < valuesToSerialize.size(); position++) {
  //    Object objectToSerialize = valuesToSerialize.get(position);
  //    byte[] value = gson.toJson(objectToSerialize).getBytes();
  //
  //    String index = StoreObject.generateIndex(cacheEntry.getKey(), String.valueOf(position));
  //    StoreObject storeObject =
  //        StoreObject.create(cacheEntry.getKey(), cacheEntry.getTypeCannonicalName(), value,
  //            cacheEntry.getExpiryMillis(), index, List.class.getSimpleName(),
  //            System.currentTimeMillis());
  //    objectsToPersist.add(storeObject);
  //  }
  //
  //  BriteDatabase.Transaction transaction = db.newTransaction();
  //
  //  Cursor query = db.query(
  //      "SELECT * FROM " + CacheTableMeta.TABLE + " WHERE " + CacheTableMeta.COLUMN_KEY + " = ?",
  //      cacheEntry.getKey());
  //
  //  while (query.moveToNext()) {
  //    int columnIndex = query.getColumnIndex(CacheTableMeta.COLUMN_INDEX);
  //    String index = query.getString(columnIndex);
  //
  //    db.delete(CacheTableMeta.TABLE,
  //        CacheTableMeta.COLUMN_KEY + " = ? AND " + CacheTableMeta.COLUMN_INDEX + " = ?",
  //        cacheEntry.getKey(), index);
  //  }
  //
  //  for (StoreObject storeObject : objectsToPersist) {
  //    db.insert(CacheTableMeta.TABLE, StoreObject.toContentValues(storeObject));
  //  }
  //
  //  transaction.markSuccessful();
  //  transaction.end();
  //}
  //
  //@Deprecated
  //private <T> void persistSingleObject(CacheEntry cacheEntry) {
  //  // New Implementation
  //  Class type = cacheEntry.getType();
  //
  //  Serializer<Object> serialiser = new GsonSerializer<>(gson);
  //
  //  byte[] bytes = serialiser.toBytes(cacheEntry.getValue());
  //  Object object = serialiser.fromBytes(bytes, type);
  //
  //  // Old implementation
  //
  //  byte[] value = gson.toJson(cacheEntry.getValue()).getBytes();
  //  StoreObject storeObject =
  //      StoreObject.create(cacheEntry.getKey(), cacheEntry.getType().getCanonicalName(), value,
  //          cacheEntry.getExpiryMillis(), null /*index*/, Object.class.getSimpleName(),
  //          System.currentTimeMillis());
  //
  //  ContentValues contentValues = StoreObject.toContentValues(storeObject);
  //
  //  db.delete(CacheTableMeta.TABLE, CacheTableMeta.COLUMN_KEY + " = ?", cacheEntry.getKey());
  //  db.insert(CacheTableMeta.TABLE, contentValues);
  //}

  //private Object getObjectFromCursor(Cursor result) {
  //  Class<?> type;
  //
  //  //while (result.moveToNext()) {
  //  int typeColumnIndex = result.getColumnIndex(CacheTableMeta.COLUMN_TYPE);
  //  int valueColumnIndex = result.getColumnIndex(CacheTableMeta.COLUMN_VALUE);
  //
  //  try {
  //    String typeStringDb = result.getString(typeColumnIndex);
  //    byte[] value = result.getBlob(valueColumnIndex);
  //
  //    type = Class.forName(typeStringDb);
  //    String jsonValue = new String(value);
  //    return gson.fromJson(jsonValue, type);
  //  } catch (ClassNotFoundException e) {
  //    // TODO: 17/09/15 Handle this error
  //    e.printStackTrace();
  //  }
  //
  //  return null;
  //}
  //
  //private String getKeyDbFromCursor(Cursor result) {
  //  int keyColumnIndex = result.getColumnIndex(CacheTableMeta.COLUMN_KEY);
  //  return result.getString(keyColumnIndex);
  //}
  //
  //private long getExpiryMillisFromCursor(Cursor result) {
  //  int expiryColumnIndex = result.getColumnIndex(CacheTableMeta.COLUMN_EXPIRY_MILLIS);
  //  return result.getLong(expiryColumnIndex);
  //}
  //
  //private Class<?> getClassTypeFromCursor(Cursor result) throws ClassNotFoundException {
  //  int typeColumnIndex = result.getColumnIndex(CacheTableMeta.COLUMN_TYPE);
  //  String type = result.getString(typeColumnIndex);
  //  return Class.forName(type);
  //}
}
