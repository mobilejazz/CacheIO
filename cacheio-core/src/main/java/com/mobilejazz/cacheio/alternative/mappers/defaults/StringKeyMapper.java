package com.mobilejazz.cacheio.alternative.mappers.defaults;

import com.mobilejazz.cacheio.alternative.mappers.KeyMapper;

import static com.mobilejazz.cacheio.internal.helper.Preconditions.checkArgument;

public class StringKeyMapper implements KeyMapper<String> {

  @Override public String toString(String key) {
    return checkArgument(key, "key cannot be null");
  }

  @Override public String fromString(String str) {
    return checkArgument(str, "str cannot be null");
  }

}
