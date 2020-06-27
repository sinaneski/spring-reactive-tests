package com.swarts.customerservice.service;

import com.swarts.customerservice.model.Customer;
import com.swarts.customerservice.model.CustomerAddress;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class CustomerService {

  public Mono<Customer> addCustomer(Customer customerRequest) {
    return null;
  }

  public Mono<Customer> getCustomer(String customerId) {
    return null;
  }

  public Mono<Void> deleteAddress(String customerId, String addressId) {
    return null;
  }

  public Mono<CustomerAddress> addAddress(CustomerAddress addressRequest) {
    return null;
  }
}
