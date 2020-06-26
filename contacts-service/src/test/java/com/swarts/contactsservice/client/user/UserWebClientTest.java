package com.swarts.contactsservice.client.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.swarts.spring.reactive.testkit.MockWebServerKit;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.contract.spec.internal.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;

class UserWebClientTest {

  private static final String USERS_PATH = "/users";
  private static final String USER_PATH = "/users/{userId}";

  private UserWebClient userWebClient;
  private MockWebServerKit mockWebTestClient;

  @BeforeEach
  void setup() {
    mockWebTestClient = MockWebServerKit.create();

    UserProperties userProperties = UserProperties.builder()
        .url(mockWebTestClient.getMockServerUrl())
        .pathUsers(USERS_PATH)
        .pathUser(USER_PATH)
        .build();

    userWebClient = new UserWebClient(WebClient.builder(), userProperties);
  }

  @AfterEach
  void tearDown() throws IOException {
    mockWebTestClient.dispose();
  }

  @Test
  void getUserShouldRequestCorrectPathAndRetrieveUser() {
    final Map<String, String> headers = Collections
        .singletonMap(HttpHeaders.CONTENT_TYPE, MediaTypes.APPLICATION_JSON);

    final String userId = "user-1";

    User addressResponse = User.builder()
        .id(userId)
        .build();

    final String expectedPath = USER_PATH.replace("{userId}", userId);

    mockWebTestClient
        .prepareMockResponseWith(HttpStatus.OK, addressResponse, headers)
        .call(() -> userWebClient.getUser(userId))
        .expectResponse(addressResponse)
        .takeRequest()
        .expectHeader(HttpHeaders.ACCEPT, MediaTypes.APPLICATION_JSON)
        .expectMethod(HttpMethod.GET.name())
        .expectPath(expectedPath);
  }

  @Test
  void getUserShouldReturnsClientErrorWhenServerRespondsWith4xxError() {
    mockWebTestClient.prepareMockResponseWith(HttpStatus.BAD_REQUEST)
        .call(() -> userWebClient.getUser("user-2"))
        .expectClientError();
  }

  @Test
  void getUserShouldReturnsServerErrorWhenServerRespondsWith5xxError() {
    mockWebTestClient.prepareMockResponseWith(HttpStatus.INTERNAL_SERVER_ERROR)
        .call(() -> userWebClient.getUser("user-3"))
        .expectServerError();
  }

  @Test
  void addUserShouldReturnAddedUser() throws JsonProcessingException {
    final Map<String, String> headers = Collections
        .singletonMap(HttpHeaders.CONTENT_TYPE, MediaTypes.APPLICATION_JSON);

    User userRequest = User.builder()
        .firstName("Maria")
        .lastName("Doe")
        .email("maria.doe@example.com")
        .build();

    User userResponse = userRequest.toBuilder()
        .id("user-1")
        .build();

    mockWebTestClient
        .prepareMockResponseWith(HttpStatus.CREATED, userResponse, headers)
        .call(() -> userWebClient.addUser(userRequest))
        .expectResponse(userResponse)
        .takeRequest()
        .expectHeader(HttpHeaders.ACCEPT, MediaTypes.APPLICATION_JSON)
        .expectHeader(HttpHeaders.CONTENT_TYPE, MediaTypes.APPLICATION_JSON)
        .expectMethod(HttpMethod.POST.name())
        .expectPath(USERS_PATH)
        .expectBody(userRequest, User.class);
  }

  @Test
  void addUserShouldReturnsClientErrorWhenServerRespondsWith4xxError() {
    mockWebTestClient.prepareMockResponseWith(HttpStatus.BAD_REQUEST)
        .call(() -> userWebClient.addUser(User.builder().build()))
        .expectClientError();
  }

  @Test
  void addUserShouldReturnsServerErrorWhenServerRespondsWith5xxError() {
    mockWebTestClient.prepareMockResponseWith(HttpStatus.INTERNAL_SERVER_ERROR)
        .call(() -> userWebClient.addUser(User.builder().build()))
        .expectServerError();
  }

}