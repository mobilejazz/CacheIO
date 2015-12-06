package com.mobilejazz.cacheio.exceptions;

/** Indicate that a serialization was unable to complete successfully. */
public class SerializerException extends CacheIOException {
  public SerializerException() {
  }

  public SerializerException(String detailMessage) {
    super(detailMessage);
  }

  public SerializerException(String detailMessage, Throwable throwable) {
    super(detailMessage, throwable);
  }

  public SerializerException(Throwable throwable) {
    super(throwable);
  }
}
