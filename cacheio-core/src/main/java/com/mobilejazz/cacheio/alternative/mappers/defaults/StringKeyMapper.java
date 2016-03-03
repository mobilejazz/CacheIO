package com.mobilejazz.cacheio.alternative.mappers.defaults;

import com.mobilejazz.cacheio.alternative.mappers.KeyMapper;

import static com.mobilejazz.cacheio.internal.helper.Preconditions.checkArgument;

public class StringKeyMapper implements KeyMapper<String> {

  @Override public String toString(String key) {
    checkArgument(key, "key cannot be null");
    return key;
  }

  @Override public String fromString(String str) {
    checkArgument(str, "str cannot be null");
    return str;
  }

}
