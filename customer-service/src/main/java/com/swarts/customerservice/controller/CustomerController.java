package com.swarts.customerservice.controller;

import com.swarts.customerservice.model.Customer;
import com.swarts.customerservice.model.CustomerAddress;
import com.swarts.customerservice.service.CustomerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
public class CustomerController {

  private final CustomerService customerService;

  public CustomerController(CustomerService customerService) {
    this.customerService = customerService;
  }

  @PostMapping(value = "/customers",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  public Mono<Customer> addCustomer(@RequestBody Customer request) {
    return customerService.addCustomer(request);
  }

  @GetMapping(value = "/customers/{customerId}",
      produces = MediaType.APPLICATION_JSON_VALUE)
  public Mono<Customer> getCustomer(@PathVariable String customerId) {
    return customerService.getCustomer(customerId);
  }

  @PostMapping(value = "/customers/{customerId}/address",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public Mono<CustomerAddress> addAddress(@RequestBody CustomerAddress request) {
    return customerService.addAddress(request);
  }

  @DeleteMapping(value = "/customers/{customerId}/address/{addressId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public Mono<Void> deleteAddress(
      @PathVariable String customerId,
      @PathVariable String addressId) {
    return customerService.deleteAddress(customerId, addressId);
  }
}
