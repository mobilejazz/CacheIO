package com.mobilejazz.cacheio.mappers;

public interface VersionMapper<T> {

  public static final Long UNVERSIONED = -1L;

  long getVersion(T model);
}
