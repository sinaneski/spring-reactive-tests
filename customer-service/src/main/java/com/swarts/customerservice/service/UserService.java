package com.swarts.customerservice.service;

import com.swarts.customerservice.client.ClientException;
import com.swarts.customerservice.client.user.User;
import com.swarts.customerservice.client.user.UserWebClient;
import com.swarts.customerservice.exception.ApplicationException;
import com.swarts.customerservice.exception.ErrorCode;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class UserService {

  private final UserWebClient userWebClient;

  public UserService(UserWebClient userWebClient) {
    this.userWebClient = userWebClient;
  }

  public Mono<User> addUser(User user) {
    return userWebClient.addUser(user)
        .onErrorMap(ClientException::isClientError,
            e -> new ApplicationException(ErrorCode.USER_INVALID_REQUEST))
        .onErrorMap(ClientException::isServerError,
            e -> new ApplicationException(ErrorCode.USER_SERVICE_ERROR));
  }

  public Mono<User> getUser(String userId) {
    return userWebClient.getUser(userId)
        .onErrorMap(ClientException::isClientError,
            e -> new ApplicationException(ErrorCode.CUSTOMER_NOT_FOUND))
        .onErrorMap(ClientException::isServerError,
            e -> new ApplicationException(ErrorCode.USER_SERVICE_ERROR));
  }

}
