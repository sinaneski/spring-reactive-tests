package com.swarts.customerservice.controller;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.swarts.customerservice.data.DataProvider;
import com.swarts.customerservice.model.Customer;
import com.swarts.customerservice.model.CustomerAddress;
import com.swarts.customerservice.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@ExtendWith(SpringExtension.class)
class CustomerControllerTest {

  private static final String CUSTOMERS_PATH = "/customers";
  private static final String CUSTOMER_PATH = "/customers/{collectionId}";
  private static final String ADDRESSES_PATH = "/customers/{collectionId}/address";
  private static final String ADDRESS_PATH = "/customers/{collectionId}/address/{addressId}";

  private WebTestClient webTestClient;

  private CustomerService customerService;

  @BeforeEach
  void setUp() {
    customerService = mock(CustomerService.class);

    webTestClient = WebTestClient
        .bindToController(new CustomerController(customerService))
        .build();
  }

  @Test
  void shouldAddCustomer() {
    Customer customerRequest = DataProvider.customerRequest();
    Customer customerResponse = DataProvider.customerResponse();

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
        .jsonPath("$.id").isEqualTo(customerResponse.getId());
  }

  @Test
  void shouldGetCustomer() {
    String customerId = "customer-1";
    Customer customer = DataProvider.customerResponse();

    when(customerService.getCustomer(customerId)).thenReturn(Mono.just(customer));

    webTestClient.get()
        .uri(CUSTOMER_PATH, customerId)
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.id").isEqualTo(customerId);
  }

  @Test
  void shouldAddAddress() {
    String customerId = "customer-1";
    CustomerAddress customerAddressRequest = DataProvider.customerAddressRequest();
    CustomerAddress customerAddressResponse = DataProvider.customerAddressResponse();

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
        .jsonPath("$.id").isEqualTo(customerAddressResponse.getId())
        .jsonPath("$.customerId").isEqualTo(customerId);
  }

  @Test
  void shouldGetAddressList() {
    String customerId = "customer-1";
    Customer customer = DataProvider.customerResponse();

    when(customerService.getCustomerAddressList(customerId))
        .thenReturn(Flux.fromIterable(customer.getAddressList()));

    webTestClient.get()
        .uri(ADDRESSES_PATH, customerId)
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.[0].customerId").isEqualTo(customerId);
  }

  @Test
  void shouldGetAddress() {
    String customerId = "customer-1";
    String addressId = "address-1";

    when(customerService.getCustomerAddress(customerId, addressId))
        .thenReturn(Mono.just(DataProvider.customerAddressResponse()));

    webTestClient.get()
        .uri(ADDRESS_PATH, customerId, addressId)
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.id").isEqualTo(addressId);
  }

  @Test
  void shouldDeleteAddress() {
    String customerId = "customer-1";
    String addressId = "address-1";

    when(customerService.deleteAddress(customerId, addressId)).thenReturn(Mono.empty());

    webTestClient.delete()
        .uri(ADDRESS_PATH, customerId, addressId)
        .exchange()
        .expectStatus()
        .isNoContent();
  }

}