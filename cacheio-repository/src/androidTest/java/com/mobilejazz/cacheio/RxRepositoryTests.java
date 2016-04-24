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

package com.mobilejazz.cacheio;

import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import com.mobilejazz.cacheio.mappers.PaginatedQueryMapper;
import com.mobilejazz.cacheio.model.TestUser;
import com.mobilejazz.cacheio.query.PaginatedQuery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import rx.Single;

import java.util.*;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(AndroidJUnit4.class) public class RxRepositoryTests {

  private CacheIO cacheIO;
  public static final String FAKE_TEST_USER_ID = "fake.user.id";
  public static final String FAKE_TEST_USER_NAME = "fake.test.user.name";

  @Before public void setUp() throws Exception {
    this.cacheIO = CacheIO.with(InstrumentationRegistry.getContext())
        .identifier("test.database")
        .executor(Executors.newSingleThreadExecutor())
        .setKeyMapper(PaginatedQuery.class, new PaginatedQueryMapper())
        .build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowAExceptionWhenDummyObjectIsNull() throws Exception {
    RxRepository<String, TestUser, PaginatedQuery> repository = givenARxRepository();

    repository.put(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowAExceptionWhenQueryAndListAreNull() throws Exception {
    RxRepository<String, TestUser, PaginatedQuery> repository = givenARxRepository();

    repository.put(null, null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowAExceptionWhenPutAndQueryIsNull() throws Exception {
    RxRepository<String, TestUser, PaginatedQuery> repository = givenARxRepository();

    repository.put(null, Collections.<TestUser>emptyList());
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowAExceptionWhenPutAndListIsNull() throws Exception {
    RxRepository<String, TestUser, PaginatedQuery> repository = givenARxRepository();

    PaginatedQuery paginatedQuery = givenAFakePaginatedQuery("test", 0, 5);

    repository.put(paginatedQuery, null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowAExceptionWhenFindAndQueryIsNull() throws Exception {
    RxRepository<String, TestUser, PaginatedQuery> repository = givenARxRepository();

    repository.find(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowAExceptionWhenFindByIdAndIdIsNull() throws Exception {
    RxRepository<String, TestUser, PaginatedQuery> repository = givenARxRepository();

    repository.findById(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowAExceptionWhenFindByIdAndIdIsEmpty() throws Exception {
    RxRepository<String, TestUser, PaginatedQuery> repository = givenARxRepository();

    repository.findById("");
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowAExceptionWhenRemoveByIdAndIdIsEmpty() throws Exception {
    RxRepository<String, TestUser, PaginatedQuery> repository = givenARxRepository();

    repository.removeById("");
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowAExceptionWhenRemoveByIdAndIdIsNull() throws Exception {
    RxRepository<String, TestUser, PaginatedQuery> repository = givenARxRepository();

    repository.removeById(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowAExceptionWhenRemoveByQueryAndQueryIsNull() throws Exception {
    RxRepository<String, TestUser, PaginatedQuery> repository = givenARxRepository();

    repository.removeByQuery(null);
  }

  @Test public void shouldPutADummyObject() throws Exception {
    RxRepository<String, TestUser, PaginatedQuery> repository = givenARxRepository();

    TestUser testUserPut = repository.put(givenDummyTestUser()).toObservable().toBlocking().first();

    Single<TestUser> singleTestUser = repository.findById(FAKE_TEST_USER_ID);

    TestUser testUser = singleTestUser.toObservable().toBlocking().first();

    assertThat(testUser).isNotNull();
    assertThat(testUser.getId()).isEqualTo(FAKE_TEST_USER_ID);
    assertThat(testUser.getName()).isEqualTo(FAKE_TEST_USER_NAME);
  }

  @Test public void shouldPutAListOfDummyObjectsWithPaginatedQuery() throws Exception {
    RxRepository<String, TestUser, PaginatedQuery> repository = givenARxRepository();

    TestUser testUserOne = givenDummyTestUser("one", "dummy_one");
    TestUser testUserTwo = givenDummyTestUser("two", "dummy_two");

    List<TestUser> testUsersToPut = new ArrayList<>(2);
    testUsersToPut.add(testUserOne);
    testUsersToPut.add(testUserTwo);

    PaginatedQuery paginatedQuery = new PaginatedQuery("test_user_paginated", 0, 10);
    List<TestUser> testUsersPut =
        repository.put(paginatedQuery, testUsersToPut).toObservable().toBlocking().first();

    List<TestUser> testUsersExpected =
        repository.find(paginatedQuery).toObservable().toBlocking().first();

    assertThat(testUsersExpected).isNotNull();
    assertThat(testUsersExpected.size()).isEqualTo(testUsersToPut.size());

    assertThat(testUsersExpected.get(0)).isNotNull();
    assertThat(testUsersExpected.get(0).getId()).isEqualTo(testUserOne.getId());
    assertThat(testUsersExpected.get(0).getName()).isEqualTo(testUserOne.getName());

    assertThat(testUsersExpected.get(1)).isNotNull();
    assertThat(testUsersExpected.get(1).getId()).isEqualTo(testUserTwo.getId());
    assertThat(testUsersExpected.get(1).getName()).isEqualTo(testUserTwo.getName());
  }

  @Test public void shouldReturnEmptyListWhenThereIsNoDummyObjectsWithPaginatedQuery()
      throws Exception {
    RxRepository<String, TestUser, PaginatedQuery> repository = givenARxRepository();

    List<TestUser> testUsers = repository.find(givenAFakePaginatedQuery("fake.id", 0, 5))
        .toObservable()
        .toBlocking()
        .first();

    assertThat(testUsers).isEmpty();
  }

  @Test public void shouldRemoveADummyObject() throws Exception {
    RxRepository<String, TestUser, PaginatedQuery> repository = givenARxRepository();

    TestUser persisted = repository.put(givenDummyTestUser()).toObservable().toBlocking().first();

    String keyRemoved =
        repository.removeById(FAKE_TEST_USER_ID).toObservable().toBlocking().first();

    assertThat(keyRemoved).isNotNull();
    assertThat(keyRemoved).isEqualTo(FAKE_TEST_USER_ID);
  }

  @Test public void shouldRemoveAListOfDummyObjectsByAPaginatedQuery() throws Exception {
    RxRepository<String, TestUser, PaginatedQuery> repository = givenARxRepository();

    TestUser testUserOne = givenDummyTestUser("one", "dummy_one");
    TestUser testUserTwo = givenDummyTestUser("two", "dummy_two");

    List<TestUser> testUsersToPut = new ArrayList<>(2);
    testUsersToPut.add(testUserOne);
    testUsersToPut.add(testUserTwo);

    PaginatedQuery paginatedQuery = new PaginatedQuery("test_user_paginated", 0, 10);
    List<TestUser> testUsersPut =
        repository.put(paginatedQuery, testUsersToPut).toObservable().toBlocking().first();

    Collection<String> keysRemoved =
        repository.removeByQuery(paginatedQuery).toObservable().toBlocking().first();

    assertThat(keysRemoved).isNotNull();
  }

  private List<TestUser> givenListOfDummyTestUser() {
    int size = 3;
    List<TestUser> testUsers = new ArrayList<>(3);

    for (int i = 0; i < size; i++) {
      TestUser testUser = new TestUser(String.valueOf(i), "fake.name" + i);
      testUsers.add(testUser);
    }

    return testUsers;
  }

  private TestUser givenDummyTestUser(String id, String name) {
    return new TestUser(id, name);
  }

  private TestUser givenDummyTestUser() {
    return givenDummyTestUser(FAKE_TEST_USER_ID, FAKE_TEST_USER_NAME);
  }

  private RxRepository<String, TestUser, PaginatedQuery> givenARxRepository() {
    RxCache<String, TestUser> cache = cacheIO.newRxCache(String.class, TestUser.class);

    RxCache<PaginatedQuery, StringList> queryCache =
        cacheIO.newRxCache(PaginatedQuery.class, StringList.class);

    return new StringKeyedRxRepository.Builder<TestUser, PaginatedQuery>().setCache(cache)
        .setQueryCache(queryCache)
        .build();
  }

  @NonNull private PaginatedQuery givenAFakePaginatedQuery(String test, int offset, int limit) {
    return new PaginatedQuery(test, offset, limit);
  }

}
