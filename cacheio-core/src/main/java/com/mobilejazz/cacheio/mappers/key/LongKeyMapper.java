package com.mobilejazz.cacheio.mappers.key;

import com.mobilejazz.cacheio.mappers.KeyMapper;

import static com.mobilejazz.cacheio.helper.Preconditions.checkArgument;

public class LongKeyMapper implements KeyMapper<Long> {

  @Override public String toString(Long model) {
    checkArgument(model, "key cannot be null");
    return model.toString();
  }

  @Override public Long fromString(String str) {
    checkArgument(str, "str cannot be null");
    return Long.parseLong(str);
  }

}
