package com.swarts.spring.reactive.testkit;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ObjectMapperUtils {

  public static <T> String toJsonString(T object) {
    try {
      return getObjectMapper().writeValueAsString(object);
    } catch (JsonProcessingException e) {
      return "JSON_PARSE_ERROR";
    }
  }

  public static <T> T toObject(String value, Class<T> returnType) {
    try {
      return getObjectMapper().readValue(value, returnType);
    } catch (JsonProcessingException e) {
      log.error("toObject failed: {}", e.getMessage());
      return null;
    }
  }

  public static <T> T toCollection(String value,  TypeReference<T> valueTypeRef) {
    try {
      return getObjectMapper().readValue(value, valueTypeRef);
    } catch (JsonProcessingException e) {
      log.error("toObject failed: {}", e.getMessage());
      return null;
    }
  }

  private static ObjectMapper getObjectMapper() {

    ObjectMapper objectMapper = new ObjectMapper();
    SimpleModule module = new SimpleModule();
    objectMapper.registerModule(module);
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    objectMapper.setSerializationInclusion(Include.NON_NULL);

    return objectMapper;
  }
}
