package com.swarts.customerservice.service;

import com.swarts.customerservice.client.address.Address;
import com.swarts.customerservice.client.address.Street;
import com.swarts.customerservice.client.user.User;
import com.swarts.customerservice.model.Customer;
import com.swarts.customerservice.model.CustomerAddress;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class CustomerService {

  private final UserService userService;
  private final AddressService addressService;

  public CustomerService(UserService userService, AddressService addressService) {
    this.userService = userService;
    this.addressService = addressService;
  }

  public Mono<Customer> addCustomer(Customer customerRequest) {
    return validateCustomerRequest(customerRequest)
        .flatMap(request -> userService.addUser(toUser(request)))
        .zipWith(addressService
            .addAddressList(toAddressList(customerRequest.getAddressList()))
            .collectList())
        .map(tuple -> {
          Customer customer = toCustomer(tuple.getT1());
          customer.setAddressList(toCustomerAddressList(tuple.getT2()));
          return customer;
        });
  }

  public Mono<Customer> getCustomer(String customerId) {
    return userService.getUser(customerId)
        .zipWith(getCustomerAddressList(customerId).collectList())
        .map(tuple -> {
          Customer customer = toCustomer(tuple.getT1());
          customer.setAddressList(tuple.getT2());
          return customer;
        });
  }

  public Mono<CustomerAddress> addAddress(CustomerAddress addressRequest) {
    return validateAddressRequest(addressRequest)
        .map(this::toAddress)
        .flatMap(addressService::addAddress)
        .map(this::toCustomerAddress);
  }

  public Flux<CustomerAddress> getCustomerAddressList(String customerId) {
    return addressService.getAddressList(customerId)
        .map(this::toCustomerAddress);
  }

  public Mono<CustomerAddress> getCustomerAddress(String customerId, String addressId) {
    return addressService.getAddress(customerId, addressId)
        .map(this::toCustomerAddress);
  }


  public Mono<Void> deleteAddress(String customerId, String addressId) {
    return addressService.deleteAddress(customerId, addressId);
  }


  private Mono<Customer> validateCustomerRequest(Customer customer) {
    return Mono.just(customer);
  }

  private Mono<CustomerAddress> validateAddressRequest(CustomerAddress addressRequest) {
    return Mono.just(addressRequest);
  }

  private User toUser(Customer customer) {
    return User.builder()
        .id(customer.getId())
        .firstName(customer.getFirstName())
        .lastName(customer.getLastName())
        .email(customer.getEmail())
        .build();
  }

  private Customer toCustomer(User user) {
    return Customer.builder()
        .id(user.getId())
        .firstName(user.getFirstName())
        .lastName(user.getLastName())
        .email(user.getEmail())
        .build();
  }

  private List<CustomerAddress> toCustomerAddressList(List<Address> addressList) {
    return addressList.stream()
        .map(this::toCustomerAddress)
        .collect(Collectors.toList());
  }

  private CustomerAddress toCustomerAddress(Address address) {
    return CustomerAddress.builder()
        .id(address.getId())
        .customerId(address.getCustomerId())
        .postCode(address.getPostCode())
        .city(address.getTown())
        .country(address.getCountry())
        .line1(address.getStreet().getLine1())
        .line2(address.getStreet().getLine2())
        .line3(address.getStreet().getLine3())
        .build();
  }

  private List<Address> toAddressList(List<CustomerAddress> customerAddressList) {
    return customerAddressList.stream()
        .map(this::toAddress)
        .collect(Collectors.toList());
  }

  private Address toAddress(CustomerAddress customerAddress) {
    return Address.builder()
        .id(customerAddress.getId())
        .customerId(customerAddress.getCustomerId())
        .postCode(customerAddress.getPostCode())
        .town(customerAddress.getCity())
        .country(customerAddress.getCountry())
        .street(Street.builder()
            .line1(customerAddress.getLine1())
            .line2(customerAddress.getLine2())
            .line3(customerAddress.getLine3())
            .build())
        .build();
  }

}
