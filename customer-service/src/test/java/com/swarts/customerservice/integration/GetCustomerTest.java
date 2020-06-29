package com.swarts.customerservice.integration;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import com.swarts.customerservice.client.address.Address;
import com.swarts.customerservice.client.user.User;
import com.swarts.customerservice.data.ClientDataProvider;
import com.swarts.customerservice.data.DataProvider;
import com.swarts.customerservice.model.Customer;
import com.swarts.spring.reactive.testkit.HttpClientKit;
import java.io.IOException;
import java.text.MessageFormat;
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
public class GetCustomerTest {

  private static final String LOCALHOST = "http://localhost";
  private static final String CUSTOMER_URL = "{0}/customers/{1}";

  @LocalServerPort
  private int port;

  private String getBaseUrl() {
    return LOCALHOST + ":" + port;
  }

  @Test
  public void shouldReturnCustomer() throws IOException {

    //given

    final String customerId = "customer-1";

    final User userResponse = ClientDataProvider.userResponse();

    final Address addressResponse = ClientDataProvider.addressResponse();

    ClientUserService.stubWith(userResponse)
        .getUserReturnOK();

    ClientAddressService.stubWith(userResponse.getId())
        .addAddress(addressResponse)
        .getAddressListReturnOK();

    //when


    final String url = MessageFormat.format(CUSTOMER_URL, getBaseUrl(), customerId);

    Response response = HttpClientKit.performGet(url);

    //then

    Customer actualResponse = HttpClientKit.getResponseBody(response, Customer.class);

    Customer expectedResponse = DataProvider.customerResponse();

    assertThat(response.code(), is(HttpStatus.SC_OK));

    assertThat(actualResponse, is(expectedResponse));
  }

}
