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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.mobilejazz.cacheio.manager.CacheOpenHelper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class CacheOpenHelperTest extends ApplicationTestCase {

  public static final String FAKE_NAME_DATABASE = "fake.name.database";

  @Mock Context context;
  @Mock SQLiteDatabase sqLiteDatabase;

  private CacheOpenHelper cacheOpenHelper;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);

    cacheOpenHelper = new CacheOpenHelper(context, FAKE_NAME_DATABASE);
  }

  @Test public void shouldDeleteDatabaseWhenTheDataBaseIsUpgraded() throws Exception {
    cacheOpenHelper.onUpgrade(sqLiteDatabase, 1, 2);

    verify(context).deleteDatabase(anyString());
  }

  @Test public void shouldNotDeleteTheDataBaseWhenTheDataBaseVersionIsTheSame() throws Exception {
    cacheOpenHelper.onUpgrade(sqLiteDatabase, 1, 1);

    verify(context, times(0)).deleteDatabase(anyString());
  }
}
