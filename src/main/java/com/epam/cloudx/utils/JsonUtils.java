package com.epam.cloudx.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.restassured.response.Response;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

@UtilityClass
public class JsonUtils {
  private static final ObjectMapper MAPPER = new ObjectMapper().registerModule(new JavaTimeModule());

  @SneakyThrows
  public static <T> T readJsonAsObject(Response response, Class<T> clazz) {
    return MAPPER.readValue(response.asString(), clazz);
  }
  @SneakyThrows
  public static <T> T readJsonFileAsObject(File file, Class<T> clazz) {
    return MAPPER.readValue(file, clazz);
  }


  @SneakyThrows
  public static <T> List<T> readJsonAsList(Response response, Class<T[]> clazz) {
    return Arrays.asList(MAPPER.readValue(response.asString(), clazz));
  }
}
