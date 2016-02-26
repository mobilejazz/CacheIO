package com.mobilejazz.cacheio.alternative;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import rx.Scheduler;
import rx.Single;
import rx.SingleSubscriber;

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

    private String generatePlaceholders(K... keys) {
        final StringBuilder builder = new StringBuilder();
        for (K key : keys) {
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


    @Override
    public Single<Map<K, V>> getAll(K... keys) {
        return getAll(config.scheduler, keys);
    }

    @Override
    public Single<Map<K, V>> getAll(Scheduler scheduler, K... keys) {

        final Date now = new Date();
        final String timeStr = Long.toString(now.getTime());

        final String sql = "SELECT * FROM " + config.tableName + " WHERE " + COLUMN_EXPIRES + " > ? AND " + COLUMN_KEY + " IN (" + generatePlaceholders(keys) + ")";

        final String[] keysAsStrings = keysAsString(keys);
        final String[] args = new String[keysAsStrings.length + 1];
        args[0] = timeStr;

        for (int i = 0; i < keysAsStrings.length; i++) {
            args[i + 1] = keysAsStrings[i];
        }

        final Cursor cursor = config.db.rawQuery(sql, keysAsStrings);

        final MappingContext mappingContext = config.mappingContext;

        return Single.create(new Single.OnSubscribe<Map<K, V>>() {
            @Override
            public void call(SingleSubscriber<? super Map<K, V>> subscriber) {

                final Map<K, V> result = new HashMap<>();

                while(cursor.moveToNext()){

                    final String keyString = cursor.getString(cursor.getColumnIndex(COLUMN_KEY));
                    final byte[] blob = cursor.getBlob(cursor.getColumnIndex(COLUMN_VALUE));

                    final ByteArrayInputStream bytesIn = new ByteArrayInputStream(blob);

                    final K key = config.keyMapper.fromString(keyString);
                    final V value = mappingContext.read(config.valueType, bytesIn);

                    result.put(key, value);

                }

                subscriber.onSuccess(result);

            }
        }).observeOn(scheduler);
    }


    @Override
    public Single<Void> putAll(Map<K, V> map, Date expiresAt) {
        return putAll(config.scheduler, map, expiresAt);
    }

    @Override
    public Single<Void> putAll(Scheduler scheduler, final Map<K, V> map, final Date expiresAt) {

        final MappingContext mappingContext = config.mappingContext;
        final SQLiteDatabase db = config.db;

        final Date createdAt = new Date();

        return Single.create(new Single.OnSubscribe<Void>() {
            @Override
            public void call(SingleSubscriber<? super Void> subscriber) {

                db.beginTransaction();

                try {

                    for (K key : map.keySet()) {

                        final V value = map.get(key);
                        final ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
                        mappingContext.write(value, bytesOut);

                        bytesOut.close();

                        final ContentValues values = new ContentValues(2);
                        values.put(COLUMN_KEY, key.toString());
                        values.put(COLUMN_VALUE, bytesOut.toByteArray());
                        values.put(COLUMN_CREATED, createdAt.getTime());
                        values.put(COLUMN_EXPIRES, expiresAt.getTime());

                        db.insert(config.tableName, null, values);

                    }

                    db.endTransaction();

                    subscriber.onSuccess(null);

                } catch (Throwable t) {
                    subscriber.onError(t);
                }

            }
        }).observeOn(scheduler);

    }


    @Override
    public Single<Void> removeAll(K... keys) {
        return removeAll(config.scheduler, keys);
    }

    @Override
    public Single<Void> removeAll(Scheduler scheduler, final K... keys) {

        final SQLiteDatabase db = config.db;

        return Single.create(new Single.OnSubscribe<Void>() {
            @Override
            public void call(SingleSubscriber<? super Void> subscriber) {

                try {

                    db.beginTransaction();

                    final String sql = "SELECT * FROM " + config.tableName + " WHERE " + COLUMN_KEY + " IN (" + generatePlaceholders(keys) + ")";
                    db.execSQL(sql, keysAsString(keys));

                    db.endTransaction();

                } catch(Throwable t){
                    subscriber.onError(t);
                }

            }
        }).observeOn(scheduler);

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
        private MappingContext mappingContext;

        private Scheduler scheduler;

        private Builder() {
        }

        private Builder(Builder<K, V> proto) {
            this.context = proto.context;
            this.databaseName = proto.databaseName;
            this.keyType = proto.keyType;
            this.valueType = proto.valueType;
            this.db = proto.db;
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

        public Builder<K, V> setMappingContext(MappingContext mappingContext) {
            this.mappingContext = mappingContext;
            return this;
        }

        public SQLiteCache<K, V> build() {

            // assertions
            checkNotNull(context, "Context cannot be null");
            checkNotNull(databaseName, "Database name cannot be null");
            checkNotNull(keyType, "Key type cannot be null");
            checkNotNull(valueType, "Value type cannot be null");
            checkNotNull(tableName, "Table name cannot be null");
            checkNotNull(keyMapper, "Key mapper cannot be null");
            checkNotNull(mappingContext, "Mapping context cannot be null");
            checkNotNull(scheduler, "Scheduler cannot be null");

            // defaults
            if (this.tableName == null) {
                this.tableName = "Cache_" + keyType.getName() + "__" + valueType.getCanonicalName().replaceAll(".", "_");
            }

            //
            final DbHelper dbHelper = new DbHelper(context, databaseName);
            this.db = dbHelper.getWritableDatabase();

            //
            return new SQLiteCache<>(this);
        }
    }

    private static final class DbHelper extends SQLiteOpenHelper {

        static final int DATABASE_VERSION = 1;

        static final String CREATE_FORMAT = "CREATE TABLE %s ( " +
                "   key TEXT PRIMARY KEY, value BLOB NOT NULL, " +
                "   created REAL NOT NULL, expires REAL NOT NULL" +
                ")";

        static final String DROP_FORMAT = "DROP TABLE IF EXISTS %s";

        public DbHelper(Context context, String databaseName){
            super(context, databaseName, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(String.format(CREATE_FORMAT, getDatabaseName()));
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(String.format(DROP_FORMAT, getDatabaseName()));
            onCreate(db);
        }
    }
}
