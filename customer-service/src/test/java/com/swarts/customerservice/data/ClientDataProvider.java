package com.swarts.customerservice.data;

import com.swarts.customerservice.client.user.User;

public class ClientDataProvider {

  public static User userRequest() {
    return User.builder()
        .firstName("Maria")
        .lastName("Doe")
        .email("maria.doe@example.com")
        .build();
  }

  public static User userResponse() {
    return userRequest().toBuilder()
        .id("customer-1")
        .build();
  }
}
