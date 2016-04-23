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
package com.mobilejazz.cacheio.mappers.value.defaults;

import com.mobilejazz.cacheio.mappers.KeyMapper;
import com.mobilejazz.cacheio.mappers.key.FloatKeyMapper;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class FloatKeyMapperTest {

  private final KeyMapper<Float> mapper = new FloatKeyMapper();

  @Test(expected = IllegalArgumentException.class) @SuppressWarnings("ResultOfMethodCallIgnored")
  public void shouldThrowIllegalArgumentExceptionIfMappingToStringWithNullValue() {
    mapper.toString(null);
  }

  @Test(expected = IllegalArgumentException.class) @SuppressWarnings("ResultOfMethodCallIgnored")
  public void shouldThrowIllegalArgumentExceptionIfMappingFromStringWithNullValue() {
    mapper.fromString(null);
  }

  @Test public void shouldMappingToStringAValue() throws Exception {
    String result = mapper.toString(1f);

    assertThat(result).isEqualTo("1.0");
  }

  @Test public void shouldMappingFromStringAValue() throws Exception {
    Float result = mapper.fromString("1.0");

    assertThat(result).isEqualTo(1f);
  }
}
