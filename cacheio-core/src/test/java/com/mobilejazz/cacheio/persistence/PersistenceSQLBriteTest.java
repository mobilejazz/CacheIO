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

package com.mobilejazz.cacheio.persistence;

import android.content.ContentValues;
import android.database.Cursor;
import com.mobilejazz.cacheio.ApplicationTestCase;
import com.mobilejazz.cacheio.exceptions.CacheErrorException;
import com.mobilejazz.cacheio.exceptions.CacheNotFoundException;
import com.mobilejazz.cacheio.manager.entity.StoreObject;
import com.mobilejazz.cacheio.manager.entity.StoreObjectBuilder;
import com.mobilejazz.cacheio.persistence.sqlbrite.PersistenceSQLBrite;
import com.squareup.sqlbrite.BriteDatabase;
import java.util.ArrayList;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@PrepareForTest({ BriteDatabase.class, StoreObject.class }) @RunWith(PowerMockRunner.class)
public class PersistenceSQLBriteTest extends ApplicationTestCase {

  public static final String FAKE_KEY = "fake.key";
  public static final byte[] FAKE_VALUE = new byte[] {};
  public static final String FAKE_TYPE = "fake.type";
  public static final long FAKE_EXPIRY_MILLIS = 1000;
  public static final String FAKE_INDEX = "fake.index";
  public static final String FAKE_METATYPE = "fake.metatype";
  public static final long FAKE_TIMESTAMP = 10000;
  public static final String FAKE_QUERY = "fake.query";

  private Persistence persistence;

  BriteDatabase briteDatabase;

  @Before public void setUp() throws Exception {
    briteDatabase = PowerMockito.mock(BriteDatabase.class);

    persistence = new PersistenceSQLBrite(briteDatabase);
  }

  @Test public void shouldReturnOneStoreObject() throws Exception {
    // Given
    Cursor cursor = mock(Cursor.class);
    when(cursor.getString(anyInt())).thenReturn(FAKE_KEY, FAKE_TYPE, FAKE_METATYPE, FAKE_INDEX);
    when(cursor.getBlob(anyInt())).thenReturn(FAKE_VALUE);
    when(cursor.getLong(anyInt())).thenReturn(FAKE_EXPIRY_MILLIS, FAKE_TIMESTAMP);
    when(cursor.getCount()).thenReturn(1);
    when(cursor.moveToNext()).thenReturn(true, false);

    // When
    when(briteDatabase.query(anyString(), anyString())).thenReturn(cursor);

    List<StoreObject> storeObjects = persistence.obtain(FAKE_KEY);

    // Them
    Assertions.assertThat(storeObjects).hasSize(1);
  }

  @Test public void shouldReturnMoreThanOneStoreObject() throws Exception {
    // Given
    Cursor cursor = mock(Cursor.class);
    when(cursor.getString(anyInt())).thenReturn(FAKE_KEY, FAKE_TYPE, FAKE_METATYPE, FAKE_INDEX);
    when(cursor.getBlob(anyInt())).thenReturn(FAKE_VALUE);
    when(cursor.getLong(anyInt())).thenReturn(FAKE_EXPIRY_MILLIS, FAKE_TIMESTAMP);
    when(cursor.getCount()).thenReturn(2);
    when(cursor.moveToNext()).thenReturn(true/*first interaction*/, true /*second interaction*/,
        false /*third interaction*/);

    // When
    when(briteDatabase.query(anyString(), anyString())).thenReturn(cursor);

    List<StoreObject> storeObjects = persistence.obtain(FAKE_KEY);

    // Them
    Assertions.assertThat(storeObjects.size()).isGreaterThan(1);
  }

  @Test(expected = CacheNotFoundException.class) public void shouldThrowCacheNotFoundException()
      throws Exception {
    // Given
    Cursor cursor = mock(Cursor.class);
    when(cursor.getString(anyInt())).thenReturn(FAKE_KEY, FAKE_TYPE, FAKE_METATYPE, FAKE_INDEX);
    when(cursor.getBlob(anyInt())).thenReturn(FAKE_VALUE);
    when(cursor.getLong(anyInt())).thenReturn(FAKE_EXPIRY_MILLIS, FAKE_TIMESTAMP);
    when(cursor.getCount()).thenReturn(0);
    when(cursor.moveToNext()).thenReturn(false);

    // When
    when(briteDatabase.query(anyString(), anyString())).thenReturn(cursor);

    persistence.obtain(FAKE_KEY);
  }

  @Test public void shouldReturnAStoreObjectProperlyMapped() throws Exception {
    // Given
    Cursor cursor = mock(Cursor.class);
    when(cursor.getString(anyInt())).thenReturn(FAKE_KEY, FAKE_TYPE, FAKE_METATYPE, FAKE_INDEX);
    when(cursor.getBlob(anyInt())).thenReturn(FAKE_VALUE);
    when(cursor.getLong(anyInt())).thenReturn(FAKE_EXPIRY_MILLIS, FAKE_TIMESTAMP);
    when(cursor.getCount()).thenReturn(1);
    when(cursor.moveToNext()).thenReturn(true/*first interaction*/, false /*second interaction*/);

    // When
    when(briteDatabase.query(anyString(), anyString())).thenReturn(cursor);

    List<StoreObject> storeObjects = persistence.obtain(FAKE_KEY);
    StoreObject storeObjectExpected = storeObjects.get(0);

    // Them
    Assertions.assertThat(storeObjectExpected.getKey()).isEqualTo(FAKE_KEY);
    Assertions.assertThat(storeObjectExpected.getExpiredMillis()).isEqualTo(FAKE_EXPIRY_MILLIS);
    Assertions.assertThat(storeObjectExpected.getValue()).isEqualTo(FAKE_VALUE);
    Assertions.assertThat(storeObjectExpected.getType()).isEqualTo(FAKE_TYPE);
    Assertions.assertThat(storeObjectExpected.getMetaType()).isEqualTo(FAKE_METATYPE);
    Assertions.assertThat(storeObjectExpected.getTimestamp()).isEqualTo(FAKE_TIMESTAMP);
    Assertions.assertThat(storeObjectExpected.getIndex()).isEqualTo(FAKE_INDEX);
  }

