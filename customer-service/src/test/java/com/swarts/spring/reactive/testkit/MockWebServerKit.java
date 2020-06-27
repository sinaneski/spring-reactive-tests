package com.swarts.spring.reactive.testkit;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsInstanceOf.instanceOf;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.swarts.customerservice.client.ClientException;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.reactivestreams.Publisher;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

public class MockWebServerKit {

  private static ObjectWriter objectWriter = new ObjectMapper().writer();
  private final WebClient webClient;
  private MockWebServer server;

  private Publisher<?> actualClientResponse;

  private MockWebServerKit() {
    this.server = new MockWebServer();
    this.webClient = WebClient
        .builder()
        .baseUrl(this.server.url("").toString())
        .build();
  }

  public static MockWebServerKit create() {
    return new MockWebServerKit();
  }

  public WebClient getWebClient() {
    return webClient;
  }

  public String getMockServerUrl() {
    return this.server.url("").toString();
  }

  public void dispose() throws IOException {
    this.server.shutdown();
  }

  public <T> MockWebServerKit prepareMockResponseWith(HttpStatus status) {
    prepareResponse(response -> response.setResponseCode(status.value()));
    return this;
  }

  public <T> MockWebServerKit prepareMockResponseWith(HttpStatus status, Map<String, String> headers) {
    prepareResponse(response -> {
      response.setResponseCode(status.value());
      headers.forEach(response::addHeader);
    });
    return this;
  }

  public <T> MockWebServerKit prepareMockResponseWith(HttpStatus status, T responseBody, Map<String, String> headers) {
    prepareResponse(response -> {
      response
          .setResponseCode(status.value())
          .setBody(toJson(responseBody));
      headers.forEach(response::addHeader);
    });

    return this;
  }

  private void prepareResponse(Consumer<MockResponse> consumer) {
    MockResponse response = new MockResponse();
    consumer.accept(response);
    this.server.enqueue(response);
  }

  private static <T> String toJson(T value) {
    try {
      return objectWriter.writeValueAsString(value);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  public <T> MockWebServerKit call(ClientDelegate<T> clientDelegate) {
    actualClientResponse = clientDelegate.call();
    return this;
  }

  public <T> MockWebServerKit expectClientError() {
    StepVerifier.create((Publisher<T>) actualClientResponse)
        .expectErrorMatches(t -> {
          assertThat(t, instanceOf(ClientException.class));
          assertThat(((ClientException) t).isClientError(), is(true));
          return true;
        })
        .verify();
    return this;
  }

  public <T> MockWebServerKit expectServerError() {
    StepVerifier.create((Publisher<T>) actualClientResponse)
        .expectErrorMatches(t -> {
          assertThat(t, instanceOf(ClientException.class));
          assertThat(((ClientException) t).isServerError(), is(true));
          return true;
        })
        .verify();
    return this;
  }

  public <T> MockWebServerKit expectResponse(T response) {
    StepVerifier.create((Publisher<T>) actualClientResponse)
        .expectNext(response)
        .verifyComplete();
    return this;
  }

  public <T> MockWebServerKit expectResponseList(T[] responseArray) {
    StepVerifier.create((Publisher<T>) actualClientResponse)
        .expectNext(responseArray)
        .verifyComplete();
    return this;
  }

  public <T> MockWebServerKit expectNoContent() {
    StepVerifier.create((Publisher<T>) actualClientResponse)
        .verifyComplete();
    return this;
  }

  public RequestVerifier takeRequest() {
    try {
      RecordedRequest request = this.server.takeRequest(1000, TimeUnit.MILLISECONDS);
      return new RequestVerifier(request);
    } catch (InterruptedException ex) {
      throw new IllegalStateException(ex);
    }
  }

  @FunctionalInterface
  public interface ClientDelegate<T> {

    Publisher<T> call();
  }

  public static class RequestVerifier {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final RecordedRequest request;

    RequestVerifier(RecordedRequest request) {
      assertThat("Request was null", request, is(notNullValue()));
      this.request = request;
    }

    public RequestVerifier expectPath(String path) {
      assertThat(request.getPath(), is(equalTo(path)));
      return this;
    }

    public RequestVerifier expectHeader(String name, String value) {
      assertThat(request.getHeader(name), is(equalTo(value)));
      return this;
    }

    public RequestVerifier expectMethod(String method) {
      assertThat(request.getMethod(), is(equalTo(method)));
      return this;
    }

    public <T> RequestVerifier expectBody(T body, Class<? extends T> bodyClass)
        throws JsonProcessingException {

      assertThat(objectMapper.readValue(request.getBody().readUtf8(), bodyClass), is(equalTo(body)));
      return this;
    }

    public RequestVerifier expectBody(String bodyContent) {
      assertThat(request.getBody().readUtf8(), is(equalTo(bodyContent)));
      return this;
    }
  }

}
