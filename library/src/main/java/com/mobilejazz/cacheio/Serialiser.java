package com.mobilejazz.cacheio;

public interface Serialiser<T> {

  byte[] toBytes(T value);

  T fromBytes(byte[] value, Class<T> type);
}
