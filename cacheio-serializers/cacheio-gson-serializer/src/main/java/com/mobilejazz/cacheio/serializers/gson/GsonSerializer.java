package com.mobilejazz.cacheio.serializers.gson;

import com.google.gson.Gson;
import com.mobilejazz.cacheio.serializer.Serializer;

public class GsonSerializer implements Serializer {

  private Gson gson;

  public GsonSerializer(Gson gson) {
    this.gson = gson;
  }

  @Override public <T> byte[] toBytes(T value) {
    return gson.toJson(value).getBytes();
  }

  @Override public <T> T fromBytes(byte[] value, Class<T> type) {
    return gson.fromJson(new String(value), type);
  }
}