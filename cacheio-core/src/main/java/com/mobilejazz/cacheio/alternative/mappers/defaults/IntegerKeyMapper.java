package com.mobilejazz.cacheio.alternative.mappers.defaults;

import com.mobilejazz.cacheio.alternative.mappers.KeyMapper;

import static com.mobilejazz.cacheio.internal.helper.Preconditions.checkArgument;

public class IntegerKeyMapper implements KeyMapper<Integer> {

  @Override public String toString(Integer key) {
    checkArgument(key, "key cannot be null");
    return key.toString();
  }

  @Override public Integer fromString(String str) {
    checkArgument(str, "str cannot be null");
    return Integer.parseInt(str);
  }

}
