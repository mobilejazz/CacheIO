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

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.mobilejazz.cacheio.alternative.RxCache;
import com.mobilejazz.cacheio.alternative.mappers.KeyMapper;
import com.mobilejazz.cacheio.alternative.mappers.ValueMapper;
import com.mobilejazz.cacheio.alternative.mappers.VersionMapper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;

import com.mobilejazz.cacheio.alternative.mappers.defaults.IntegerKeyMapper;
import com.mobilejazz.cacheio.alternative.mappers.defaults.LongKeyMapper;
import com.mobilejazz.cacheio.alternative.mappers.defaults.StringKeyMapper;
import rx.Single;
import rx.SingleSubscriber;
import rx.functions.Func1;

import static com.mobilejazz.cacheio.internal.helper.Preconditions.checkNotNull;

public class SQLiteRxCache<K, V> implements RxCache<K, V> {

  private static final String COLUMN_KEY = "key";
  private static final String COLUMN_VALUE = "value";
  private static final String COLUMN_VERSION = "version";
  private static final String COLUMN_CREATED = "created";
  private static final String COLUMN_EXPIRES = "expires";

  private final Builder<K, V> config;

  private SQLiteRxCache(Builder<K, V> proto) {
    this.config = new Builder<>(proto);
  }

  @Override public Single<V> get(final K key) {

    final Collection<K> keys = new ArrayList<>(1);
    keys.add(key);

    return getAll(keys).map(new Func1<Map<K, V>, V>() {
      @Override public V call(Map<K, V> map) {
        return map.get(key);
      }
    });

  }

  @Override public Single<Map<K, V>> getAll(final Collection<K> keys) {
    final Future<Map<K, V>> future = config.executor.submit(new GetAll(keys));
    return Single.from(future);
  }

  @Override public Single<V> put(final K key, V value, long expiry, TimeUnit unit) {

    final Map<K, V> map = new HashMap<>(1);
    map.put(key, value);

    return putAll(map, expiry, unit).map(new Func1<Map<K, V>, V>() {
      @Override public V call(Map<K, V> map) {
        return map.get(key);
      }
    });

  }

  @Override
  public Single<Map<K, V>> putAll(final Map<K, V> map, final long expiry, final TimeUnit unit) {
    final Future<Map<K, V>> future = config.executor.submit(new PutAll(map, expiry, unit));
    return Single.from(future);
  }

  @Override public Single<K> remove(K key) {

    final Collection<K> keys = new ArrayList<>(1);
    keys.add(key);

    return removeAll(keys).map(new Func1<Collection<K>, K>() {
      @Override public K call(Collection<K> keys) {
        return keys.iterator().next();
      }
    });

  }

  @Override public Single<Collection<K>> removeAll(final Collection<K> keys) {

    final Future<Collection<K>> future = config.executor.submit(new RemoveAll(keys));
    return Single.from(future);

  }

  private static String generatePlaceholders(int count) {
    final StringBuilder builder = new StringBuilder();
    for (int i = 0; i < count; i++) {
      builder.append(",").append("?");
    }
    return builder.toString().substring(1);
  }

  private String[] keysAsString(Collection<K> keys) {
    String[] result = new String[keys.size()];
    int idx = 0;
    for (K key : keys) {
      result[idx++] = config.keyMapper.toString(key);
    }
    return result;
  }

  private final class GetAll implements Callable<Map<K, V>> {

    private final Collection<K> keys;

    private final Date now = new Date();

    public GetAll(Collection<K> keys) {
      this.keys = keys;
    }

    @Override public Map<K, V> call() throws Exception {
      final String timeStr = Long.toString(now.getTime());

      final String sql = "SELECT * FROM "
          + config.tableName
          + " WHERE "
          + COLUMN_EXPIRES
          + " > ? AND "
          + COLUMN_KEY
          + " IN ("
          + generatePlaceholders(keys.size())
          + ")";

      final String[] keysAsStrings = keysAsString(keys);
      final String[] args = new String[keysAsStrings.length + 1];
      args[0] = timeStr;

      System.arraycopy(keysAsStrings, 0, args, 1, keysAsStrings.length);

      final ValueMapper valueValueMapper = config.valueMapper;

      final Cursor cursor = config.db.rawQuery(sql, args);

      final Map<K, V> result = new HashMap<>();

      while (cursor.moveToNext()) {

        final String keyString = cursor.getString(cursor.getColumnIndex(COLUMN_KEY));
        final byte[] blob = cursor.getBlob(cursor.getColumnIndex(COLUMN_VALUE));

        final ByteArrayInputStream bytesIn = new ByteArrayInputStream(blob);

        final K key = config.keyMapper.fromString(keyString);
        final V value = valueValueMapper.read(config.valueType, bytesIn);

        result.put(key, value);

      }

      cursor.close();

      return result;
    }

  }

