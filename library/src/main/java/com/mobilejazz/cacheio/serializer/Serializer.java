package com.mobilejazz.cacheio.serializer;

import com.mobilejazz.cacheio.exceptions.SerializerException;

public interface Serializer {

  <T> byte[] toBytes(T value) throws SerializerException;

  <T> T fromBytes(byte[] value, Class<T> type) throws SerializerException;
}
