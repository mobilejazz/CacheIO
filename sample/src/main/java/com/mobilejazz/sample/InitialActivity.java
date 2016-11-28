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
    FutureCache<String, User> futureCache =
        cacheIO.newFutureCache(String.class, User.class);
    RxCache<String, User> rxCache = cacheIO.newRxCache(String.class, User.class);



    User userOne = new User();
    userOne.setId(1);
    userOne.setName("Jose Luis Franconetti");

    syncCache.put("test.dumy", userOne, Long.MAX_VALUE, TimeUnit.SECONDS);

    User user = syncCache.get("test.dumy");

    Log.d(TAG, user.toString());





    /*
    CacheIO cacheIO = CacheIO.with(getApplicationContext())
        .identifier("cacheio-test")
        .logLevel(LogLevel.FULL)
        .serializer(new GsonSerializer(GsonFactory.create()))
        .build();

    Cache cache = cacheIO.cacheDataSource();

    User userOne = new User();
    userOne.setId(1);
    userOne.setName("Jose Luis Franconetti");

    User userTwo = new User();
    userTwo.setId(2);
    userTwo.setName("Test user");

    CacheEntry cacheEntryUserOne = CacheEntry.create(USER_KEY_ONE, User.class, userOne, 12);
    cache.persist(cacheEntryUserOne);
    cache.persist(cacheEntryUserOne);
    cache.persist(cacheEntryUserOne);

    List<User> users = new ArrayList<>();
    users.add(userOne);
    users.add(userTwo);

    CacheEntry cacheEntryUserList = CacheEntry.create(USER_KEY_LIST, User.class, users, 12);
    cache.persist(cacheEntryUserList);
    cache.persist(cacheEntryUserList);
    cache.persist(cacheEntryUserList);

    CacheEntry resultQueryUserOne = cache.obtain(USER_KEY_ONE);

    User resultUser = (User) resultQueryUserOne.getValue();
    Log.d(TAG, "User One = " + resultUser.toString());

    CacheEntry resultQueryUserList = cache.obtain(USER_KEY_LIST);


    List<User> resultQueryListUsers = (List<User>) resultQueryUserList.getValue();
    for (User user : resultQueryListUsers) {
      Log.d(TAG, "List - " + user.toString());
    }
    */

/*    User userThree = new User();
    userThree.setId(3);
    userThree.setName("Aldo Borrero");*/

    //CacheEntry cacheEntryUserThree = CacheEntry.create("user.key.three", User.class, userThree);
    //cache.persist(cacheEntryUserThree);
    //cache.delete("user.key.three");

    //CacheEntry emptyUserThreeEntry = cache.obtain("user.key.three");
    //Log.d(TAG, "Cache is emptied for User three: " + String.valueOf(emptyUserThreeEntry == null));
  }
}