  @Test public void shouldNotDeleteAObjectByKey() throws Exception {
    // When
    when(briteDatabase.delete(anyString(), anyString(), anyString())).thenReturn(0);

    boolean result = persistence.delete(FAKE_KEY);

    // Then
    Assertions.assertThat(result).isFalse();
  }

  @Test public void shouldDeleteAObjectByKey() throws Exception {
    // When
    when(briteDatabase.delete(anyString(), anyString(), anyString())).thenReturn(1);

    boolean result = persistence.delete(FAKE_KEY);

    // Then
    Assertions.assertThat(result).isTrue();
  }

  @Test(expected = CacheErrorException.class) public void shouldThrowACacheErrorException()
      throws Exception {
    // When
    when(briteDatabase.delete(anyString(), anyString(), anyString())).thenThrow(Exception.class);

    persistence.delete(FAKE_KEY);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowAExceptionWhenPersistAndStoreObjectsAreNull() throws Exception {
    // When
    persistence.persist(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowAExceptionWhenPersistAndListStoreObjectsSizeIsZero() throws Exception {
    // Given
    List<StoreObject> storeObjects = new ArrayList<>();

    // When
    persistence.persist(storeObjects);
  }

  @Test public void shouldPersistASingleStoreObject() throws Exception {
    // Given
    List<StoreObject> storeObjects = new ArrayList<>();
    StoreObject storeObject = provideAFakeStoreObject();
    storeObjects.add(storeObject);

    // Cursor mock
    Cursor cursor = mock(Cursor.class);
    when(cursor.moveToNext()).thenReturn(false);

    // ContentValues Mock
    ContentValues contentValues = PowerMockito.mock(ContentValues.class);

    PowerMockito.mockStatic(StoreObject.class);
    PowerMockito.when(StoreObject.toContentValues(storeObject)).thenReturn(contentValues);

    // When
    BriteDatabase.Transaction transaction = mock(BriteDatabase.Transaction.class);
    when(briteDatabase.newTransaction()).thenReturn(transaction);
    doNothing().when(transaction).markSuccessful();
    doNothing().when(transaction).end();
    when(briteDatabase.query(anyString(), anyString())).thenReturn(cursor);
    when(briteDatabase.insert(anyString(), any(ContentValues.class))).thenReturn((long) 23);

    boolean result = persistence.persist(storeObjects);

    Assertions.assertThat(result).isTrue();
  }

  @Test public void shouldNotPersistASingleObject() throws Exception {
    // Given
    List<StoreObject> storeObjects = new ArrayList<>();
    StoreObject storeObject = provideAFakeStoreObject();
    storeObjects.add(storeObject);

    // Cursor mock
    Cursor cursor = mock(Cursor.class);
    when(cursor.moveToNext()).thenReturn(false);

    // ContentValues Mock
    ContentValues contentValues = PowerMockito.mock(ContentValues.class);

    PowerMockito.mockStatic(StoreObject.class);
    PowerMockito.when(StoreObject.toContentValues(storeObject)).thenReturn(contentValues);

    // When
    BriteDatabase.Transaction transaction = mock(BriteDatabase.Transaction.class);
    when(briteDatabase.newTransaction()).thenReturn(transaction);
    doNothing().when(transaction).markSuccessful();
    doNothing().when(transaction).end();
    when(briteDatabase.query(anyString(), anyString())).thenReturn(cursor);
    when(briteDatabase.insert(anyString(), any(ContentValues.class))).thenReturn((long) -1);

    boolean result = persistence.persist(storeObjects);

    Assertions.assertThat(result).isFalse();
  }

  @Test public void shouldPersistAListOfStoreObjects() throws Exception {
    // Given
    List<StoreObject> storeObjects = new ArrayList<>();
    StoreObject storeObject = provideAFakeStoreObject();
    storeObjects.add(storeObject);
    storeObjects.add(storeObject);

    // Cursor mock
    Cursor cursor = mock(Cursor.class);
    when(cursor.moveToNext()).thenReturn(true/*first interaction*/, false /*second interaction*/);

    // ContentValues Mock
    ContentValues contentValues = PowerMockito.mock(ContentValues.class);

    PowerMockito.mockStatic(StoreObject.class);
    PowerMockito.when(StoreObject.toContentValues(storeObject)).thenReturn(contentValues);

    // When
    BriteDatabase.Transaction transaction = mock(BriteDatabase.Transaction.class);
    when(briteDatabase.newTransaction()).thenReturn(transaction);
    doNothing().when(transaction).markSuccessful();
    doNothing().when(transaction).end();
    when(briteDatabase.query(anyString(), anyString())).thenReturn(cursor);
    when(briteDatabase.insert(anyString(), any(ContentValues.class))).thenReturn((long) 23,
        (long) 24);

    boolean result = persistence.persist(storeObjects);

    Assertions.assertThat(result).isTrue();
  }
}
