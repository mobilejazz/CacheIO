package com.mobilejazz.cacheio.alternative;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.mobilejazz.cacheio.alternative.mappers.IntegerKeyMapper;
import com.mobilejazz.cacheio.alternative.mappers.LongKeyMapper;
import com.mobilejazz.cacheio.alternative.mappers.StringKeyMapper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import rx.Scheduler;
import rx.Single;
import rx.SingleSubscriber;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static com.mobilejazz.cacheio.internal.helper.Preconditions.checkNotNull;


public class SQLiteCache<K, V> implements Cache<K, V> {

    private static final String COLUMN_KEY = "key";
    private static final String COLUMN_VALUE = "value";
    private static final String COLUMN_CREATED = "created";
    private static final String COLUMN_EXPIRES = "expires";

    private final Builder<K, V> config;

    private SQLiteCache(Builder<K, V> proto) {
        this.config = new Builder<>(proto);
    }

    @Override
    public Single<V> put(K key, V value, long expiry, TimeUnit unit) {
        return put(config.scheduler, key, value, expiry, unit);
    }

    @Override
    public Single<V> put(Scheduler scheduler, final K key, V value, long expiry, TimeUnit unit) {
        final Map<K, V> map = new HashMap<>(1);
        map.put(key, value);

        return putAll(map, expiry, unit)
                .map(new Func1<Map<K,V>, V>() {
                    @Override
                    public V call(Map<K, V> map) {
                        return map.get(key);
                    }
                });
    }

    @Override
    public Single<Map<K, V>> getAll(K... keys) {
        return getAll(config.scheduler, keys);
    }

    @Override
    public Single<Map<K, V>> getAll(Scheduler scheduler, K... keys) {

        final Date now = new Date();
        final String timeStr = Long.toString(now.getTime());

        final String sql = "SELECT * FROM " + config.tableName + " WHERE " + COLUMN_EXPIRES + " > ? AND " + COLUMN_KEY + " IN (" + generatePlaceholders(keys.length) + ")";

        final String[] keysAsStrings = keysAsString(keys);
        final String[] args = new String[keysAsStrings.length + 1];
        args[0] = timeStr;

        System.arraycopy(keysAsStrings, 0, args, 1, keysAsStrings.length);

        final Mapper valueMapper = config.valueMapper;

        return Single.create(new Single.OnSubscribe<Map<K, V>>() {
            @Override
            public void call(SingleSubscriber<? super Map<K, V>> subscriber) {

                final Cursor cursor = config.db.rawQuery(sql, args);

                final Map<K, V> result = new HashMap<>();

                while (cursor.moveToNext()) {

                    final String keyString = cursor.getString(cursor.getColumnIndex(COLUMN_KEY));
                    final byte[] blob = cursor.getBlob(cursor.getColumnIndex(COLUMN_VALUE));

                    final ByteArrayInputStream bytesIn = new ByteArrayInputStream(blob);

                    final K key = config.keyMapper.fromString(keyString);
                    final V value = valueMapper.read(config.valueType, bytesIn);

                    result.put(key, value);

                }

                subscriber.onSuccess(result);

                cursor.close();

            }
        }).observeOn(scheduler);
    }


    @Override
    public Single<Map<K, V>> putAll(Map<K, V> map, long expiry, TimeUnit unit) {
        return putAll(config.scheduler, map, expiry, unit);
    }

    @Override
    public Single<Map<K, V>> putAll(Scheduler scheduler, final Map<K, V> map, long expiry, TimeUnit unit) {

        final Mapper valueMapper = config.valueMapper;
        final SQLiteDatabase db = config.db;

        final long createdAt = System.currentTimeMillis();
        final long expiresAt = createdAt + unit.toMillis(expiry);

        return Single.create(new Single.OnSubscribe<Map<K, V>>() {

            @Override
            public void call(SingleSubscriber<? super Map<K, V>> subscriber) {

                db.beginTransaction();

                try {

                    // Delete the keys first

                    final Set<K> keys = map.keySet();

                    final String sql = "DELETE FROM " + config.tableName + " WHERE " + COLUMN_KEY + " IN (" + generatePlaceholders(keys.size()) + ")";
                    db.execSQL(sql, keysAsString(keys));

                    // Insert
                    for (K key : keys) {

                        final V value = map.get(key);
                        final ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
                        valueMapper.write(value, bytesOut);

                        bytesOut.close();

                        final ContentValues values = new ContentValues(2);
                        values.put(COLUMN_KEY, key.toString());
                        values.put(COLUMN_VALUE, bytesOut.toByteArray());
                        values.put(COLUMN_CREATED, createdAt);
                        values.put(COLUMN_EXPIRES, expiresAt);

                        db.insert(config.tableName, null, values);

                    }

                    db.setTransactionSuccessful();

                    subscriber.onSuccess(map);

                } catch (Throwable t) {
                    subscriber.onError(t);
                } finally {
                    db.endTransaction();
                }

            }
        }).observeOn(scheduler);

    }


