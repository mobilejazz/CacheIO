package com.mobilejazz.cacheio.alternative.mappers;

public interface KeyMapper<T> {

  String toString(T key);

  T fromString(String str);
}
