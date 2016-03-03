package com.mobilejazz.cacheio.alternative.mappers.defaults;

import com.mobilejazz.cacheio.alternative.mappers.KeyMapper;

import static com.mobilejazz.cacheio.internal.helper.Preconditions.checkArgument;

public class LongKeyMapper implements KeyMapper<Long> {

  @Override public String toString(Long key) {
    checkArgument(key, "key cannot be null");
    return key.toString();
  }

  @Override public Long fromString(String str) {
    checkArgument(str, "str cannot be null");
    return Long.parseLong(str);
  }

}
