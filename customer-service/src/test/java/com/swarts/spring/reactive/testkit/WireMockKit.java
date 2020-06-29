package com.swarts.spring.reactive.testkit;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.patch;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

public class WireMockKit {

  public static <T> void setupGetStub(String requestUrl, int responseStatus, T responseBody) {

    stubFor(get(urlEqualTo(requestUrl))
        .withHeader(HttpHeaders.ACCEPT, containing(MediaType.APPLICATION_JSON_VALUE))
        .willReturn(aResponse()
            .withFixedDelay(0)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .withStatus(responseStatus)
            .withBody(ObjectMapperUtils.toJsonString(responseBody))
        )
    );
  }

  public static <T> void setupPostStub(String requestUrl, int responseStatus, T responseBody) {

    stubFor(post(urlEqualTo(requestUrl))
        .withHeader(HttpHeaders.CONTENT_TYPE, containing(MediaType.APPLICATION_JSON_VALUE))
        .withHeader(HttpHeaders.ACCEPT, containing(MediaType.APPLICATION_JSON_VALUE))
        .willReturn(aResponse()
            .withFixedDelay(0)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .withStatus(responseStatus)
            .withBody(ObjectMapperUtils.toJsonString(responseBody))
        )
    );
  }

  public static <T> void setPutStub(String requestUrl, int responseStatus, T responseBody) {

    stubFor(post(urlEqualTo(requestUrl))
        .withHeader(HttpHeaders.CONTENT_TYPE, containing(MediaType.APPLICATION_JSON_VALUE))
        .withHeader(HttpHeaders.ACCEPT, containing(MediaType.APPLICATION_JSON_VALUE))
        .willReturn(aResponse()
            .withFixedDelay(0)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .withStatus(responseStatus)
            .withBody(ObjectMapperUtils.toJsonString(responseBody))
        )
    );
  }

  public static <T> void setupPatchStub(String requestUrl, int responseStatus, T responseBody) {

    stubFor(patch(urlEqualTo(requestUrl))
        .withHeader(HttpHeaders.CONTENT_TYPE, containing(MediaType.APPLICATION_JSON_VALUE))
        .withHeader(HttpHeaders.ACCEPT, containing(MediaType.APPLICATION_JSON_VALUE))
        .willReturn(aResponse()
            .withFixedDelay(0)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .withStatus(responseStatus)
            .withBody(ObjectMapperUtils.toJsonString(responseBody))
        )
    );
  }

  public static void setupPatchStub(String requestUrl, int responseStatus) {

    stubFor(patch(urlEqualTo(requestUrl))
        .withHeader(HttpHeaders.CONTENT_TYPE, containing(MediaType.APPLICATION_JSON_VALUE))
        .withHeader(HttpHeaders.ACCEPT, containing(MediaType.APPLICATION_JSON_VALUE))
        .willReturn(aResponse()
            .withFixedDelay(0)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .withStatus(responseStatus)
        )
    );
  }

  public static void setupDeleteStub(String requestUrl, int responseStatus) {

    stubFor(delete(urlEqualTo(requestUrl))
        .willReturn(aResponse()
            .withFixedDelay(0)
            .withStatus(responseStatus)
        )
    );
  }

}
