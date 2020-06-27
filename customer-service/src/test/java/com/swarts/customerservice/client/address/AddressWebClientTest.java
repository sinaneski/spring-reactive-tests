package com.swarts.customerservice.client.address;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.swarts.spring.reactive.testkit.MockWebServerKit;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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

  private static final String ADDRESSES_PATH = "/address/{customerId}";
  private static final String ADDRESS_PATH = "/address/{customerId}/{addressId}";

  private AddressWebClient addressWebClient;
  private MockWebServerKit mockWebTestClient;

  @BeforeEach
  void setup() {
    mockWebTestClient = MockWebServerKit.create();
    AddressProperties addressProperties = AddressProperties.builder()
        .url(mockWebTestClient.getMockServerUrl())
        .pathAddresses(ADDRESSES_PATH)
        .pathAddress(ADDRESS_PATH)
        .build();
    addressWebClient = new AddressWebClient(WebClient.builder(), addressProperties);
  }

  @AfterEach
  void tearDown() throws IOException {
    mockWebTestClient.dispose();
  }

  @Test
  void getAddressesShouldRequestCorrectPathAndRetrieveAllAddress() {
    final Map<String, String> headers = Collections
        .singletonMap(HttpHeaders.CONTENT_TYPE, MediaTypes.APPLICATION_JSON);

    final String customerId = "customer-1";

    List<Address> addressList = Arrays.asList(
        Address.builder().id("address-1").build(),
        Address.builder().id("address-2").build()
    );

    final String expectedPath = ADDRESSES_PATH.replace("{customerId}", customerId);

    mockWebTestClient
        .prepareMockResponseWith(HttpStatus.OK, addressList, headers)
        .call(() -> addressWebClient.getAddresses(customerId))
        .expectResponseList(addressList.toArray())
        .takeRequest()
        .expectHeader(HttpHeaders.ACCEPT, MediaTypes.APPLICATION_JSON)
        .expectMethod(HttpMethod.GET.name())
        .expectPath(expectedPath);
  }

  @Test
  void getAddressesShouldReturnsClientErrorWhenServerRespondsWith4xxError() {
    mockWebTestClient.prepareMockResponseWith(HttpStatus.BAD_REQUEST)
        .call(() -> addressWebClient.getAddresses("customer-1"))
        .expectClientError();
  }

  @Test
  void getAddressesShouldReturnsServerErrorWhenServerRespondsWith5xxError() {
    mockWebTestClient.prepareMockResponseWith(HttpStatus.INTERNAL_SERVER_ERROR)
        .call(() -> addressWebClient.getAddresses("customer-1"))
        .expectServerError();
  }

  @Test
  void getAddressShouldRequestCorrectPathAndRetrieveAddress() {
    final Map<String, String> headers = Collections
        .singletonMap(HttpHeaders.CONTENT_TYPE, MediaTypes.APPLICATION_JSON);

    final String customerId = "customer-1";
    final String addressId = "address-1";

    Address addressResponse = Address.builder()
        .id(addressId)
        .build();

    final String expectedPath = ADDRESS_PATH
        .replace("{customerId}", customerId)
        .replace("{addressId}", addressId);

    mockWebTestClient
        .prepareMockResponseWith(HttpStatus.OK, addressResponse, headers)
        .call(() -> addressWebClient.getAddress(customerId, addressId))
        .expectResponse(addressResponse)
        .takeRequest()
        .expectHeader(HttpHeaders.ACCEPT, MediaTypes.APPLICATION_JSON)
        .expectMethod(HttpMethod.GET.name())
        .expectPath(expectedPath);
  }

  @Test
  void getAddressShouldReturnsClientErrorWhenServerRespondsWith4xxError() {
    mockWebTestClient.prepareMockResponseWith(HttpStatus.BAD_REQUEST)
        .call(() -> addressWebClient.getAddress("customer-1", "address-2"))
        .expectClientError();
  }

  @Test
  void getAddressShouldReturnsServerErrorWhenServerRespondsWith5xxError() {
    mockWebTestClient.prepareMockResponseWith(HttpStatus.INTERNAL_SERVER_ERROR)
        .call(() -> addressWebClient.getAddress("customer-1", "address-3"))
        .expectServerError();
  }

  @Test
  void createAddressShouldCreateRequestedAddress() throws JsonProcessingException {
    final Map<String, String> headers = Collections
        .singletonMap(HttpHeaders.CONTENT_TYPE, MediaTypes.APPLICATION_JSON);

    Address addressRequest = Address.builder()
        .customerId("customer-1")
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
        .expectPath(ADDRESSES_PATH.replace("{customerId}", "customer-1"))
        .expectBody(addressRequest, Address.class);
  }

  @Test
  void createAddressShouldReturnsClientErrorWhenServerRespondsWith4xxError() {
    mockWebTestClient.prepareMockResponseWith(HttpStatus.BAD_REQUEST)
        .call(() -> addressWebClient.createAddress(Address.builder().build()))
        .expectClientError();
  }

  @Test
  void createAddressShouldReturnsServerErrorWhenServerRespondsWith5xxError() {
    mockWebTestClient.prepareMockResponseWith(HttpStatus.INTERNAL_SERVER_ERROR)
        .call(() -> addressWebClient.createAddress(Address.builder().build()))
        .expectServerError();
  }


  @Test
  void deleteAddressShouldDeleteRequestedAddress() {

    final String customerId = "customer-1";
    final String addressId = "address-1";

    final String expectedPath = ADDRESS_PATH
        .replace("{customerId}", customerId)
        .replace("{addressId}", addressId);

    mockWebTestClient
        .prepareMockResponseWith(HttpStatus.NO_CONTENT)
        .call(() -> addressWebClient.deleteAddress(customerId, addressId))
        .expectNoContent()
        .takeRequest()
        .expectMethod(HttpMethod.DELETE.name())
        .expectPath(expectedPath);
  }

  @Test
  void deleteAddressShouldReturnsClientErrorWhenServerRespondsWith4xxError() {
    mockWebTestClient.prepareMockResponseWith(HttpStatus.BAD_REQUEST)
        .call(() -> addressWebClient.deleteAddress("customer-1", "address-2"))
        .expectClientError();
  }

  @Test
  void deleteAddressShouldReturnsServerErrorWhenServerRespondsWith5xxError() {
    mockWebTestClient.prepareMockResponseWith(HttpStatus.INTERNAL_SERVER_ERROR)
        .call(() -> addressWebClient.deleteAddress("customer-1", "address-3"))
        .expectServerError();
  }

}
