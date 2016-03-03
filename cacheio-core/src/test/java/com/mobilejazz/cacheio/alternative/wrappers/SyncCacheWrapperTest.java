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
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import rx.Single;
import rx.SingleSubscriber;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings("unchecked")
@RunWith(MockitoJUnitRunner.class)
public class SyncCacheWrapperTest {

    @Mock
    private FutureCache<String, String> delegate;

    private SyncCacheWrapper<String, String> wrapper;

    @Before
    public void beforeTest(){
        wrapper = SyncCacheWrapper.newBuilder(String.class, String.class)
                .setDelegate(delegate)
                .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullKeyType(){
        SyncCacheWrapper.newBuilder(null, String.class)
                .setDelegate((FutureCache<Object, String>) mock(FutureCache.class))
                .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullValueType(){
        SyncCacheWrapper.newBuilder(String.class, null)
                .setDelegate((FutureCache<String, Object>) mock(FutureCache.class))
                .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullDelegate(){
        SyncCacheWrapper.newBuilder(String.class, String.class)
                .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullDelegate2(){
        SyncCacheWrapper.newBuilder(String.class, String.class)
                .setDelegate(null)
                .build();
    }

    @Test
    public void testGet() throws ExecutionException, InterruptedException {

        when(delegate.get("foo")).thenReturn(immediate("bar"));

        final String result = wrapper.get("foo");

        assertThat(result).isEqualTo("bar");
    }

    @Test
    public void testGetAll() throws ExecutionException, InterruptedException {

        final Map<String, String> map = new HashMap<>();
        map.put("hello", "world");
        map.put("foo", "bar");
        map.put("mal", "reynolds");

        when(delegate.getAll(map.keySet())).thenReturn(immediate(map));

        final Map<String, String> result = wrapper.getAll(map.keySet());

        assertThat(result).isEqualTo(map);
    }

    @Test
    public void testPut() throws ExecutionException, InterruptedException {

        when(delegate.put("foo", "bar", 10, TimeUnit.SECONDS)).thenReturn(immediate("bar"));

        final String result = wrapper.put("foo", "bar", 10, TimeUnit.SECONDS);

        assertThat(result).isEqualTo("bar");
    }

    @Test
    public void testPutAll() throws ExecutionException, InterruptedException {

        final Map<String, String> map = new HashMap<>();
        map.put("hello", "world");
        map.put("foo", "bar");
        map.put("mal", "reynolds");

        when(delegate.putAll(map, 10, TimeUnit.MINUTES)).thenReturn(immediate(map));

        final Map<String, String> result = wrapper.putAll(map, 10, TimeUnit.MINUTES);

        assertThat(result).isEqualTo(map);
    }

    @Test
    public void testRemove() throws ExecutionException, InterruptedException {

        when(delegate.remove("foo")).thenReturn(immediate("bar"));

        final String result = wrapper.remove("foo");

        assertThat(result).isEqualTo("bar");
    }

    @Test
    public void testRemoveAll() throws ExecutionException, InterruptedException {

        final Collection<String> keys = new ArrayList<>();
        keys.add("foo");
        keys.add("bar");
        keys.add("baz");

        when(delegate.removeAll(keys)).thenReturn(immediate(keys));

        final Collection<String> result = wrapper.removeAll(keys);

        assertThat(result).isEqualTo(keys);
    }

    /**
     * Utility method for creating an immediate future
     * @param value The value to be returned
     * @param <T>   The type of the value to be returned
     * @return      A future with it's result set immediately to the supplied value
     */
    private static <T> Future<T> immediate(final T value){
        return Single.create(new Single.OnSubscribe<T>() {
            @Override
            public void call(SingleSubscriber<? super T> singleSubscriber) {
                singleSubscriber.onSuccess(value);
            }
        }).toObservable().toBlocking().toFuture();
    }
}
