package com.mobilejazz.cacheio.exceptions;

public class CacheIOException extends RuntimeException {

  public CacheIOException() {
  }

  public CacheIOException(String detailMessage) {
    super(detailMessage);
  }

  public CacheIOException(String detailMessage, Throwable throwable) {
    super(detailMessage, throwable);
  }

  public CacheIOException(Throwable throwable) {
    super(throwable);
  }
}
