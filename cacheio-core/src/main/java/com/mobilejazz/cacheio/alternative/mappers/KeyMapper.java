package com.mobilejazz.cacheio.alternative.mappers;

public interface KeyMapper<T> {

  String toString(T model);

  T fromString(String str);
}