  private final class PutAll implements Callable<Map<K, V>> {

    private final Map<K, V> map;
    private final long expiry;
    private final TimeUnit expiryUnit;

    private final Date now = new Date();

    public PutAll(Map<K, V> map, long expiry, TimeUnit expiryUnit) {
      this.map = map;
      this.expiry = expiry;
      this.expiryUnit = expiryUnit;
    }

    @Override public Map<K, V> call() throws Exception {
      final SQLiteDatabase db = config.db;
      final KeyMapper<K> keyMapper = config.keyMapper;
      final VersionMapper<V> versionMapper = config.versionMapper;
      final ValueMapper valueMapper = config.valueMapper;

      try {

        final Map<K, V> result = new HashMap<>(map.size());

        final long createdAt = now.getTime();
        final long expiresAt = createdAt + expiryUnit.toMillis(expiry);

        db.beginTransaction();

        final Set<K> keys = map.keySet();
        final Set<K> keysToUpdate = new HashSet<K>(keys.size());

        for (K key : keys) {

          final V value = map.get(key);
          final String keyStr = keyMapper.toString(key);
          final String versionStr = Long.toString(versionMapper.getVersion(value));

          final Cursor cursor =
              db.query(config.tableName, new String[] { COLUMN_KEY }, "key = ? AND version > ?",
                  new String[] { keyStr, versionStr }, null, null, null, null);

          if (cursor.getCount() == 0) {
            // we have the latest version and need to update
            keysToUpdate.add(key);
          }

          cursor.close();

        }

        if (keysToUpdate.size() > 0) {

          // delete previous entries

          final String sql = "DELETE FROM "
              + config.tableName
              + " WHERE "
              + COLUMN_KEY
              + " IN ("
              + generatePlaceholders(keysToUpdate.size())
              + ")";
          db.execSQL(sql, keysAsString(keysToUpdate));

          // Insert new entries

          for (K key : keysToUpdate) {

            final V value = map.get(key);
            final long version = versionMapper.getVersion(value);

            final ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
            valueMapper.write(value, bytesOut);

            bytesOut.close();

            final ContentValues values = new ContentValues(2);
            values.put(COLUMN_KEY, key.toString());
            values.put(COLUMN_VALUE, bytesOut.toByteArray());
            values.put(COLUMN_VERSION, version);
            values.put(COLUMN_CREATED, createdAt);
            values.put(COLUMN_EXPIRES, expiresAt);

            db.insert(config.tableName, null, values);

            result.put(key, value);

          }

        }

        db.setTransactionSuccessful();

        return result;

      } finally {
        db.endTransaction();
      }
    }

  }

  private final class RemoveAll implements Callable<Collection<K>> {

    private final Collection<K> keys;

    private final Date now = new Date();

    public RemoveAll(Collection<K> keys) {
      this.keys = keys;
    }

    @Override public Collection<K> call() throws Exception {

      final SQLiteDatabase db = config.db;

      try {

        db.beginTransaction();

        final String sql = "DELETE FROM "
            + config.tableName
            + " WHERE "
            + COLUMN_KEY
            + " IN ("
            + generatePlaceholders(keys.size())
            + ")";

        db.execSQL(sql, keysAsString(keys));

        db.setTransactionSuccessful();

        return keys;

      } finally {
        db.endTransaction();
      }

    }

  }

  public static <K, V> Builder<K, V> newBuilder(Class<K> keyType, Class<V> valueType) {
    return new Builder<K, V>().setKeyType(keyType).setValueType(valueType);
  }

  public static final class Builder<K, V> {

    private Context context;
    private String databaseName;

    private Class<K> keyType;
    private Class<V> valueType;

    private String tableName;
    private SQLiteDatabase db;

    private KeyMapper<K> keyMapper;
    private ValueMapper valueMapper;
    private VersionMapper<V> versionMapper;

    private ExecutorService executor;

    private Builder() {
    }

