package com.swarts.customerservice.service;

import static org.mockito.Mockito.when;

import com.swarts.customerservice.client.ClientException;
import com.swarts.customerservice.client.user.User;
import com.swarts.customerservice.client.user.UserWebClient;
import com.swarts.customerservice.data.ClientDataProvider;
import com.swarts.customerservice.exception.ApplicationException;
import com.swarts.customerservice.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class UserServiceTest {

  private UserWebClient userWebClient;
  private UserService userService;

  @BeforeEach
  void setUp() {
    userWebClient = Mockito.mock(UserWebClient.class);

    userService = new UserService(userWebClient);
  }

  @Test
  void shouldAddUser() {

    User userRequest = ClientDataProvider.userRequest();
    User userResponse = ClientDataProvider.userResponse();

    when(userWebClient.addUser(userRequest)).thenReturn(Mono.just(userResponse));

    Mono<User> userMono = userService.addUser(userRequest);

    StepVerifier.create(userMono)
        .expectNext(userResponse)
        .verifyComplete();
  }


  @Test
  void addUserShouldReturnInvalidRequestErrorWhenServiceReturn4xxError() {

    User userRequest = ClientDataProvider.userRequest();

    when(userWebClient.addUser(userRequest))
        .thenReturn(Mono.error(new ClientException(HttpStatus.BAD_REQUEST, "Invalid request")));

    StepVerifier.create(userService.addUser(userRequest))
        .expectErrorMatches(new ApplicationException(ErrorCode.USER_INVALID_REQUEST)::equals)
        .verify();
  }

  @Test
  void addUserShouldReturnUserServiceErrorWhenServiceReturn5xxError() {

    User userRequest = ClientDataProvider.userRequest();

    when(userWebClient.addUser(userRequest))
        .thenReturn(Mono.error(new ClientException(HttpStatus.SERVICE_UNAVAILABLE, "service unavailable")));

    StepVerifier.create(userService.addUser(userRequest))
        .expectErrorMatches(new ApplicationException(ErrorCode.USER_SERVICE_ERROR)::equals)
        .verify();
  }


  @Test
  void shouldReturnUser() {
    User userResponse = ClientDataProvider.userResponse();

    String userId = "user-1";

    when(userWebClient.getUser(userId)).thenReturn(Mono.just(userResponse));

    Mono<User> userMono = userService.getUser(userId);

    StepVerifier.create(userMono)
        .expectNext(userResponse)
        .verifyComplete();
  }

  @Test
  void getUserShouldReturnNotFoundErrorWhenServiceReturn4xxError() {

    String userId = "user-1";

    when(userWebClient.getUser(userId))
        .thenReturn(Mono.error(new ClientException(HttpStatus.NOT_FOUND, "Invalid request")));

    StepVerifier.create(userService.getUser(userId))
        .expectErrorMatches(new ApplicationException(ErrorCode.CUSTOMER_NOT_FOUND)::equals)
        .verify();
  }

  @Test
  void getUserShouldReturnUserServiceErrorWhenServiceReturn5xxError() {

    String userId = "user-1";

    when(userWebClient.getUser(userId))
        .thenReturn(Mono.error(new ClientException(HttpStatus.SERVICE_UNAVAILABLE, "service unavailable")));

    StepVerifier.create(userService.getUser(userId))
        .expectErrorMatches(new ApplicationException(ErrorCode.USER_SERVICE_ERROR)::equals)
        .verify();
  }

}