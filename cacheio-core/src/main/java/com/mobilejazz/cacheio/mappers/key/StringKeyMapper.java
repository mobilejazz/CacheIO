package com.mobilejazz.cacheio.mappers.key;

import com.mobilejazz.cacheio.mappers.KeyMapper;

import static com.mobilejazz.cacheio.helper.Preconditions.checkArgument;

public class StringKeyMapper implements KeyMapper<String> {

  @Override public String toString(String model) {
    checkArgument(model, "key cannot be null");
    return model;
  }

  @Override public String fromString(String str) {
    checkArgument(str, "str cannot be null");
    return str;
  }

}
