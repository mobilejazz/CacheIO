package com.mobilejazz.cacheio.mappers;

public interface KeyMapper<T> {

  String toString(T model);

  T fromString(String str);
}
