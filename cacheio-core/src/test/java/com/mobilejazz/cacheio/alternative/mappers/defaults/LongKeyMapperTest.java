package com.mobilejazz.cacheio.alternative.mappers.defaults;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class LongKeyMapperTest {

    private final LongKeyMapper mapper = new LongKeyMapper();

    @Test(expected = IllegalArgumentException.class)
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void testToStringWithNullInteger(){
        mapper.toString(null);
    }

    @Test(expected = IllegalArgumentException.class)
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void testFromStringWithNullString(){
        mapper.fromString(null);
    }

    @Test
    public void testMappingToString(){

        assertThat(mapper.toString(1L)).isEqualTo("1");
        assertThat(mapper.toString(2L)).isEqualTo("2");
        assertThat(mapper.toString(3L)).isEqualTo("3");
        assertThat(mapper.toString(4L)).isEqualTo("4");

        assertThat(mapper.toString(1L)).isNotEqualTo("4");
    }

    @Test
    public void testMappingFromString(){

        assertThat(mapper.fromString("1")).isEqualTo(1L);
        assertThat(mapper.fromString("2")).isEqualTo(2L);
        assertThat(mapper.fromString("3")).isEqualTo(3L);
        assertThat(mapper.fromString("4")).isEqualTo(4L);

        assertThat(mapper.fromString("1")).isNotEqualTo(4L);
    }
}
