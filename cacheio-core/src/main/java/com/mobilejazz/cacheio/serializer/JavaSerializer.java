/*
 * Copyright (C) 2015 Mobile Jazz
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

package com.mobilejazz.cacheio.serializer;

import com.mobilejazz.cacheio.exceptions.SerializerException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

public class JavaSerializer implements Serializer {

  @Override public <T> byte[] toBytes(T value) throws SerializerException {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    try {
      ObjectOutput out = new ObjectOutputStream(bos);
      out.writeObject(value);
      out.close();
      return bos.toByteArray();
    } catch (IOException e) {
      throw new SerializerException(e);
    }
  }

  @SuppressWarnings("unchecked") @Override public <T> T fromBytes(byte[] value, Class<T> type)
      throws SerializerException {
    ByteArrayInputStream is = new ByteArrayInputStream(value);
    try {
      ObjectInput oi = new ObjectInputStream(is);
      return (T) oi.readObject();
    } catch (IOException | ClassNotFoundException e) {
      throw new SerializerException(e);
    }
  }
}
