package com.mobilejazz.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;


import com.mobilejazz.cacheio.alternative.Cache;
import com.mobilejazz.cacheio.alternative.SQLiteCache;
import com.mobilejazz.cacheio.serializers.gson.GsonMapper;
import com.mobilejazz.sample.gson.GsonFactory;
import com.mobilejazz.sample.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import rx.Single;
import rx.SingleSubscriber;
import rx.schedulers.Schedulers;

public class InitialActivity extends AppCompatActivity {

    private static final String TAG = InitialActivity.class.getSimpleName();
    public static final String USER_KEY_LIST = "user.key.list";
    public static final String USER_KEY_ONE = "user.key.one";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial);


        final Cache<Integer, User> cache = SQLiteCache.newBuilder(Integer.class, User.class)
                .setContext(getApplicationContext())
                .setDatabaseName("CacheDatabase")
                .setTableName("Users")
                .setValueMapper(new GsonMapper(GsonFactory.create()))
                .setScheduler(Schedulers.io())
                .build();

        final User userOne = new User();
        userOne.setId(1);
        userOne.setName("Jose Luis Franconetti");

        final User userTwo = new User();
        userTwo.setId(2);
        userTwo.setName("Test user");

        final Map<Integer, User> entries = new HashMap<>();
        entries.put(userOne.getId(), userOne);
        entries.put(userTwo.getId(), userTwo);


        try {

            // Note the put isn't carried out until you call subscribe on the Single<T> object which is returned
            // This is due to the nature of the Observable contract
            final Single<Map<Integer, User>> putOp = cache.putAll(entries, 1, TimeUnit.SECONDS);

            final CountDownLatch putLatch = new CountDownLatch(1);

            putOp.subscribe(new SingleSubscriber<Map<Integer, User>>() {
                @Override
                public void onSuccess(Map<Integer, User> entries) {
                    Log.d(TAG, "Successfully inserted user entries");
                    putLatch.countDown();
                }

                @Override
                public void onError(Throwable error) {
                    Log.e(TAG, "Failed to insert user entries", error);
                }
            });


            putLatch.await();

            final CountDownLatch getLatch = new CountDownLatch(1);

            final List<Integer> keys = new ArrayList<>();
            keys.add(1);
            keys.add(2);

            final Single<Map<Integer, User>> getOp = cache.getAll(keys);

            getOp.subscribe(new SingleSubscriber<Map<Integer, User>>() {
                @Override
                public void onSuccess(Map<Integer, User> values) {
                    Log.d(TAG, "Retrieved entries");
                    Log.d(TAG, "User 1 matches = " + userOne.equals(values.get(1)));
                    Log.d(TAG, "User 2 matches = " + userTwo.equals(values.get(2)));
                    getLatch.countDown();
                }

                @Override
                public void onError(Throwable error) {
                    Log.e(TAG, "Failed to get user entries", error);
                }
            });

            getLatch.await();

            // wait 5 seconds and allow the entries to expire
            Thread.sleep(5000);

            final Single<Map<Integer, User>> expiredGetOp = cache.getAll(keys);

            expiredGetOp.subscribe(new SingleSubscriber<Map<Integer, User>>() {
                @Override
                public void onSuccess(Map<Integer, User> values) {
                    Log.d(TAG, "Entries have expired = " + values.isEmpty());
                }

                @Override
                public void onError(Throwable error) {
                    Log.e(TAG, "Failed to get user entries", error);
                }
            });

        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }
}
