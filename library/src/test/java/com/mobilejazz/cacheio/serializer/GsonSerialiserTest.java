package com.mobilejazz.cacheio.serializer;

import com.google.gson.Gson;
import com.mobilejazz.cacheio.ApplicationTestCase;
import com.mobilejazz.cacheio.GsonSerialiser;
import com.mobilejazz.cacheio.Serialiser;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

public class GsonSerialiserTest extends ApplicationTestCase {

  private Serialiser serialiser;

  @Before public void setUp() throws Exception {
    Gson gson = new Gson();
    serialiser = new GsonSerialiser(gson);
  }

  @Test public void shouldPassTest() throws Exception {
    Assertions.assertThat(true).isTrue();
  }
}
