package com.mobilejazz.cacheio;

public interface Serializer {

  <T> byte[] toBytes(T value);

  <T> T fromBytes(byte[] value, Class<T> type);
}
