package com.swarts.customerservice.service;

import static org.mockito.Mockito.when;

import com.swarts.customerservice.client.ClientException;
import com.swarts.customerservice.client.address.Address;
import com.swarts.customerservice.client.address.AddressWebClient;
import com.swarts.customerservice.data.ClientDataProvider;
import com.swarts.customerservice.exception.ApplicationException;
import com.swarts.customerservice.exception.ErrorCode;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class AddressServiceTest {

  private AddressWebClient addressWebClient;
  private AddressService addressService;

  @BeforeEach
  void setUp() {
    addressWebClient = Mockito.mock(AddressWebClient.class);

    addressService = new AddressService(addressWebClient);
  }

  @Test
  void shouldAddAddress() {
    Address addressRequest = ClientDataProvider.addressRequest();
    Address addressResponse = ClientDataProvider.addressResponse();

    when(addressWebClient.createAddress(addressRequest)).thenReturn(Mono.just(addressResponse));

    Mono<Address> addressMono = addressService.addAddress(addressRequest);

    StepVerifier.create(addressMono)
        .expectNext(addressResponse)
        .verifyComplete();
  }

  @Test
  void addAddressShouldReturnInvalidRequestErrorWhenServiceReturn4xxError() {

    Address addressRequest = ClientDataProvider.addressRequest();

    when(addressWebClient.createAddress(addressRequest))
        .thenReturn(Mono.error(new ClientException(HttpStatus.BAD_REQUEST, "Invalid request")));

    StepVerifier.create(addressService.addAddress(addressRequest))
        .expectErrorMatches(new ApplicationException(ErrorCode.ADDRESS_INVALID_REQUEST)::equals)
        .verify();
  }

  @Test
  void addAddressShouldReturnAddressServiceErrorWhenServiceReturn5xxError() {

    Address addressRequest = ClientDataProvider.addressRequest();

    when(addressWebClient.createAddress(addressRequest))
        .thenReturn(Mono.error(new ClientException(HttpStatus.SERVICE_UNAVAILABLE, "service unavailable")));

    StepVerifier.create(addressService.addAddress(addressRequest))
        .expectErrorMatches(new ApplicationException(ErrorCode.ADDRESS_SERVICE_ERROR)::equals)
        .verify();
  }

  @Test
  void shouldAddAddressList() {
    List<Address> addressRequest = Arrays.asList(
        ClientDataProvider.addressRequest(),
        ClientDataProvider.addressRequest().toBuilder()
            .postCode("XY1 1AB").
            build()
    );

    List<Address> addressResponse = Arrays.asList(
        ClientDataProvider.addressResponse(),
        ClientDataProvider.addressRequest().toBuilder()
            .id("address-2")
            .postCode("XY1 1AB")
            .build()
    );

    when(addressWebClient.createAddress(addressRequest.get(0))).thenReturn(Mono.just(addressResponse.get(0)));
    when(addressWebClient.createAddress(addressRequest.get(1))).thenReturn(Mono.just(addressResponse.get(1)));

    Flux<Address> addressFlux = addressService.addAddressList(addressRequest);

    StepVerifier.create(addressFlux)
        .expectNext(addressResponse.get(0))
        .expectNext(addressResponse.get(1))
        .verifyComplete();
  }

  @Test
  void addAddressListShouldReturnInvalidRequestErrorWhenServiceReturn4xxError() {

    List<Address> addressRequest = Collections.singletonList(ClientDataProvider.addressRequest());

    when(addressWebClient.createAddress(addressRequest.get(0)))
        .thenReturn(Mono.error(new ClientException(HttpStatus.BAD_REQUEST, "Invalid request")));

    StepVerifier.create(addressService.addAddressList(addressRequest))
        .expectErrorMatches(new ApplicationException(ErrorCode.ADDRESS_INVALID_REQUEST)::equals)
        .verify();
  }

  @Test
  void addAddressListShouldReturnAddressServiceErrorWhenServiceReturn5xxError() {

    List<Address> addressRequest = Collections.singletonList(ClientDataProvider.addressRequest());

    when(addressWebClient.createAddress(addressRequest.get(0)))
        .thenReturn(Mono.error(new ClientException(HttpStatus.SERVICE_UNAVAILABLE, "service unavailable")));

    StepVerifier.create(addressService.addAddressList(addressRequest))
        .expectErrorMatches(new ApplicationException(ErrorCode.ADDRESS_SERVICE_ERROR)::equals)
        .verify();
  }

  @Test
  void shouldReturnAddressList() {

    List<Address> addressResponse = Collections.singletonList(ClientDataProvider.addressResponse());

    String customerId = "customer-1";

    when(addressWebClient.getAddresses(customerId)).thenReturn(Flux.fromIterable(addressResponse));

    Flux<Address> addressFlux = addressService.getAddressList(customerId);

    StepVerifier.create(addressFlux)
        .expectNext(addressResponse.get(0))
        .verifyComplete();
  }

  @Test
  void getAddressListShouldReturnNotFoundErrorWhenServiceReturn4xxError() {

    String customerId = "customer-1";

    when(addressWebClient.getAddresses(customerId))
        .thenReturn(Flux.error(new ClientException(HttpStatus.NOT_FOUND, "No address found")));

    StepVerifier.create(addressService.getAddressList(customerId))
        .expectErrorMatches(new ApplicationException(ErrorCode.ADDRESS_NOT_FOUND)::equals)
        .verify();
  }

  @Test
  void getAddressListShouldReturnAddressServiceErrorWhenServiceReturn5xxError() {

    String customerId = "customer-1";

    when(addressWebClient.getAddresses(customerId))
        .thenReturn(Flux.error(new ClientException(HttpStatus.SERVICE_UNAVAILABLE, "service unavailable")));

    StepVerifier.create(addressService.getAddressList(customerId))
        .expectErrorMatches(new ApplicationException(ErrorCode.ADDRESS_SERVICE_ERROR)::equals)
        .verify();
  }

  @Test
  void shouldReturnAddress() {

    Address addressResponse = ClientDataProvider.addressResponse();

    String customerId = "customer-1";
    String addressId = "address-1";

    when(addressWebClient.getAddress(customerId, addressId)).thenReturn(Mono.just(addressResponse));

    Mono<Address> addressMono = addressService.getAddress(customerId, addressId);

    StepVerifier.create(addressMono)
        .expectNext(addressResponse)
        .verifyComplete();
  }

  @Test
  void getAddressShouldReturnNotErrorFoundWhenServiceReturn4xxError() {

    String customerId = "customer-1";
    String addressId = "address-1";

    when(addressWebClient.getAddress(customerId, addressId))
        .thenReturn(Mono.error(new ClientException(HttpStatus.NOT_FOUND, "address is not exist")));

    StepVerifier.create(addressService.getAddress(customerId, addressId))
        .expectErrorMatches(new ApplicationException(ErrorCode.ADDRESS_NOT_FOUND)::equals)
        .verify();
  }

  @Test
  void getAddressShouldReturnAddressServiceErrorWhenServiceReturn5xxError() {

    String customerId = "customer-1";
    String addressId = "address-1";

    when(addressWebClient.getAddress(customerId, addressId))
        .thenReturn(Mono.error(new ClientException(HttpStatus.SERVICE_UNAVAILABLE, "service unavailable")));

    StepVerifier.create(addressService.getAddress(customerId, addressId))
        .expectErrorMatches(new ApplicationException(ErrorCode.ADDRESS_SERVICE_ERROR)::equals)
        .verify();
  }

  @Test
  void shouldDeleteAddress() {

    String customerId = "customer-1";
    String addressId = "address-1";

    when(addressWebClient.deleteAddress(customerId, addressId)).thenReturn(Mono.empty());

    StepVerifier.create(addressService.deleteAddress(customerId, addressId))
        .verifyComplete();
  }

  @Test
  void deleteAddressShouldReturnNoContentWhenServiceReturn4xxError() {

    String customerId = "customer-1";
    String addressId = "address-1";

    when(addressWebClient.deleteAddress(customerId, addressId))
        .thenReturn(Mono.error(new ClientException(HttpStatus.NOT_FOUND, "address is not exist")));

    StepVerifier.create(addressService.deleteAddress(customerId, addressId))
        .verifyComplete();
  }

  @Test
  void deleteAddressShouldReturnAddressServiceErrorWhenServiceReturn5xxError() {

    String customerId = "customer-1";
    String addressId = "address-1";

    when(addressWebClient.deleteAddress(customerId, addressId))
        .thenReturn(Mono.error(new ClientException(HttpStatus.GATEWAY_TIMEOUT, "server error")));

    StepVerifier.create(addressService.deleteAddress(customerId, addressId))
        .expectErrorMatches(new ApplicationException(ErrorCode.ADDRESS_SERVICE_ERROR)::equals)
        .verify();
  }
}