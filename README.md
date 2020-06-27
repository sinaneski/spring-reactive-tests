# spring-reactive-tests

WebClient, Mutation and Integration tests in spring reactive web. 

We'll implement an `customer-service` that get customer contact information from other rest apis.

`customer-service` will have following endpoints:
- POST `/customers/{customerId}`
- POST `/customers/{customerId}/address`
- GET `/customers/{customerId}/address`
- GET `/customers/{customerId}/address/{addressId}`
- DELETE `/customers/{customerId}/address/{addressId}`

`customer-service` gets customer information `user-service`, and address information from `address-service`.

`user-service` endpoints:
- POST `/users`
- GET `/users/{userId}`

`address-service` endpoints:
- POST `/address/{customerId}`
- GET `/address/{customerId}`
- GET `/address/{customerId}/{addressId}`
- DELETE `/address/{customerId}/{addressId}`

# Testing WebClient

We implemented `MockWebServerKit` class to test spring `WebClient` in an easy way.

This class uses `MockWebServer` from `okhttp3` project in order to mock the other apis server responses.

## How `MockWebServerKit` works

- `.prepareMockResponseWith(HttpStatus.OK, addressResponse, headers)` : we set up a dummy response when web client called the 3rd party this response will be return to our client.
- `.call(() -> addressWebClient.getAddress(addressId))` 
 we implemented `ClientDelegate` FunctionalInterface that has `call` method. We delegate this call to endpoint calls in our web client, and call our method.
- the response could be `successful` or `error`. We check success body with `expectResponse` , error cases by `expectClientError` and `expectServerError`. For expectResponse we give the expected object to able to check response body. 
- `.takeRequest()`  takes what request we send. After this call we only check our request. And, we can check `path`, `header` parameters. Or for `POST` and `PUT` operations we can check `body`.

## How to use `MockWebServerKit`

1. Create a mockWebTestClient using `mockWebTestClient = MockWebServerKit.create();` to start a mock server before each test.

    ```java 
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
     ``` 
 
Here we set `mockWebTestClient.getMockServerUrl()` to our `base-url`.
 
2. Prepare a mock response for a given request and check actual webClient implementation calls endpoint using 
correct path, header, body fields and get expected response.

- Example: testing GET endpoint call
    ```java 
      mockWebTestClient
            .prepareMockResponseWith(HttpStatus.OK, addressResponse, headers)
            .call(() -> addressWebClient.getAddress(addressId))
            .expectResponse(addressResponse)
            .takeRequest()
            .expectHeader(HttpHeaders.ACCEPT, MediaTypes.APPLICATION_JSON)
            .expectMethod(HttpMethod.GET.name())
            .expectPath(expectedPath);
    
    ```

- Example: testing POST endpoint call
    ```java 
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
    ```
  
- Example: testing DELETE endpoint call

    ```java 
      mockWebTestClient
            .prepareMockResponseWith(HttpStatus.NO_CONTENT)
            .call(() -> addressWebClient.deleteAddress(addressId))
            .expectNoContent()
            .takeRequest()
            .expectMethod(HttpMethod.DELETE.name())
            .expectPath(ADDRESS_PATH.replace("{addressId}", addressId))
    ```
  
- Example: testing client error when endpoint return 4xx error

    ```java 
      mockWebTestClient.prepareMockResponseWith(HttpStatus.BAD_REQUEST)
           .call(() -> addressWebClient.deleteAddress("address-2"))
           .expectClientError();
    ```

- Example: testing server error when endpoint return 5xx error

    ```java 
      mockWebTestClient.prepareMockResponseWith(HttpStatus.INTERNAL_SERVER_ERROR)
        .call(() -> addressWebClient.deleteAddress("address-3"))
        .expectServerError();
    ```

        
        
3. Close mock server using `mockWebTestClient.dispose();` after each test.

```java 
 @AfterEach
  public void tearDown() throws IOException {
    mockWebTestClient.dispose();
  }
 ``` 
  

# Mutation Test

We'll use `pitest` for mutation testing.

# Integration Test

We'll use `WireMock` and `OkHttpClient` in integration test.
