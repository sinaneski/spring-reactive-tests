package com.swarts.customerservice.data;

import com.swarts.customerservice.client.address.Address;
import com.swarts.customerservice.client.address.Street;
import com.swarts.customerservice.client.user.User;

public class ClientDataProvider {

  public static Address addressRequest() {
    return Address.builder()
        .customerId("customer-1")
        .street(Street.builder()
            .line1("line1")
            .line2("line2")
            .line3("line3")
            .build())
        .postCode("PC1 2NB")
        .town("London")
        .country("UK")
        .build();
  }

  public static Address addressResponse() {
    return addressRequest().toBuilder()
        .id("address-1")
        .build();
  }

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
