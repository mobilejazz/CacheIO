package com.mobilejazz.cacheio.exceptions;

public class CacheErrorException extends CacheIOException {
  public CacheErrorException(Exception e) {
    super(e);
  }
}
