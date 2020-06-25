package com.swarts.contactsservice.client.address;

import com.swarts.spring.reactive.testkit.MockWebServerKit;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.contract.spec.internal.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;

class AddressWebClientTest {

  private static final String ADDRESS_PATH = "/address/{addressId}";
  private AddressWebClient addressWebClient;
  private MockWebServerKit mockWebTestClient;

  @BeforeEach
  public void setup() {
    mockWebTestClient = MockWebServerKit.create();
    AddressProperties addressProperties = AddressProperties.builder()
        .url(mockWebTestClient.getMockServerUrl())
        .pathAddress(ADDRESS_PATH)
        .build();
    addressWebClient = new AddressWebClient(WebClient.builder(), addressProperties);
  }

  @AfterEach
  public void tearDown() throws IOException {
    mockWebTestClient.dispose();
  }

  @Test
  public void getAddressShouldRequestCorrectPathAndRetrieveAddress() {
    final Map<String, String> headers = Collections
        .singletonMap(HttpHeaders.CONTENT_TYPE, MediaTypes.APPLICATION_JSON);

    final String addressId = "address-1";

    Address addressResponse = Address.builder()
        .id(addressId)
        .build();

    final String expectedPath = ADDRESS_PATH.replace("{addressId}", addressId);

    mockWebTestClient
        .prepareMockResponseWith(HttpStatus.OK, addressResponse, headers)
        .call(() -> addressWebClient.getAddress(addressId))
        .expectResponse(addressResponse)
        .takeRequest()
        .expectHeader(HttpHeaders.ACCEPT, MediaTypes.APPLICATION_JSON)
        .expectMethod(HttpMethod.GET.name())
        .expectPath(expectedPath);
  }

  @Test
  public void getAddressShouldReturnsClientErrorWhenServerRespondsWith4xxError() {
    mockWebTestClient.prepareMockResponseWith(HttpStatus.BAD_REQUEST)
        .call(() -> addressWebClient.getAddress("address-2"))
        .expectClientError();
  }

  @Test
  public void getAddressShouldReturnsServerErrorWhenServerRespondsWith5xxError() {
    mockWebTestClient.prepareMockResponseWith(HttpStatus.INTERNAL_SERVER_ERROR)
        .call(() -> addressWebClient.getAddress("address-3"))
        .expectServerError();
  }

}