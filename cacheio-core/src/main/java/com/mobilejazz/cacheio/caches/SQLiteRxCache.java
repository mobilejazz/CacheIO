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

package com.mobilejazz.cacheio.caches;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.mobilejazz.cacheio.RxCache;
import com.mobilejazz.cacheio.mappers.KeyMapper;
import com.mobilejazz.cacheio.mappers.ValueMapper;
import com.mobilejazz.cacheio.mappers.VersionMapper;
import com.mobilejazz.cacheio.mappers.version.NoOpVersionMapper;
import rx.Single;
import rx.SingleSubscriber;
import rx.functions.Func1;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

import static com.mobilejazz.cacheio.helper.Preconditions.checkArgument;

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

    return Single.create(new Single.OnSubscribe<Map<K, V>>() {
      @Override public void call(SingleSubscriber<? super Map<K, V>> singleSubscriber) {

        final GetAll task = new GetAll(keys, singleSubscriber);
        config.executor.execute(task);

      }
    });

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

    return Single.create(new Single.OnSubscribe<Map<K, V>>() {
      @Override public void call(SingleSubscriber<? super Map<K, V>> singleSubscriber) {

        final PutAll task = new PutAll(map, expiry, unit, singleSubscriber);
        config.executor.execute(task);

      }
    });

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

    return Single.create(new Single.OnSubscribe<Collection<K>>() {
      @Override public void call(SingleSubscriber<? super Collection<K>> singleSubscriber) {

        final RemoveAll task = new RemoveAll(keys, singleSubscriber);
        config.executor.execute(task);

      }
    });

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

  private final class GetAll implements Runnable {

    private final Collection<K> keys;
    private final SingleSubscriber<? super Map<K, V>> subscriber;

    private final Date now = new Date();

    public GetAll(Collection<K> keys, SingleSubscriber<? super Map<K, V>> subscriber) {
      this.keys = keys;
      this.subscriber = subscriber;
    }

    @Override public void run() {
      try {

        final String timeStr = Long.toString(now.getTime());

        final String sql = "SELECT * FROM "
            + config.tableName
            + " WHERE "
            + COLUMN_EXPIRES
            + " >= ? AND "
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

        subscriber.onSuccess(result);

      } catch (Throwable t) {
        subscriber.onError(t);
      }
    }

  }

  private final class PutAll implements Runnable {

    private final Map<K, V> map;
    private final long expiry;
    private final TimeUnit expiryUnit;
    private final SingleSubscriber<? super Map<K, V>> subscriber;

    private final Date now = new Date();

    public PutAll(Map<K, V> map, long expiry, TimeUnit expiryUnit,
        SingleSubscriber<? super Map<K, V>> subscriber) {
      this.map = map;
      this.expiry = expiry;
      this.expiryUnit = expiryUnit;
      this.subscriber = subscriber;
    }

    @Override public void run() {

      final SQLiteDatabase db = config.db;
      final KeyMapper<K> keyMapper = config.keyMapper;
      final VersionMapper<V> versionMapper = config.versionMapper;
      final ValueMapper valueMapper = config.valueMapper;

      try {

        final Map<K, V> result = new HashMap<>(map.size());

        final long createdAt = now.getTime();
        final long expiresAt =
            expiry == Long.MAX_VALUE ? expiry : createdAt + expiryUnit.toMillis(expiry);

        db.beginTransaction();

        final Set<K> keys = map.keySet();
        final Set<K> keysToUpdate = new HashSet<>(keys);

        final String versionSql = "SELECT " + COLUMN_KEY + ", " + COLUMN_VERSION + " FROM " +
            config.tableName + " WHERE key in (" + generatePlaceholders(keys.size()) + ")";

        final Cursor versionCursor = config.db.rawQuery(versionSql, keysAsString(keys));

        // determine based on version which keys we should update

        while (versionCursor.moveToNext()) {

          final K key = keyMapper.fromString(
              versionCursor.getString(versionCursor.getColumnIndex(COLUMN_KEY)));
          final long version = versionCursor.getLong(versionCursor.getColumnIndex(COLUMN_VERSION));

          final V value = map.get(key);

          final long valueVersion = versionMapper.getVersion(value);

          if (valueVersion != NoOpVersionMapper.UNVERSIONED
              && versionMapper.getVersion(value) < version) {
            // attempting to overwrite a newer version so remove the key from the update list
            keysToUpdate.remove(key);
          }

        }

        versionCursor.close();

        // update

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
        db.endTransaction();

        subscriber.onSuccess(result);

      } catch (Throwable t) {
        db.endTransaction();
        subscriber.onError(t);
      }
    }

  }

  private final class RemoveAll implements Runnable {

    private final Collection<K> keys;
    private final SingleSubscriber<? super Collection<K>> subscriber;

    public RemoveAll(Collection<K> keys, SingleSubscriber<? super Collection<K>> subscriber) {
      this.keys = keys;
      this.subscriber = subscriber;
    }

    @Override public void run() {

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
        db.endTransaction();

        subscriber.onSuccess(keys);

      } catch (Throwable t) {
        db.endTransaction();
        subscriber.onError(t);
      }

    }

  }

  public static <K, V> Builder<K, V> newBuilder(Class<K> keyType, Class<V> valueType) {
    return new Builder<K, V>().setKeyType(keyType).setValueType(valueType);
  }

  public static final class Builder<K, V> {

    public static final String CREATE_TABLE_SQL = "CREATE TABLE IF NOT EXISTS %s ( " +
        "   key TEXT PRIMARY KEY, value BLOB NOT NULL, version REAL NOT NULL, " +
        "   created REAL NOT NULL, expires REAL NOT NULL" +
        ")";

    private static final String DELETE_EXPIRED_SQL =
        "DELETE FROM %s WHERE " + COLUMN_EXPIRES + " " + "< ?";

    private Class<K> keyType;
    private Class<V> valueType;

    private String tableName;
    private SQLiteDatabase db;

    private KeyMapper<K> keyMapper;
    private ValueMapper valueMapper;
    private VersionMapper<V> versionMapper;

    private Executor executor;

    private Builder() {
    }

    private Builder(Builder<K, V> proto) {
      this.db = proto.db;
      this.keyType = proto.keyType;
      this.valueType = proto.valueType;
      this.tableName = proto.tableName;
      this.keyMapper = proto.keyMapper;
      this.valueMapper = proto.valueMapper;
      this.versionMapper = proto.versionMapper;
      this.executor = proto.executor;
    }

    public Builder<K, V> setExecutor(Executor executor) {
      this.executor = executor;
      return this;
    }

    public Builder<K, V> setDatabase(SQLiteDatabase db) {
      this.db = db;
      return this;
    }

    private Builder<K, V> setKeyType(Class<K> keyType) {
      this.keyType = keyType;
      return this;
    }

    private Builder<K, V> setValueType(Class<V> valueType) {
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

    private void removeExpired() {
      final long now = System.currentTimeMillis();
      db.execSQL(String.format(DELETE_EXPIRED_SQL, tableName), new Object[] { now });
    }

    @SuppressWarnings("unchecked") public RxCache<K, V> build() {

      // defaults

      if (this.tableName == null) {
        this.tableName = keyType.getName().replaceAll("\\.", "_") + "___" + valueType.getName()
            .replaceAll("\\.", "_");
      }

      if (this.versionMapper == null) {
        this.versionMapper = new NoOpVersionMapper<>();
      }

      // assertions

      checkArgument(db, "Database cannot be null");
      checkArgument(keyType, "Key type cannot be null");
      checkArgument(valueType, "Value type cannot be null");
      checkArgument(tableName, "Table name cannot be null");
      checkArgument(keyMapper, "Key mapper cannot be null");
      checkArgument(valueMapper, "Mapping context cannot be null");
      checkArgument(executor, "Executor cannot be null");
      checkArgument(versionMapper, "Version mapper cannot be null");
      checkArgument(tableName, "Table name cannot be null");

      // create the table if it doesn't exist

      db.execSQL(String.format(CREATE_TABLE_SQL, tableName));

      // clear out all expired entries
      removeExpired();

      //
      return new SQLiteRxCache<>(this);
    }
  }

}
