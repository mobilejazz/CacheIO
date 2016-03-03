package com.mobilejazz.cacheio.alternative.wrappers;

import com.mobilejazz.cacheio.alternative.FutureCache;
import com.mobilejazz.cacheio.alternative.RxCache;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import rx.Single;
import rx.SingleSubscriber;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings("unchecked") @RunWith(MockitoJUnitRunner.class)
public class FutureCacheWrapperTest {

  @Mock private RxCache<String, String> delegate;

  private FutureCacheWrapper<String, String> wrapper;

  @Before public void beforeTest() {
    wrapper =
        FutureCacheWrapper.newBuilder(String.class, String.class).setDelegate(delegate).build();
  }

  @Test(expected = IllegalArgumentException.class) public void testNullKeyType() {
    FutureCacheWrapper.newBuilder(null, String.class)
        .setDelegate((RxCache<Object, String>) mock(RxCache.class))
        .build();
  }

  @Test(expected = IllegalArgumentException.class) public void testNullValueType() {
    FutureCacheWrapper.newBuilder(String.class, null)
        .setDelegate((RxCache<String, Object>) mock(RxCache.class))
        .build();
  }

  @Test(expected = IllegalArgumentException.class) public void testNullDelegate() {
    FutureCacheWrapper.newBuilder(String.class, String.class).build();
  }

  @Test(expected = IllegalArgumentException.class) public void testNullDelegate2() {
    FutureCacheWrapper.newBuilder(String.class, String.class).setDelegate(null).build();
  }

  @Test public void testGet() throws ExecutionException, InterruptedException {

    final Single<String> single = Single.create(new Single.OnSubscribe<String>() {
      @Override public void call(SingleSubscriber<? super String> singleSubscriber) {
        singleSubscriber.onSuccess("bar");
      }
    });

    when(delegate.get("foo")).thenReturn(single);

    final Future<String> foo = wrapper.get("foo");
    final String result = foo.get();

    assertThat(result).isEqualTo("bar");
  }

  @Test public void testGetAll() throws ExecutionException, InterruptedException {

    final Map<String, String> map = new HashMap<>();
    map.put("hello", "world");
    map.put("foo", "bar");
    map.put("mal", "reynolds");

    final Single<Map<String, String>> single =
        Single.create(new Single.OnSubscribe<Map<String, String>>() {
          @Override
          public void call(SingleSubscriber<? super Map<String, String>> singleSubscriber) {
            singleSubscriber.onSuccess(map);
          }
        });

    when(delegate.getAll(map.keySet())).thenReturn(single);

    final Future<Map<String, String>> foo = wrapper.getAll(map.keySet());
    final Map<String, String> result = foo.get();

    assertThat(result).isEqualTo(map);
  }

  @Test public void testPut() throws ExecutionException, InterruptedException {

    final Single<String> single = Single.create(new Single.OnSubscribe<String>() {
      @Override public void call(SingleSubscriber<? super String> singleSubscriber) {
        singleSubscriber.onSuccess("bar");
      }
    });

    when(delegate.put("foo", "bar", 10, TimeUnit.SECONDS)).thenReturn(single);

    final Future<String> foo = wrapper.put("foo", "bar", 10, TimeUnit.SECONDS);
    final String result = foo.get();

    assertThat(result).isEqualTo("bar");
  }

  @Test public void testPutAll() throws ExecutionException, InterruptedException {

    final Map<String, String> map = new HashMap<>();
    map.put("hello", "world");
    map.put("foo", "bar");
    map.put("mal", "reynolds");

    final Single<Map<String, String>> single =
        Single.create(new Single.OnSubscribe<Map<String, String>>() {
          @Override
          public void call(SingleSubscriber<? super Map<String, String>> singleSubscriber) {
            singleSubscriber.onSuccess(map);
          }
        });

    when(delegate.putAll(map, 10, TimeUnit.MINUTES)).thenReturn(single);

    final Future<Map<String, String>> foo = wrapper.putAll(map, 10, TimeUnit.MINUTES);
    final Map<String, String> result = foo.get();

    assertThat(result).isEqualTo(map);
  }

  @Test public void testRemove() throws ExecutionException, InterruptedException {

    final Single<String> single = Single.create(new Single.OnSubscribe<String>() {
      @Override public void call(SingleSubscriber<? super String> singleSubscriber) {
        singleSubscriber.onSuccess("bar");
      }
    });

    when(delegate.remove("foo")).thenReturn(single);

    final Future<String> foo = wrapper.remove("foo");
    final String result = foo.get();

    assertThat(result).isEqualTo("bar");
  }

  @Test public void testRemoveAll() throws ExecutionException, InterruptedException {

    final List<String> keys = new ArrayList<>();
    keys.add("foo");
    keys.add("bar");
    keys.add("baz");

    final Single<Collection<String>> single =
        Single.create(new Single.OnSubscribe<Collection<String>>() {
          @Override
          public void call(SingleSubscriber<? super Collection<String>> singleSubscriber) {
            singleSubscriber.onSuccess(keys);
          }
        });

    when(delegate.removeAll(keys)).thenReturn(single);

    final Future<Collection<String>> foo = wrapper.removeAll(keys);
    final Collection<String> result = foo.get();

    assertThat(result).isEqualTo(keys);
  }

}
