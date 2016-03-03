package com.mobilejazz.cacheio.alternative.mappers.defaults;

import com.mobilejazz.cacheio.alternative.mappers.KeyMapper;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class StringKeyMapperTest {

  private final KeyMapper<String> mapper = new StringKeyMapper();

  @Test(expected = IllegalArgumentException.class) @SuppressWarnings("ResultOfMethodCallIgnored")
  public void shouldThrowIllegalArgumentExceptionIfMappingToStringTheValueIsNull() {
    mapper.toString(null);
  }

  @Test(expected = IllegalArgumentException.class) @SuppressWarnings("ResultOfMethodCallIgnored")
  public void shouldThrowIllegalArgumentExceptionIfMappingFromStringTheValueIsNull() {
    mapper.fromString(null);
  }

  @Test public void shouldMappingToStringAValue() throws Exception {
    String result = mapper.toString("1");

    assertThat(result).isEqualTo("1");
  }

  @Test public void shouldMappingFromStringAValue() throws Exception {
    String result = mapper.fromString("1");

    assertThat(result).isEqualTo("1");
  }
}
