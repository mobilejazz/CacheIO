package com.mobilejazz.cacheio;

import android.content.Context;
import com.google.gson.Gson;
import com.mobilejazz.cacheio.manager.CacheOpenHelper;
import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;

public class CacheIO {

  private Cache cache;

  public CacheIO(Gson gson, String dbName, Context context, boolean logging) {
    SqlBrite sqlBrite = SqlBrite.create();
    BriteDatabase briteDatabase = sqlBrite.wrapDatabaseHelper(new CacheOpenHelper(context, dbName));

    Persitence persitence = new PersitenceSqlLite(briteDatabase);
    Serializer serializer = new GsonSerializer(gson);

    cache = new CacheManager(persitence, serializer);
  }

  public Cache cacheDataSource() {
    return cache;
  }

  public static Builder with(Context context) {
    return new Builder(context);
  }

  public static class Builder {

    private Gson gson;
    private String dbName;
    private boolean logging;
    private Context context;

    public Builder(Context context) {
      this.context = context;
    }

    public Builder addLogging(boolean value) {
      this.logging = value;
      return this;
    }

    public Builder addDbName(String value) {
      this.dbName = value;
      return this;
    }

    public Builder addGson(Gson gson) {
      this.gson = gson;
      return this;
    }

    public CacheIO build() {
      // TODO: 17/09/15 Check all the values

      return new CacheIO(gson, dbName, context, logging);
    }
  }
}
