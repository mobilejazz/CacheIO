package com.mobilejazz.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import com.mobilejazz.cacheio.CacheIO;
import com.mobilejazz.cacheio.FutureCache;
import com.mobilejazz.cacheio.RxCache;
import com.mobilejazz.cacheio.SyncCache;
import com.mobilejazz.cacheio.serializers.gson.GsonValueMapper;
import com.mobilejazz.sample.gson.GsonFactory;
import com.mobilejazz.sample.model.User;

import java.util.concurrent.*;

public class InitialActivity extends AppCompatActivity {

  private static final String TAG = InitialActivity.class.getSimpleName();
  public static final String USER_KEY_LIST = "user.key.list";
  public static final String USER_KEY_ONE = "user.key.one";

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_initial);

    CacheIO cacheIO = CacheIO.with(getApplicationContext())
        .identifier("cacheio_test")
        .executor(Executors.newSingleThreadExecutor())
        .setValueMapper(new GsonValueMapper(GsonFactory.create()))
        .build();

    SyncCache<String, User> syncCache = cacheIO.newSyncCache(String.class, User.class);
    FutureCache<String, User> futureCache = cacheIO.newFutureCache(String.class, User.class);
    RxCache<String, User> rxCache = cacheIO.newRxCache(String.class, User.class);

    User userOne = new User();
    userOne.setId(1);
    userOne.setName("Jose Luis Franconetti");

    syncCache.put("test.dumy", userOne, Long.MAX_VALUE, TimeUnit.SECONDS);

    User user = syncCache.get("test.dumy");

    Log.d(TAG, user.toString());

  }
}
