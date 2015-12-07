package com.mobilejazz.cacheio.serializers.gson;

import com.google.gson.Gson;
import com.mobilejazz.cacheio.BuildConfig;
import com.mobilejazz.cacheio.serializer.Serializer;
import com.mobilejazz.cacheio.serializers.gson.model.UserTestModel;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21, manifest = Config.NONE)
public class GsonSerializerTest {

  private Serializer serializer;

  @Before public void setUp() throws Exception {
    Gson gson = new Gson();
    serializer = new GsonSerializer(gson);
  }

  @Test public void shouldSerialiseAObject() throws Exception {
    UserTestModel userTestModel = fakeUserTestObject();

    byte[] bytes = serializer.toBytes(userTestModel);

    Assertions.assertThat(bytes).isNotNull();
    Assertions.assertThat(bytes.length).isGreaterThan(0);
  }

  @Test public void shouldDeserializeAObject() throws Exception {
    UserTestModel userTestModelToDeserialize = fakeUserTestObject();
    byte[] userDeserialized = fakeUserTestBytes(userTestModelToDeserialize);

    UserTestModel userTestModelExpected =
        serializer.fromBytes(userDeserialized, UserTestModel.class);

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
    return serializer.toBytes(userTestModel);
  }
}
