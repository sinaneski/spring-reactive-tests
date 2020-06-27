package com.swarts.customerservice.service;

import com.swarts.customerservice.client.ClientException;
import com.swarts.customerservice.client.address.Address;
import com.swarts.customerservice.client.address.AddressWebClient;
import com.swarts.customerservice.exception.ApplicationException;
import com.swarts.customerservice.exception.ErrorCode;
import java.util.List;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class AddressService {

  private final AddressWebClient addressWebClient;

  public AddressService(AddressWebClient addressWebClient) {
    this.addressWebClient = addressWebClient;
  }

  public Mono<Address> addAddress(Address address) {
    return addressWebClient.createAddress(address)
        .onErrorMap(ClientException::isClientError,
            e -> new ApplicationException(ErrorCode.ADDRESS_INVALID_REQUEST))
        .onErrorMap(ClientException::isServerError,
            e -> new ApplicationException(ErrorCode.ADDRESS_SERVICE_ERROR));
  }

  public Flux<Address> addAddressList(List<Address> addressList) {
    return Flux.fromIterable(addressList)
        .flatMap(addressWebClient::createAddress)
        .onErrorMap(ClientException::isClientError,
            e -> new ApplicationException(ErrorCode.ADDRESS_INVALID_REQUEST))
        .onErrorMap(ClientException::isServerError,
            e -> new ApplicationException(ErrorCode.ADDRESS_SERVICE_ERROR));
  }

  public Flux<Address> getAddressList(String customerId) {
    return addressWebClient.getAddresses(customerId)
        .onErrorMap(ClientException::isClientError,
            e -> new ApplicationException(ErrorCode.ADDRESS_NOT_FOUND))
        .onErrorMap(ClientException::isServerError,
            e -> new ApplicationException(ErrorCode.ADDRESS_SERVICE_ERROR));
  }

  public Mono<Address> getAddress(String customerId, String addressId) {
    return addressWebClient.getAddress(customerId, addressId)
        .onErrorMap(ClientException::isClientError,
            e -> new ApplicationException(ErrorCode.ADDRESS_NOT_FOUND))
        .onErrorMap(ClientException::isServerError,
            e -> new ApplicationException(ErrorCode.ADDRESS_SERVICE_ERROR));

  }

  public Mono<Void> deleteAddress(String customerId, String addressId) {
    return addressWebClient.deleteAddress(customerId, addressId)
        .onErrorMap(ClientException::isServerError,
            e -> new ApplicationException(ErrorCode.ADDRESS_SERVICE_ERROR))
        .onErrorResume(ClientException::isClientError, e -> Mono.empty());
  }

}
