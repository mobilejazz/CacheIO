package com.mobilejazz.cacheio.alternative.mappers.defaults;

import com.mobilejazz.cacheio.alternative.mappers.KeyMapper;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class LongKeyMapperTest {

  private final KeyMapper<Long> mapper = new LongKeyMapper();

  @Test(expected = IllegalArgumentException.class) @SuppressWarnings("ResultOfMethodCallIgnored")
  public void shouldThrowIllegalArgumentExceptionIfMappingToStringTheValueIsNull() {
    mapper.toString(null);
  }

  @Test(expected = IllegalArgumentException.class) @SuppressWarnings("ResultOfMethodCallIgnored")
  public void shouldThrowIllegalArgumentExceptionIfMappingFromStringTheValueIsNull() {
    mapper.fromString(null);
  }

  @Test public void shouldMappingToStringAValue() throws Exception {
    String result = mapper.toString(1L);

    assertThat(result).isEqualTo("1");
  }

  @Test public void shouldMappingFromStringAValue() throws Exception {
    Long result = mapper.fromString("1");

    assertThat(result).isEqualTo(1L);
  }
}
