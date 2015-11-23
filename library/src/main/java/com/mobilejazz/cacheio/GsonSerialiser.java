package com.mobilejazz.cacheio;

import com.google.gson.Gson;

public class GsonSerialiser<T> implements Serialiser<T> {

  private Gson gson;

  public GsonSerialiser(Gson gson) {
    this.gson = gson;
  }

  @Override public byte[] toBytes(T value) {
    return gson.toJson(value).getBytes();
  }

  @Override public T fromBytes(byte[] value, Class type) {
    return (T) gson.fromJson(new String(value), type);
  }
}
