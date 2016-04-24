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

import com.mobilejazz.cacheio.mappers.DefaultQueryMapper;
import com.mobilejazz.cacheio.query.DefaultQuery;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DefaultQueryMapperTests {

  private DefaultQueryMapper mapper;
  public static final String FAKE_QUERY = "fake.query";

  @Before public void setUp() throws Exception {
    mapper = new DefaultQueryMapper();
  }

  @Test(expected = IllegalArgumentException.class) @SuppressWarnings("ResultOfMethodCallIgnored")
  public void shouldThrowIllegalArgumentExceptionIfMappingToStringWithNullValue() {
    mapper.toString(null);
  }

  @Test(expected = IllegalArgumentException.class) @SuppressWarnings("ResultOfMethodCallIgnored")
  public void shouldThrowIllegalArgumentExceptionIfMappingFromStringWithNullValue() {
    mapper.fromString(null);
  }

  @Test public void shouldMappingToDefaultQueryToStringValue() throws Exception {
    String value = mapper.toString(givenAFakeDefaultQuery());

    assertThat(value).isNotNull();
    assertThat(value).isEqualTo(FAKE_QUERY);
  }

  @Test public void shouldMappingFromStringToDefaultQuery() throws Exception {
    DefaultQuery query = mapper.fromString(FAKE_QUERY);

    assertThat(query).isNotNull();
    assertThat(query.getId()).isEqualTo(FAKE_QUERY);
  }

  private DefaultQuery givenAFakeDefaultQuery() {
    return new DefaultQuery(FAKE_QUERY);
  }
}
