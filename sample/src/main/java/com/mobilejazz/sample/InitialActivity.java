package com.mobilejazz.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import com.mobilejazz.cacheio.Cache;
import com.mobilejazz.cacheio.CacheIO;
import com.mobilejazz.cacheio.manager.entity.CacheEntry;
import com.mobilejazz.sample.gson.GsonFactory;
import com.mobilejazz.sample.model.User;
import java.util.ArrayList;
import java.util.List;

public class InitialActivity extends AppCompatActivity {

  private static final String TAG = InitialActivity.class.getSimpleName();

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_initial);

    CacheIO cacheIO = CacheIO.with(getApplicationContext())
        .addLogging(true)
        .addDbName("cache.http")
        .addGson(GsonFactory.create())
        .build();

    Cache cache = cacheIO.cacheDataSource();

    User userOne = new User();
    userOne.setId(1);
    userOne.setName("Jose Luis Franconetti");

    User userTwo = new User();
    userTwo.setId(2);
    userTwo.setName("Test user");

    CacheEntry cacheEntryUserOne = CacheEntry.create("user.key.one", User.class, userOne, 12);
    cache.persist(cacheEntryUserOne);

    List<User> users = new ArrayList<>();
    users.add(userOne);
    users.add(userTwo);

    //CacheEntry cacheEntryUserList = CacheEntry.create("user.key.list", User.class, users, 12);
    CacheEntry<List<User>> cacheEntryUserList = new CacheEntry();
    cacheEntryUserList.setKey("user.key.list");
    cacheEntryUserList.setType(User.class);
    cacheEntryUserList.setValue(users);
    cacheEntryUserList.setExpiryMillis(12);


    cache.persist(cacheEntryUserList);

    CacheEntry resultQueryUserOne = cache.obtain("user.key.one");

    User resultUser = (User) resultQueryUserOne.getValue();
    Log.d(TAG, resultUser.toString());

    CacheEntry resultQueryUserList = cache.obtain("user.key.list");

    List<User> resultQueryListUsers = (List<User>) resultQueryUserList.getValue();
    for (User user : resultQueryListUsers) {
      Log.d(TAG, user.toString());
    }

    User userThree = new User();
    userThree.setId(3);
    userThree.setName("Aldo Borrero");

    CacheEntry cacheEntryUserThree = CacheEntry.create("user.key.three", User.class, userThree);
    cache.persist(cacheEntryUserThree);
    cache.delete("user.key.three");

    CacheEntry emptyUserThreeEntry = cache.obtain("user.key.three");
    Log.d(TAG, "Cache is emptied for User three: " + String.valueOf(emptyUserThreeEntry == null));
  }
}
