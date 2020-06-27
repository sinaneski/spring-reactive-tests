package com.swarts.customerservice.data;

import com.swarts.customerservice.model.Customer;
import com.swarts.customerservice.model.CustomerAddress;
import java.util.Collections;

public class DataProvider {

  public static Customer customerRequest() {
    return Customer.builder()
        .firstName("Maria")
        .lastName("Doe")
        .email("maria.doe@example.com")
        .addressList(Collections.singletonList(customerAddressRequest()))
        .build();
  }

  public static CustomerAddress customerAddressRequest() {
    return CustomerAddress.builder()
        .customerId("customer-1")
        .city("London")
        .country("UK")
        .line1("line1")
        .line2("line2")
        .line3("line3")
        .postCode("PC1 2NB")
        .build();
  }

  public static Customer customerResponse() {
    return customerRequest().toBuilder()
        .id("customer-1")
        .addressList(Collections.singletonList(customerAddressResponse()))
        .build();
  }

  public static CustomerAddress customerAddressResponse() {
    return customerAddressRequest().toBuilder()
        .id("address-1")
        .build();
  }
}
