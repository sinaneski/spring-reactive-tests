# spring-reactive-tests

The aim of this project is to demonstrate Unit, Mutation and Integration tests in Spring reactive web. 

Following utility classes were implemented:

 - `MockWebServerKit` to easily mock and test `WebClient` in unit test.
 - `HttpClientKit` to easily send requests to our controller in integration test.
 - `WireMockKit` to easily stub our 3rd party dependencies in integration test.

You could use these classes in your projects. 

A demo project,`customer-service`, were implemented to show usage of these utility classes and testing a Spring reactive application.
 
`customer-service` gets customer information `user-service`, and address information from `address-service`.

## Endpoints

**`customer-service` endpoints**

|  Operation | Endpoint                                       | Description                                     |
| ---------- | ---------------------------------------------- | ----------------------------------------------- |  
| POST       | `/customers`                                   | Add a new customer                              |
| POST       | `/customers/{customerId}/address`              | Add a new address to a given customer           |
| GET        | `/customers/{customerId}/address`              | Get address list of a given customer            |
| GET        | `/customers/{customerId}/address/{addressId}`  | Get an address of a given customer              |
| DELETE     | `/customers/{customerId}/address/{addressId}`  | Delete a given address                          |


**`user-service` endpoints**

|  Operation | Endpoint                                       | Description                                     |
| ---------- | ---------------------------------------------- | ----------------------------------------------- |  
| POST       | `/users`                                       | Add a new user                                  |
| GET        | `/users/{userId}`                              | Get user information                            |


**`address-service` endpoints**

|  Operation | Endpoint                                       | Description                                     |
| ---------- | ---------------------------------------------- | ----------------------------------------------- |  
| POST       | `/address/{customerId}`                        | Add a new address                               |
| GET        | `/address/{customerId}`                        | Get address list of a given customer            |
| GET        | `/customers/{customerId}/{addressId}`          | Get an address of a given customer              |
| DELETE     | `/customers/{customerId}/{addressId}`          | Delete agiven address                           |

# Unit Test

Testing Spring `controller`, `services` and `clients (WebClient)` components were explained in this section. 

## Testing Controller

`WebTestClient` were used to send request to the controller. 

`@ExtendWith(SpringExtension.class)` annotations should be added to use `WebTest` client. 

Here is a sample to test a `GET` endpoint: 

```java 
    webTestClient.get()
        .uri(CUSTOMER_PATH, customerId)
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.id").isEqualTo(customerId);
```

## Testing Service

`StepVerifier` were used to test reactive service implementations. Dependencies were mocked using `Mockito`.

For each test a `addressWebClient` created as a `mock` class.
```java  
  @BeforeEach
  void setUp() {
    addressWebClient = Mockito.mock(AddressWebClient.class);

    addressService = new AddressService(addressWebClient);
  }
```

`createAddress` method were mocked using `when` statement. The method were consumed using `StepVerifier`. 
 
Response was verified `expectNext` and `verifyComplete`.

```java 
  @Test
  void shouldAddAddress() {
    Address addressRequest = ClientDataProvider.addressRequest();
    Address addressResponse = ClientDataProvider.addressResponse();

    when(addressWebClient.createAddress(addressRequest)).thenReturn(Mono.just(addressResponse));
    
    StepVerifier.create(addressService.addAddress(addressRequest))
        .expectNext(addressResponse)
        .verifyComplete();
  }
```

Error cases and exception were verified using `expectErrorMatches` statement. 
```java 
    ...
    StepVerifier.create(addressService.addAddress(addressRequest))
        .expectErrorMatches(new ApplicationException(ErrorCode.ADDRESS_INVALID_REQUEST)::equals)
        .verify();
```

## Testing WebClient

`MockWebServerKit` class were implemented as a utility class to test spring `WebClient` in an easy way.

This class uses `MockWebServer` from `okhttp3` project in order to mock the third party server responses.

### How `MockWebServerKit` works

- `.prepareMockResponseWith(HttpStatus.OK, addressResponse, headers)` : is used to set up a dummy response when an endpoint called from a 3rd party client. 
- `.call(() -> addressWebClient.getAddress(addressId))`: `ClientDelegate` is FunctionalInterface that has a `call` method. You can delegate this call to endpoint method.
- the response could be `successful` or `error`. You can check success body with `expectResponse` , error cases by `expectClientError` and `expectServerError`. For expectResponse, give the expected object to able to check response body. 
- `.takeRequest()`:  takes the request. After this call you can only check the request. You can check `path`, `header` parameters. Or for `POST` and `PUT` operations you can check `body`.

### How to use `MockWebServerKit`

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
 
Here, `mockWebTestClient.getMockServerUrl()` is `base-url`.
 
2. Prepare a mock response for a given request and check actual webClient implementation calls endpoint using 
correct path, header, body fields and get expected response.

- Example: testing a GET endpoint call
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

- Example: testing a POST endpoint call
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
  
- Example: testing a DELETE endpoint call

    ```java 
      mockWebTestClient
            .prepareMockResponseWith(HttpStatus.NO_CONTENT)
            .call(() -> addressWebClient.deleteAddress(addressId))
            .expectNoContent()
            .takeRequest()
            .expectMethod(HttpMethod.DELETE.name())
            .expectPath(ADDRESS_PATH.replace("{addressId}", addressId))
    ```
  
- Example: testing client errors when endpoint return 4xx error

    ```java 
      mockWebTestClient.prepareMockResponseWith(HttpStatus.BAD_REQUEST)
           .call(() -> addressWebClient.deleteAddress("address-2"))
           .expectClientError();
    ```

- Example: testing server errors when endpoint return 5xx error

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

Test coverage could be measured **code and mutation coverage**.  
Code coverage measures percentage of execution paths that exercised during tests. 
On the other hand mutation test dynamically change the code, cause the tests fail and measures tests coverage.

In this project, [pitest](https://pitest.org/) was used for the mutation testing.


Each change in the code called as mutant. 
You can check all mutators from [here](https://pitest.org/quickstart/mutators/) for the pitest framework.

The `customer-service` project is a gradle project, and pitest integrated to the project using [info.solidsoft.pitest](https://plugins.gradle.org/plugin/info.solidsoft.pitest) gradle plugin.

You can check settings from build.gradle file. 

```groovy

pitest {
    threads.set(4)
    outputFormats.set(['XML', 'HTML'])
    timestampedReports.set(false)
    mutators.set(['CONDITIONALS_BOUNDARY', 'VOID_METHOD_CALLS', 'NEGATE_CONDITIONALS',
                  'INVERT_NEGS', 'MATH', 'INCREMENTS',
                  'TRUE_RETURNS', 'FALSE_RETURNS', 'PRIMITIVE_RETURNS', 'EMPTY_RETURNS', 'NULL_RETURNS']
    )
    timeoutConstInMillis.set(10000)
    junit5PluginVersion.set('0.12')
}
```

- threads: Number of threads to run pitest
- outputFormats: To generate pitest reports in XML and HTML formats
- timestampedReports: Generate reports and named report directory with timestamp. Setted false.
- mutators: List of mutators. Default mutators will be used w/o setting this field.
- timeoutConstInMillis: Test timeouts
- junit5PluginVersion: to run JUnit5 tests with pitest. Check [pitest-junit5-plugin](https://github.com/pitest/pitest-junit5-plugin)

To run the pitest on your local run the following command in the project root directory.

 ```bash
 ./gradlew pitest
 ```

Test reports will be under `./customer-service/build/reports/pitests` directory. You can open `index.html` in a browser to see the mutation coverage and details.


# Integration Test

`WireMock`, `OkHttpClient`  will be used in integration test.
