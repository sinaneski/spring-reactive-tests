package com.swarts.customerservice.client.user;

import com.swarts.customerservice.client.ClientException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class UserWebClient {

  private final WebClient webClient;
  private final UserProperties userProperties;

  public UserWebClient(
      WebClient.Builder webClientBuilder,
      UserProperties userProperties) {
    this.webClient = webClientBuilder.baseUrl(userProperties.getUrl()).build();
    this.userProperties = userProperties;
  }

  public Mono<User> getUser(String userId) {
    return webClient.get()
        .uri(userProperties.getPathUser(), userId)
        .accept(MediaType.APPLICATION_JSON)
        .retrieve()
        .onStatus(HttpStatus::isError, response -> Mono.just(ClientException.from(response)))
        .bodyToMono(User.class);
  }

  public Mono<User> addUser(User user) {
    return webClient.post()
        .uri(userProperties.getPathUsers())
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(user)
        .retrieve()
        .onStatus(HttpStatus::isError, response -> Mono.just(ClientException.from(response)))
        .bodyToMono(User.class);  }
}
