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

import com.mobilejazz.cacheio.mappers.PaginatedQueryMapper;
import com.mobilejazz.cacheio.query.PaginatedQuery;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PaginatedQueryMapperTests {

  public static final String FAKE_PAGINATED_QUERY_ID = "fake.paginated.query.id";
  public static final int FAKE_OFFSET = 0;
  public static final int FAKE_LIMIT = 10;

  private PaginatedQueryMapper mapper;

  @Before public void setUp() throws Exception {
    mapper = new PaginatedQueryMapper();
  }

  @Test(expected = IllegalArgumentException.class) @SuppressWarnings("ResultOfMethodCallIgnored")
  public void shouldThrowIllegalArgumentExceptionIfMappingToStringWithNullValue() {
    mapper.toString(null);
  }

  @Test(expected = IllegalArgumentException.class) @SuppressWarnings("ResultOfMethodCallIgnored")
  public void shouldThrowIllegalArgumentExceptionIfMappingFromStringWithNullValue() {
    mapper.fromString(null);
  }

  @Test public void shouldMappingToPaginatedQueryToStringValue() throws Exception {
    String value = mapper.toString(givenAFakePaginatedQuery());

    assertThat(value).isNotNull();
    assertThat(value).isEqualTo(FAKE_PAGINATED_QUERY_ID + "_" + FAKE_OFFSET + "_" + FAKE_LIMIT);
  }

  @Test public void shouldMappingFromStringToPaginatedQuery() throws Exception {
    PaginatedQuery query =
        mapper.fromString(FAKE_PAGINATED_QUERY_ID + "_" + FAKE_OFFSET + "_" + FAKE_LIMIT);

    assertThat(query).isNotNull();
    assertThat(query.getId()).isEqualTo(FAKE_PAGINATED_QUERY_ID);
    assertThat(query.getOffset()).isEqualTo(FAKE_OFFSET);
    assertThat(query.getLimit()).isEqualTo(FAKE_LIMIT);
  }

  private PaginatedQuery givenAFakePaginatedQuery() {
    return new PaginatedQuery(FAKE_PAGINATED_QUERY_ID, FAKE_OFFSET, FAKE_LIMIT);
  }
}
