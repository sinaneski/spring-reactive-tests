package com.swarts.contactsservice.client.address;

import com.swarts.contactsservice.client.ClientException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class AddressWebClient {

  private final WebClient webClient;
  private final AddressProperties addressProperties;

  public AddressWebClient(
      WebClient.Builder webClientBuilder,
      AddressProperties addressProperties) {
    this.webClient = webClientBuilder.baseUrl(addressProperties.getUrl()).build();
    this.addressProperties = addressProperties;
  }

  public Flux<Address> getAddresses(String customerId) {
    return webClient.get()
        .uri(addressProperties.getPathAddresses(), customerId)
        .accept(MediaType.APPLICATION_JSON)
        .retrieve()
        .onStatus(HttpStatus::isError, response -> Mono.just(ClientException.from(response)))
        .bodyToFlux(Address.class);
  }

  public Mono<Address> getAddress(String customerId, String addressId) {
    return webClient.get()
        .uri(addressProperties.getPathAddress(), customerId, addressId)
        .accept(MediaType.APPLICATION_JSON)
        .retrieve()
        .onStatus(HttpStatus::isError, response -> Mono.just(ClientException.from(response)))
        .bodyToMono(Address.class);
  }

  public Mono<Address> createAddress(Address address) {
    return webClient.post()
        .uri(addressProperties.getPathAddresses(), address.getCustomerId())
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(address)
        .retrieve()
        .onStatus(HttpStatus::isError, response -> Mono.just(ClientException.from(response)))
        .bodyToMono(Address.class);
  }

  public Mono<Void> deleteAddress(String customerId, String addressId) {
    return webClient.delete()
        .uri(addressProperties.getPathAddress(), customerId, addressId)
        .accept(MediaType.APPLICATION_JSON)
        .retrieve()
        .onStatus(HttpStatus::isError, response -> Mono.just(ClientException.from(response)))
        .bodyToMono(Void.class);
  }
}
