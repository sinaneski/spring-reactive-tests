package com.swarts.customerservice.controller;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.swarts.customerservice.model.Customer;
import com.swarts.customerservice.model.CustomerAddress;
import com.swarts.customerservice.service.CustomerService;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@ExtendWith(SpringExtension.class)
class CustomerControllerTest {

  private static final String CUSTOMERS_PATH = "/customers";
  private static final String CUSTOMER_PATH = "/customers/{collectionId}";
  private static final String ADDRESSES_PATH = "/customers/{collectionId}/address";
  private static final String ADDRESS_PATH = "/customers/{collectionId}/address/{addressId}";

  private WebTestClient webTestClient;

  private CustomerService customerService;
  private CustomerController customerController;

  @BeforeEach
  void setUp() {
    customerService = mock(CustomerService.class);
    customerController = new CustomerController(customerService);

    webTestClient = WebTestClient
        .bindToController(customerController)
        .build();

  }

  @Test
  void shouldAddCustomer() {
    Customer customerRequest = customerRequest();
    Customer customerResponse = customerResponse();

    when(customerService.addCustomer(customerRequest)).thenReturn(Mono.just(customerResponse));

    webTestClient.post()
        .uri(CUSTOMERS_PATH)
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(customerRequest)
        .exchange()
        .expectStatus()
        .isCreated()
        .expectBody()
        .jsonPath("$.id", customerResponse.getId());
  }

  @Test
  void shouldGetCustomer() {
    String customerId = "customer-1";
    Customer customer = customerResponse();

    when(customerService.getCustomer(customerId)).thenReturn(Mono.just(customer));

    webTestClient.get()
        .uri(CUSTOMER_PATH, customerId)
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.id", customerId);
  }

  @Test
  void shouldAddAddress() {
    String customerId = "customer-1";
    CustomerAddress customerAddressRequest = customerAddressRequest();
    CustomerAddress customerAddressResponse = customerAddressRequest();

    when(customerService.addAddress(customerAddressRequest)).thenReturn(Mono.just(customerAddressResponse));

    webTestClient.post()
        .uri(ADDRESSES_PATH, customerId)
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(customerAddressRequest)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.customerId", customerId);
  }

  @Test
  void shouldDelete() {
    String customerId = "customer-1";
    String addressId = "address-1";

    when(customerService.deleteAddress(customerId, addressId)).thenReturn(Mono.empty());

    webTestClient.delete()
        .uri(ADDRESS_PATH, customerId, addressId)
        .exchange()
        .expectStatus()
        .isNoContent();
  }

  private Customer customerRequest() {
    return Customer.builder()
        .firstName("Maria")
        .lastName("Doe")
        .email("maria.doe@example.com")
        .addressList(Collections.singletonList(customerAddressRequest()))
        .build();
  }

  private CustomerAddress customerAddressRequest() {
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

  private Customer customerResponse() {
    return customerRequest().toBuilder()
        .id("customer-1")
        .addressList(Collections.singletonList(customerAddressResponse()))
        .build();
  }

  private CustomerAddress customerAddressResponse() {
    return customerAddressRequest().toBuilder()
        .id("address-1")
        .build();
  }
}