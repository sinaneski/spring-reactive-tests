package com.swarts.customerservice.integration;

import com.swarts.customerservice.client.address.Address;
import com.swarts.spring.reactive.testkit.WireMockKit;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import wiremock.org.apache.http.HttpStatus;

public class ClientAddressService {

  private static final String ADDRESSES_PATH = "/address/{0}";
  private static final String ADDRESS_PATH = "/address/{0}/{1}";

  private final String customerId;
  private final List<Address> addressList;

  public ClientAddressService(String customerId) {
    this.addressList = new ArrayList<>();
    this.customerId = customerId;
  }

  public static ClientAddressService stubWith(String customerId) {
    return new ClientAddressService(customerId);
  }

  public ClientAddressService getAddressListReturnOK() {
    WireMockKit.setupGetStub(MessageFormat.format(ADDRESSES_PATH, customerId),
        HttpStatus.SC_OK,
        addressList);
    return this;
  }

  public ClientAddressService getAddressOK(String addressId) {
    Address address = addressList.get(0);
    WireMockKit.setupGetStub(MessageFormat.format(ADDRESS_PATH, customerId, addressId),
        HttpStatus.SC_OK,
        address);
    return this;
  }

  public ClientAddressService createAddressReturnCREATED() {
    WireMockKit.setupPostStub(MessageFormat.format(ADDRESSES_PATH, customerId),
        HttpStatus.SC_CREATED,
        addressList.get(0));
    return this;
  }

  public ClientAddressService deleteAddressReturnOK(String addressId) {
    WireMockKit.setupDeleteStub(MessageFormat.format(ADDRESS_PATH, customerId, addressId),
        HttpStatus.SC_NO_CONTENT);
    return this;
  }

  public ClientAddressService addAddress(Address address) {
    address.setCustomerId(customerId);
    addressList.add(address);
    return this;
  }
}
