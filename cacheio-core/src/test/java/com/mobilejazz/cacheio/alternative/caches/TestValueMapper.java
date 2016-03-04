/*
 * Copyright (C) 2016 Mobile Jazz
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.mobilejazz.cacheio.alternative.caches;

import com.mobilejazz.cacheio.alternative.mappers.ValueMapper;
import com.mobilejazz.cacheio.exceptions.SerializerException;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class TestValueMapper implements ValueMapper {

  private final Random random = new Random();
  private final Map<SerializedValue, Object> bytesToValue = new ConcurrentHashMap<>();

  @Override public void write(Object value, OutputStream out) throws SerializerException {

    final byte[] bytes = new byte[20];
    random.nextBytes(bytes);

    final SerializedValue serialized = new SerializedValue(bytes);
    bytesToValue.put(serialized, value);

    try {
      out.write(bytes);
    } catch (IOException e) {
      throw new SerializerException(e);
    }

  }

  @Override public <T> T read(Class<T> type, InputStream in) throws SerializerException {

    final byte[] bytes = new byte[20];

    try {
      in.read(bytes);

      final SerializedValue serialized = new SerializedValue(bytes);
      return type.cast(bytesToValue.get(serialized));

    } catch (IOException e) {
      throw new SerializerException(e);
    }
  }

  private static final class SerializedValue {

    private final byte[] bytes;

    public SerializedValue(byte[] bytes) {
      this.bytes = bytes;
    }

    @Override public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      SerializedValue that = (SerializedValue) o;

      return Arrays.equals(bytes, that.bytes);

    }

    @Override public int hashCode() {
      return bytes != null ? Arrays.hashCode(bytes) : 0;
    }
  }
}
