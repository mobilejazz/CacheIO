package com.mobilejazz.sample.gson;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonFactory {

  private GsonFactory() {
    throw new AssertionError("No instances of this class are allowed");
  }

  public static Gson create() {
    return new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
        .setPrettyPrinting()
        .registerTypeAdapter(IsoDateConverter.TYPE, new IsoDateConverter())
        .create();
  }
}
