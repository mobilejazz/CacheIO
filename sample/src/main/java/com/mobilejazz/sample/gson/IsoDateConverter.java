package com.mobilejazz.sample.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class IsoDateConverter implements JsonSerializer<Date>, JsonDeserializer<Date> {
  public static final Type TYPE = new TypeToken<Date>() {
  }.getType();

  public static final DateFormat DATE_FORMAT =
      new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);

  static {
    DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
  }

  public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
    return new JsonPrimitive(DATE_FORMAT.format(src));
  }

  public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    if (!(json instanceof JsonPrimitive)) {
      throw new JsonParseException("The date should be a string value");
    }
    return deserializeToDate(json);
  }

  private Date deserializeToDate(JsonElement json) {
    try {
      return DATE_FORMAT.parse(json.getAsString());
    } catch (ParseException e) {
      throw new JsonSyntaxException(
          "This date does not implement ISO 8601 standard: " + json.getAsString(), e);
    }
  }
}
