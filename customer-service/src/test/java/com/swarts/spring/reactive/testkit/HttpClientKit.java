package com.swarts.spring.reactive.testkit;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

@Slf4j
public class HttpClientKit {

  private static final OkHttpClient client = getOkHttpClient();

  private static OkHttpClient getOkHttpClient() {
    OkHttpClient client = new OkHttpClient.Builder()
        .callTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .build();
    return client;
  }

  public static <T> Response performPatch(String targetUrl, T requestBody)
      throws IOException {
    Request request = new Request.Builder()
        .patch(RequestBody.create(ObjectMapperUtils.toJsonString(requestBody),
            okhttp3.MediaType.parse("application/json")))
        .url(targetUrl)
        .build();

    return client.newCall(request).execute();
  }

  public static <T> Response performPut(String targetUrl, T requestBody)
      throws IOException {
    Request request = new Request.Builder()
        .put(RequestBody.create(ObjectMapperUtils.toJsonString(requestBody),
            okhttp3.MediaType.parse("application/json")))
        .url(targetUrl)
        .build();

    return client.newCall(request).execute();
  }

  public static <T> Response performPost(String targetUrl, T requestBody) throws IOException {
    Request request = new Request.Builder()
        .post(RequestBody.create(ObjectMapperUtils.toJsonString(requestBody),
            okhttp3.MediaType.parse("application/json")))
        .url(targetUrl)
        .build();

    return client.newCall(request).execute();
  }

  public static Response performGet(String targetUrl) throws IOException {
    Request request = new Request.Builder()
        .get()
        .url(targetUrl)
        .build();

    return client.newCall(request).execute();
  }

  public static Response performDelete(String targetUrl) throws IOException {
    Request request = new Request.Builder()
        .delete()
        .url(targetUrl)
        .build();

    return client.newCall(request).execute();
  }


  public static <T> T getResponseBody(Response response, Class<T> type) throws IOException {
    final String responseBodyString = getBodyAsString(response);

    return ObjectMapperUtils.toObject(
        responseBodyString,
        type
    );
  }

  public static <T> T getResponseBody(Response response, TypeReference<T> valueTypeRef) throws IOException {
    final String responseBodyString = getBodyAsString(response);

    return ObjectMapperUtils.toCollection(
        responseBodyString,
        valueTypeRef
    );
  }


  private static String getBodyAsString(Response response) throws IOException {
    ResponseBody responseBody = response.body();

    assertThat(responseBody, is(notNullValue()));

    final String responseBodyString = responseBody.string();

    log.info("ResponseBody: {}", responseBodyString);
    return responseBodyString;
  }
}
