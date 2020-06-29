package com.swarts.customerservice.integration;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import com.swarts.customerservice.client.address.Address;
import com.swarts.customerservice.client.user.User;
import com.swarts.customerservice.data.ClientDataProvider;
import com.swarts.customerservice.data.DataProvider;
import com.swarts.customerservice.model.CustomerAddress;
import com.swarts.spring.reactive.testkit.HttpClientKit;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;
import okhttp3.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import wiremock.org.apache.http.HttpStatus;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 8091)
public class GetCustomerAddressTest {

  private static final String LOCALHOST = "http://localhost";
  private static final String CUSTOMER_ADDRESS_LIST_URL = "{0}/customers/{1}/address";
  private static final String CUSTOMER_ADDRESS_URL = "{0}/customers/{1}/address/{2}";

  @LocalServerPort
  private int port;

  private String getBaseUrl() {
    return LOCALHOST + ":" + port;
  }

  @Test
  public void shouldReturnCustomerAddressList() throws IOException {

    //given

    final String customerId = "customer-1";
    final String addressId = "address-1";

    final User userResponse = ClientDataProvider.userResponse();

    final Address addressResponse = ClientDataProvider.addressResponse();

    ClientAddressService.stubWith(userResponse.getId())
        .addAddress(addressResponse)
        .getAddressListReturnOK();

    //when


    final String url = MessageFormat.format(CUSTOMER_ADDRESS_LIST_URL, getBaseUrl(), customerId);

    Response response = HttpClientKit.performGet(url);

    //then

    List<CustomerAddress> actualResponse = HttpClientKit.getResponseBody(response,
        new TypeReference<List<CustomerAddress>>() {});

    CustomerAddress expectedResponse = DataProvider.customerAddressResponse();

    assertThat(response.code(), is(HttpStatus.SC_OK));

    assertThat(actualResponse, hasItem(expectedResponse));
  }

  @Test
  public void shouldReturnCustomerAddress() throws IOException {

    //given

    final String customerId = "customer-1";
    final String addressId = "address-1";

    final User userResponse = ClientDataProvider.userResponse();

    final Address addressResponse = ClientDataProvider.addressResponse();

    ClientAddressService.stubWith(userResponse.getId())
        .addAddress(addressResponse)
        .getAddressOK(addressId);

    //when


    final String url = MessageFormat.format(CUSTOMER_ADDRESS_URL, getBaseUrl(), customerId, addressId);

    Response response = HttpClientKit.performGet(url);

    //then

    CustomerAddress actualResponse = HttpClientKit.getResponseBody(response, CustomerAddress.class);

    CustomerAddress expectedResponse = DataProvider.customerAddressResponse();

    assertThat(response.code(), is(HttpStatus.SC_OK));

    assertThat(actualResponse, is(expectedResponse));
  }

}