    @Override
    public Single<K[]> removeAll(K... keys) {
        return removeAll(config.scheduler, keys);
    }

    @Override
    public Single<K[]> removeAll(Scheduler scheduler, final K... keys) {

        final SQLiteDatabase db = config.db;

        return Single.create(new Single.OnSubscribe<K[]>() {
            @Override
            public void call(SingleSubscriber<? super K[]> subscriber) {

                try {

                    db.beginTransaction();

                    final String sql = "DELETE * FROM " + config.tableName + " WHERE " + COLUMN_KEY + " IN (" + generatePlaceholders(keys.length) + ")";
                    db.execSQL(sql, keysAsString(keys));

                    db.endTransaction();

                } catch(Throwable t){
                    subscriber.onError(t);
                }

            }
        }).observeOn(scheduler);

    }

    private static String generatePlaceholders(int count) {
        final StringBuilder builder = new StringBuilder();
        for(int i=0; i<count; i++){
            builder.append(",").append("?");
        }
        return builder.toString().substring(1);
    }

    private String[] keysAsString(K... keys) {
        String[] result = new String[keys.length];
        for (int i = 0; i < keys.length; i++) {
            result[i] = config.keyMapper.toString(keys[i]);
        }
        return result;
    }

    private String[] keysAsString(Collection<K> keys) {
        String[] result = new String[keys.size()];
        int idx = 0;
        for (K key : keys) {
            result[idx++] = config.keyMapper.toString(key);
        }
        return result;
    }


    public static <K, V> Builder<K, V> newBuilder(Class<K> keyType, Class<V> valueType) {
        return new Builder<K, V>()
                .setKeyType(keyType)
                .setValueType(valueType);
    }

    public static final class Builder<K, V> {

        private Context context;
        private String databaseName;

        private Class<K> keyType;
        private Class<V> valueType;

        private String tableName;
        private SQLiteDatabase db;

        private KeyMapper<K> keyMapper;
        private Mapper valueMapper;

        private Scheduler scheduler = Schedulers.immediate();

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
            this.db = proto.db;
            this.scheduler = proto.scheduler;
        }

        public Builder<K, V> setContext(Context context) {
            this.context = context;
            return this;
        }

        public Builder<K, V> setScheduler(Scheduler scheduler) {
            this.scheduler = scheduler;
            return this;
        }

        public Builder<K, V> setDatabaseName(String databaseName) {
            this.databaseName = databaseName;
            return this;
        }

        public Builder<K, V> setKeyType(Class<K> keyType) {
            this.keyType = keyType;
            return this;
        }

        public Builder<K, V> setValueType(Class<V> valueType) {
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

        public Builder<K, V> setValueMapper(Mapper valueMapper) {
            this.valueMapper = valueMapper;
            return this;
        }

        @SuppressWarnings("unchecked")
        public Cache<K, V> build() {

            // defaults
            if (this.tableName == null) {
                this.tableName = valueType.getCanonicalName().replaceAll(".", "_");
            }

            if(keyMapper == null){

                if(keyType == String.class){
                    keyMapper = (KeyMapper<K>) new StringKeyMapper();
                } else if(keyType == Integer.class){
                    keyMapper = (KeyMapper<K>) new IntegerKeyMapper();
                } else if(keyType == int.class){
                    keyMapper = (KeyMapper<K>) new IntegerKeyMapper();
                } else if(keyType == Long.class){
                    keyMapper = (KeyMapper<K>) new LongKeyMapper();
                } else if(keyType == long.class){
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
            checkNotNull(scheduler, "Scheduler cannot be null");


            //
            final DbHelper dbHelper = new DbHelper(context, databaseName, tableName);
            this.db = dbHelper.getWritableDatabase();

            dbHelper.onCreate(db);      // ensure the table is created
            dbHelper.removeExpired();   // remove any expired entries

            //
            return new SQLiteCache<>(this);
        }
    }

    private static final class DbHelper extends SQLiteOpenHelper {

        static final int DATABASE_VERSION = 1;

        static final String CREATE_FORMAT = "CREATE TABLE IF NOT EXISTS %s ( " +
                "   key TEXT PRIMARY KEY, value BLOB NOT NULL, " +
                "   created REAL NOT NULL, expires REAL NOT NULL" +
                ")";

        static final String DROP_FORMAT = "DROP TABLE IF EXISTS %s";

        static final String DELETE_EXPIRED_FORMAT = "DELETE FROM %s WHERE " + COLUMN_EXPIRES + " < ?";

        private final String tableName;

        public DbHelper(Context context, String databaseName, String tableName){
            super(context, databaseName, null, DATABASE_VERSION);
            this.tableName = tableName;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(String.format(CREATE_FORMAT, tableName));
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(String.format(DROP_FORMAT, getDatabaseName()));
            onCreate(db);
        }

        public void removeExpired(){
            final long now = System.currentTimeMillis();
            getWritableDatabase().execSQL(String.format(DELETE_EXPIRED_FORMAT, tableName), new Object[]{now});
        }
    }
}
