package com.swarts.customerservice.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.swarts.customerservice.client.address.Address;
import com.swarts.customerservice.client.user.User;
import com.swarts.customerservice.data.ClientDataProvider;
import com.swarts.customerservice.data.DataProvider;
import com.swarts.customerservice.model.Customer;
import com.swarts.customerservice.model.CustomerAddress;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class CustomerServiceTest {

  private CustomerService customerService;

  private UserService mockUserService;
  private AddressService mockAddressService;

  @BeforeEach
  void setUp() {
    mockUserService = Mockito.mock(UserService.class);
    mockAddressService = Mockito.mock(AddressService.class);
    customerService = new CustomerService(mockUserService, mockAddressService);
  }

  @Test
  void shouldAddCustomer() {
    Customer customerRequest = DataProvider.customerRequest();
    Customer customerResponse = DataProvider.customerResponse();

    when(mockAddressService.addAddressList(any())).thenReturn(Flux.just(ClientDataProvider.addressResponse()));

    when(mockUserService.addUser(any(User.class))).thenReturn(Mono.just(ClientDataProvider.userResponse()));

    Mono<Customer> customerMono = customerService.addCustomer(customerRequest);

    StepVerifier.create(customerMono)
        .expectNext(customerResponse)
        .verifyComplete();
  }

  @Test
  void addCustomerShouldPassCorrectParametersToUserService() {
    Customer customerRequest = DataProvider.customerRequest();

    ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);

    when(mockAddressService.addAddressList(any())).thenReturn(Flux.just(ClientDataProvider.addressResponse()));

    when(mockUserService.addUser(userArgumentCaptor.capture())).thenReturn(Mono.just(ClientDataProvider.userResponse()));

    StepVerifier.create(customerService.addCustomer(customerRequest))
        .consumeNextWith(response -> { })
        .verifyComplete();

    User userRequest = userArgumentCaptor.getValue();
    assertThat(userRequest, is(notNullValue()));
    assertThat(userRequest.getFirstName(), is(customerRequest.getFirstName()));
    assertThat(userRequest.getLastName(), is(customerRequest.getLastName()));
    assertThat(userRequest.getEmail(), is(customerRequest.getEmail()));
  }

  @Test
  void addCustomerShouldPassCorrectParametersToAddressService() {
    Customer customerRequest = DataProvider.customerRequest();

    ArgumentCaptor<List<Address>> addressArgumentCaptor = ArgumentCaptor.forClass(List.class);

    when(mockAddressService.addAddressList(addressArgumentCaptor.capture()))
        .thenReturn(Flux.just(ClientDataProvider.addressResponse()));

    when(mockUserService.addUser(any(User.class))).thenReturn(Mono.just(ClientDataProvider.userResponse()));

    StepVerifier.create(customerService.addCustomer(customerRequest))
        .consumeNextWith(response -> { })
        .verifyComplete();

    List<Address> addressList = addressArgumentCaptor.getValue();
    assertThat(addressList, is(notNullValue()));
    assertThat(addressList.size(), is(1));
    Address address = addressList.get(0);
    assertThat(address, is(ClientDataProvider.addressRequest()));
  }

  @Test
  void shouldGetCustomer() {
    String customerId = "customer-1";

    Customer customerResponse = DataProvider.customerResponse();

    when(mockUserService.getUser(customerId)).thenReturn(Mono.just(ClientDataProvider.userResponse()));

    when(mockAddressService.getAddressList(customerId)).thenReturn(Flux.just(ClientDataProvider.addressResponse()));

    Mono<Customer> customerMono = customerService.getCustomer(customerId);

    StepVerifier.create(customerMono)
        .expectNext(customerResponse)
        .verifyComplete();
  }


  @Test
  void shouldGetCustomerAddressList() {
    String customerId = "customer-1";

    CustomerAddress customerAddressResponse = DataProvider.customerAddressResponse();

    when(mockUserService.getUser(customerId)).thenReturn(Mono.just(ClientDataProvider.userResponse()));

    when(mockAddressService.getAddressList(customerId)).thenReturn(Flux.just(ClientDataProvider.addressResponse()));

    Flux<CustomerAddress> addressFlux = customerService.getCustomerAddressList(customerId);

    StepVerifier.create(addressFlux)
        .expectNext(customerAddressResponse)
        .verifyComplete();
  }

  @Test
  void shouldGetCustomerAddress() {
    String customerId = "customer-1";
    String addressId = "address-1";

    CustomerAddress customerAddressResponse = DataProvider.customerAddressResponse();

    when(mockUserService.getUser(customerId)).thenReturn(Mono.just(ClientDataProvider.userResponse()));

    when(mockAddressService.getAddress(customerId, addressId)).thenReturn(Mono.just(ClientDataProvider.addressResponse()));

    Mono<CustomerAddress> addressMono = customerService.getCustomerAddress(customerId, addressId);

    StepVerifier.create(addressMono)
        .expectNext(customerAddressResponse)
        .verifyComplete();
  }

  @Test
  void shouldDeleteAddress() {
    String customerId = "customer-1";
    String addressId = "address-1";

    when(mockAddressService.deleteAddress(customerId, addressId)).thenReturn(Mono.empty());

    StepVerifier.create(customerService.deleteAddress(customerId, addressId))
        .verifyComplete();
  }
}