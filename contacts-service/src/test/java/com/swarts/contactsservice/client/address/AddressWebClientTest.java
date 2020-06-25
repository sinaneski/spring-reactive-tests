package com.swarts.contactsservice.client.address;

import com.fasterxml.jackson.core.JsonProcessingException;
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

  private static final String ADDRESSES_PATH = "/address";
  private static final String ADDRESS_PATH = "/address/{addressId}";
  private AddressWebClient addressWebClient;
  private MockWebServerKit mockWebTestClient;

  @BeforeEach
  public void setup() {
    mockWebTestClient = MockWebServerKit.create();
    AddressProperties addressProperties = AddressProperties.builder()
        .url(mockWebTestClient.getMockServerUrl())
        .pathAddresses(ADDRESSES_PATH)
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

  @Test
  public void createAddressShouldCreateRequestedAddress() throws JsonProcessingException {
    final Map<String, String> headers = Collections
        .singletonMap(HttpHeaders.CONTENT_TYPE, MediaTypes.APPLICATION_JSON);

    Address addressRequest = Address.builder()
        .street(Street.builder()
            .line1("line1")
            .line2("line2")
            .build())
        .postCode("PC1 2NB")
        .town("London")
        .country("UK")
        .build();

    Address addressResponse = addressRequest.toBuilder()
        .id("address-1")
        .build();

    mockWebTestClient
        .prepareMockResponseWith(HttpStatus.CREATED, addressResponse, headers)
        .call(() -> addressWebClient.createAddress(addressRequest))
        .expectResponse(addressResponse)
        .takeRequest()
        .expectHeader(HttpHeaders.ACCEPT, MediaTypes.APPLICATION_JSON)
        .expectHeader(HttpHeaders.CONTENT_TYPE, MediaTypes.APPLICATION_JSON)
        .expectMethod(HttpMethod.POST.name())
        .expectPath(ADDRESSES_PATH)
        .expectBody(addressRequest, Address.class);
  }

  @Test
  public void createAddressShouldReturnsClientErrorWhenServerRespondsWith4xxError() {
    mockWebTestClient.prepareMockResponseWith(HttpStatus.BAD_REQUEST)
        .call(() -> addressWebClient.createAddress(Address.builder().build()))
        .expectClientError();
  }

  @Test
  public void createAddressShouldReturnsServerErrorWhenServerRespondsWith5xxError() {
    mockWebTestClient.prepareMockResponseWith(HttpStatus.INTERNAL_SERVER_ERROR)
        .call(() -> addressWebClient.createAddress(Address.builder().build()))
        .expectServerError();
  }


  @Test
  public void deleteAddressShouldDeleteRequestedAddress() throws JsonProcessingException {

    final String addressId = "address-1";

    mockWebTestClient
        .prepareMockResponseWith(HttpStatus.NO_CONTENT)
        .call(() -> addressWebClient.deleteAddress(addressId))
        .expectNoContent()
        .takeRequest()
        .expectMethod(HttpMethod.DELETE.name())
        .expectPath(ADDRESS_PATH.replace("{addressId}", addressId));
  }

  @Test
  public void deleteAddressShouldReturnsClientErrorWhenServerRespondsWith4xxError() {
    mockWebTestClient.prepareMockResponseWith(HttpStatus.BAD_REQUEST)
        .call(() -> addressWebClient.deleteAddress("address-2"))
        .expectClientError();
  }

  @Test
  public void deleteAddressShouldReturnsServerErrorWhenServerRespondsWith5xxError() {
    mockWebTestClient.prepareMockResponseWith(HttpStatus.INTERNAL_SERVER_ERROR)
        .call(() -> addressWebClient.deleteAddress("address-3"))
        .expectServerError();
  }

}
