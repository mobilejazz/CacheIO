package com.mobilejazz.cacheio.mappers.key;

import com.mobilejazz.cacheio.mappers.KeyMapper;

import static com.mobilejazz.cacheio.helper.Preconditions.checkArgument;

public class IntegerKeyMapper implements KeyMapper<Integer> {

  @Override public String toString(Integer model) {
    checkArgument(model, "key cannot be null");
    return model.toString();
  }

  @Override public Integer fromString(String str) {
    checkArgument(str, "str cannot be null");
    return Integer.parseInt(str);
  }

}
