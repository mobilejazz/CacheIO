package com.mobilejazz.cacheio.alternative.mappers;

public interface VersionMapper<T> {

  long getVersion(T model);
}
