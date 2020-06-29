package com.swarts.customerservice.integration;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

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
public class DeleteCustomerAddressTest {

  private static final String LOCALHOST = "http://localhost";
  private static final String CUSTOMER_ADDRESS_URL = "{0}/customers/{1}/address/{2}";

  @LocalServerPort
  private int port;

  private String getBaseUrl() {
    return LOCALHOST + ":" + port;
  }

  @Test
  public void shouldDeleteCustomerAddress() throws IOException {

    //given

    String customerId = "customer-1";
    String addressId = "address-1";
    ClientAddressService.stubWith(customerId)
        .deleteAddressReturnOK(addressId);

    //when

    final String url = MessageFormat.format(CUSTOMER_ADDRESS_URL, getBaseUrl(), customerId, addressId);

    Response response = HttpClientKit.performDelete(url);

    //then

    assertThat(response.code(), is(HttpStatus.SC_NO_CONTENT));
  }

}
