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

import android.database.Cursor;
import com.mobilejazz.cacheio.manager.entity.StoreObject;
import com.mobilejazz.cacheio.persistence.Persistence;
import com.mobilejazz.cacheio.persistence.sqlbrite.PersistenceSQLBrite;
import com.squareup.sqlbrite.BriteDatabase;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@PrepareForTest(BriteDatabase.class) @RunWith(PowerMockRunner.class)
public class PersistenceSQLBriteTest extends ApplicationTestCase {

  public static final String FAKE_KEY = "fake.key";
  public static final byte[] FAKE_VALUE = new byte[] {};
  public static final String FAKE_TYPE = "fake.type";
  public static final long FAKE_EXPIRY_MILLIS = 1000;
  public static final String FAKE_INDEX = "fake.index";
  public static final String FAKE_METATYPE = "fake.metatype";
  public static final long FAKE_TIMESTAMP = 10000;

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

    // Then
    Assertions.assertThat(storeObjects).hasSize(1);
  }
}
