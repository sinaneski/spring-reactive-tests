package com.swarts.customerservice.integration;

import com.swarts.customerservice.client.user.User;
import com.swarts.spring.reactive.testkit.WireMockKit;
import java.text.MessageFormat;
import wiremock.org.apache.http.HttpStatus;

public class ClientUserService {

  private static final String USERS_PATH = "/users";
  private static final String USER_PATH = "/users/{0}";

  private final User user;

  public ClientUserService(User user) {
    this.user = user;
  }

  public static ClientUserService stubWith(User user) {
    return new ClientUserService(user);
  }

  public ClientUserService getUserReturnOK() {
    WireMockKit.setupGetStub(MessageFormat.format(USER_PATH, user.getId()),
        HttpStatus.SC_OK,
        user);
    return this;
  }

  public ClientUserService postUserReturnCREATED() {
    WireMockKit.setupPostStub(USERS_PATH,
        HttpStatus.SC_CREATED,
        user);
    return this;
  }
}
