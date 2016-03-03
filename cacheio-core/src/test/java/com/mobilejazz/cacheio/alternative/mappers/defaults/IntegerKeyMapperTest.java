package com.mobilejazz.cacheio.alternative.mappers.defaults;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class IntegerKeyMapperTest {

    private final IntegerKeyMapper mapper = new IntegerKeyMapper();

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

        assertThat(mapper.toString(1)).isEqualTo("1");
        assertThat(mapper.toString(2)).isEqualTo("2");
        assertThat(mapper.toString(3)).isEqualTo("3");
        assertThat(mapper.toString(4)).isEqualTo("4");

        assertThat(mapper.toString(1)).isNotEqualTo("4");
    }

    @Test
    public void testMappingFromString(){

        assertThat(mapper.fromString("1")).isEqualTo(1);
        assertThat(mapper.fromString("2")).isEqualTo(2);
        assertThat(mapper.fromString("3")).isEqualTo(3);
        assertThat(mapper.fromString("4")).isEqualTo(4);

        assertThat(mapper.fromString("1")).isNotEqualTo(4);
    }
}
