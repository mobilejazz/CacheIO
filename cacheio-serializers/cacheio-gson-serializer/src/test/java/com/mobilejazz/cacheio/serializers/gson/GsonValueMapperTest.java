package com.mobilejazz.cacheio.serializers.gson;

import com.google.gson.Gson;
import com.mobilejazz.cacheio.BuildConfig;
import com.mobilejazz.cacheio.mappers.ValueMapper;
import com.mobilejazz.cacheio.serializers.gson.model.UserTestModel;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.*;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21, manifest = Config.NONE)
public class GsonValueMapperTest {

  private ValueMapper valueMapper;

  @Before public void setUp() throws Exception {
    Gson gson = new Gson();
    valueMapper = new GsonValueMapper(gson);
  }

  @Test public void shouldWriteTheBytesOfObject() throws Exception {
    UserTestModel userTestModel = fakeUserTestObject();

    final ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
    valueMapper.write(userTestModel, bytesOut);

    Assertions.assertThat(bytesOut).isNotNull();
    Assertions.assertThat(bytesOut.size()).isGreaterThan(0);
  }

  @Test public void shouldDeserializeAObject() throws Exception {
    UserTestModel userTestModelToDeserialize = fakeUserTestObject();
    byte[] userDeserialized = fakeUserTestBytes(userTestModelToDeserialize);
    final ByteArrayInputStream bytesIn = new ByteArrayInputStream(userDeserialized);

    final UserTestModel value = valueMapper.read(UserTestModel.class, bytesIn);

    Assertions.assertThat(value).isNotNull();
    Assertions.assertThat(value.getId()).isEqualTo(userTestModelToDeserialize.getId());
    Assertions.assertThat(value.getName()).isEqualTo(userTestModelToDeserialize.getName());
  }

  private UserTestModel fakeUserTestObject() {
    UserTestModel userTestModel = new UserTestModel();
    userTestModel.setId(1);
    userTestModel.setName("Test name");

    return userTestModel;
  }

  private byte[] fakeUserTestBytes(UserTestModel userTestModel) {
    final ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
    valueMapper.write(userTestModel, bytesOut);
    return bytesOut.toByteArray();
  }
}
