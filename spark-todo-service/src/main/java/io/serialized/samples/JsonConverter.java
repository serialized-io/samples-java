package io.serialized.samples;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import spark.ResponseTransformer;

import java.io.IOException;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT;
import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;

public class JsonConverter implements ResponseTransformer {

  private static final ObjectMapper objectMapper = new ObjectMapper()
      .configure(FAIL_ON_UNKNOWN_PROPERTIES, false)
      .configure(WRITE_DATES_AS_TIMESTAMPS, false)
      .configure(INDENT_OUTPUT, true)
      .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
      .setSerializationInclusion(NON_NULL);

  @Override
  public String render(Object model) {
    if (model == null) {
      return "";
    } else {
      try {
        return objectMapper.writeValueAsString(model);
      } catch (JsonProcessingException e) {
        throw new RuntimeException(e);
      }
    }
  }

  public static <T> T fromJson(String body, Class<T> clazz) {
    try {
      return objectMapper.readValue(body, clazz);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

}
