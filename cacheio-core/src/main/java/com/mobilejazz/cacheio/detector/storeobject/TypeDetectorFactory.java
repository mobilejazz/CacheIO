package com.mobilejazz.cacheio.detector.storeobject;

import java.util.List;

public class TypeDetectorFactory {

  public static <T> TypeValueStrategy obtain(T value) {
    if (value instanceof List) {
      return new ListTypeValueStrategy();
    } else if (value instanceof Object) {
      return new ObjectTypeValueStrategy();
    } else {
      return new TypeValueStrategy.NullTypeValueStrategy();
    }
  }
}
