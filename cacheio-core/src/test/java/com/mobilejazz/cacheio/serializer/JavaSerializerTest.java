package com.mobilejazz.cacheio.serializer;

import com.mobilejazz.cacheio.ApplicationTestCase;
import com.mobilejazz.cacheio.exceptions.SerializerException;
import com.mobilejazz.cacheio.model.UserTestModelNotSerializable;
import com.mobilejazz.cacheio.model.UserTestModelSerializable;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

public class JavaSerializerTest extends ApplicationTestCase {

  private Serializer serializer;

  @Before public void setUp() throws Exception {
    serializer = new JavaSerializer();
  }

  @Test public void shouldSerialiseAObject() throws Exception {
    UserTestModelSerializable userTestModelSerializable = fakeUserTestObject();

    byte[] bytes = serializer.toBytes(userTestModelSerializable);

    Assertions.assertThat(bytes).isNotNull();
    Assertions.assertThat(bytes.length).isGreaterThan(0);
  }

  @Test public void shouldDeserializeAObject() throws Exception {
    UserTestModelSerializable userTestModelSerializableToDeserialize = fakeUserTestObject();
    byte[] userDeserialized = fakeUserTestBytes(userTestModelSerializableToDeserialize);

    UserTestModelSerializable userTestModelSerializableExpected =
        serializer.fromBytes(userDeserialized, UserTestModelSerializable.class);

    Assertions.assertThat(userTestModelSerializableExpected).isNotNull();
    Assertions.assertThat(userTestModelSerializableExpected.getId())
        .isEqualTo(userTestModelSerializableToDeserialize.getId());
    Assertions.assertThat(userTestModelSerializableExpected.getName())
        .isEqualTo(userTestModelSerializableToDeserialize.getName());
  }

  @Test(expected = SerializerException.class)
  public void shouldThrowSerializableExceptionWhenSerialize() throws Exception {
    UserTestModelNotSerializable userToSerialize = fakeUserTestObjectNotSerializable();

    serializer.toBytes(userToSerialize);
  }

  //region Private methods

  private UserTestModelNotSerializable fakeUserTestObjectNotSerializable() {
    UserTestModelNotSerializable userTestModelSerializable = new UserTestModelNotSerializable();
    userTestModelSerializable.setId(1);
    userTestModelSerializable.setName("Test name");

    return userTestModelSerializable;
  }

  private UserTestModelSerializable fakeUserTestObject() {
    UserTestModelSerializable userTestModelSerializable = new UserTestModelSerializable();
    userTestModelSerializable.setId(1);
    userTestModelSerializable.setName("Test name");

    return userTestModelSerializable;
  }

  private byte[] fakeUserTestBytes(UserTestModelSerializable userTestModelSerializable) {
    return serializer.toBytes(userTestModelSerializable);
  }

  //endregion
}