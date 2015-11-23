package com.mobilejazz.cacheio.serializer;

import com.google.gson.Gson;
import com.mobilejazz.cacheio.ApplicationTestCase;
import com.mobilejazz.cacheio.GsonSerializer;
import com.mobilejazz.cacheio.Serializer;
import com.mobilejazz.cacheio.model.UserTestModel;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

public class GsonSerializerTest extends ApplicationTestCase {

  private Serializer serialiser;

  @Before public void setUp() throws Exception {
    Gson gson = new Gson();
    serialiser = new GsonSerializer(gson);
  }

  @Test public void shouldSerialiseAObject() throws Exception {
    UserTestModel userTestModel = fakeUserTestObject();

    byte[] bytes = serialiser.toBytes(userTestModel);

    Assertions.assertThat(bytes).isNotNull();
    Assertions.assertThat(bytes.length).isGreaterThan(0);
  }

  @Test public void shouldDeserializeAObject() throws Exception {
    UserTestModel userTestModelToDeserialize = fakeUserTestObject();
    byte[] userDeserialized = fakeUserTestBytes(userTestModelToDeserialize);

    UserTestModel userTestModelExpected =
        serialiser.fromBytes(userDeserialized, UserTestModel.class);

    Assertions.assertThat(userTestModelExpected).isNotNull();
    Assertions.assertThat(userTestModelExpected.getId())
        .isEqualTo(userTestModelToDeserialize.getId());
    Assertions.assertThat(userTestModelExpected.getName())
        .isEqualTo(userTestModelToDeserialize.getName());
  }

  private UserTestModel fakeUserTestObject() {
    UserTestModel userTestModel = new UserTestModel();
    userTestModel.setId(1);
    userTestModel.setName("Test name");

    return userTestModel;
  }

  private byte[] fakeUserTestBytes(UserTestModel userTestModel) {
    return serialiser.toBytes(userTestModel);
  }
}