    private Builder(Builder<K, V> proto) {
      this.keyType = proto.keyType;
      this.valueType = proto.valueType;
      this.context = proto.context;
      this.databaseName = proto.databaseName;
      this.tableName = proto.tableName;
      this.keyMapper = proto.keyMapper;
      this.valueMapper = proto.valueMapper;
      this.versionMapper = proto.versionMapper;
      this.db = proto.db;
      this.executor = proto.executor;
    }

    public Builder<K, V> setContext(Context context) {
      this.context = context;
      return this;
    }

    public Builder<K, V> setScheduler(ExecutorService executor) {
      this.executor = executor;
      return this;
    }

    public Builder<K, V> setDatabaseName(String databaseName) {
      this.databaseName = databaseName;
      return this;
    }

    protected Builder<K, V> setKeyType(Class<K> keyType) {
      this.keyType = keyType;
      return this;
    }

    protected Builder<K, V> setValueType(Class<V> valueType) {
      this.valueType = valueType;
      return this;
    }

    public Builder<K, V> setTableName(String tableName) {
      this.tableName = tableName;
      return this;
    }

    public Builder<K, V> setKeyMapper(KeyMapper<K> keyMapper) {
      this.keyMapper = keyMapper;
      return this;
    }

    public Builder<K, V> setValueMapper(ValueMapper valueMapper) {
      this.valueMapper = valueMapper;
      return this;
    }

    public Builder<K, V> setVersionMapper(VersionMapper<V> versionMapper) {
      this.versionMapper = versionMapper;
      return this;
    }

    @SuppressWarnings("unchecked") public RxCache<K, V> build() {

      // defaults
      if (this.tableName == null) {
        this.tableName = valueType.getCanonicalName().replaceAll(".", "_");
      }

      if (keyMapper == null) {

        if (keyType == String.class) {
          keyMapper = (KeyMapper<K>) new StringKeyMapper();
        } else if (keyType == Integer.class) {
          keyMapper = (KeyMapper<K>) new IntegerKeyMapper();
        } else if (keyType == int.class) {
          keyMapper = (KeyMapper<K>) new IntegerKeyMapper();
        } else if (keyType == Long.class) {
          keyMapper = (KeyMapper<K>) new LongKeyMapper();
        } else if (keyType == long.class) {
          keyMapper = (KeyMapper<K>) new LongKeyMapper();
        }

      }

      // assertions
      checkNotNull(context, "Context cannot be null");
      checkNotNull(databaseName, "Database name cannot be null");
      checkNotNull(keyType, "Key type cannot be null");
      checkNotNull(valueType, "Value type cannot be null");
      checkNotNull(tableName, "Table name cannot be null");
      checkNotNull(keyMapper, "Key mapper cannot be null");
      checkNotNull(valueMapper, "Mapping context cannot be null");
      checkNotNull(executor, "Executor cannot be null");
      checkNotNull(versionMapper, "Version mapper cannot be null");

      //
      final DbHelper dbHelper = new DbHelper(context, databaseName, tableName);
      this.db = dbHelper.getWritableDatabase();

      dbHelper.onCreate(db);      // ensure the table is created
      dbHelper.removeExpired();   // remove any expired entries

      //
      return new SQLiteRxCache<>(this);
    }
  }

  private static final class DbHelper extends SQLiteOpenHelper {

    static final int DATABASE_VERSION = 1;

    static final String CREATE_FORMAT = "CREATE TABLE IF NOT EXISTS %s ( " +
        "   key TEXT PRIMARY KEY, value BLOB NOT NULL, version REAL NOT NULL, " +
        "   created REAL NOT NULL, expires REAL NOT NULL" +
        ")";

    static final String DROP_FORMAT = "DROP TABLE IF EXISTS %s";

    static final String DELETE_EXPIRED_FORMAT = "DELETE FROM %s WHERE " + COLUMN_EXPIRES + " < ?";

    private final String tableName;

    public DbHelper(Context context, String databaseName, String tableName) {
      super(context, databaseName, null, DATABASE_VERSION);
      this.tableName = tableName;
    }

    @Override public void onCreate(SQLiteDatabase db) {
      db.execSQL(String.format(CREATE_FORMAT, tableName));
    }

    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
      db.execSQL(String.format(DROP_FORMAT, getDatabaseName()));
      onCreate(db);
    }

    public void removeExpired() {
      final long now = System.currentTimeMillis();
      getWritableDatabase().execSQL(String.format(DELETE_EXPIRED_FORMAT, tableName),
          new Object[] { now });
    }
  }
}
