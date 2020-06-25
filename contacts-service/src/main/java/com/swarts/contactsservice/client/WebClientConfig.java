package com.swarts.contactsservice.client;

import org.springframework.boot.web.reactive.function.client.WebClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ClientHttpConnector;

@Configuration
public class WebClientConfig {

  @Bean
  public WebClientCustomizer webClientCustomizer(ClientHttpConnector clientHttpConnector) {
    return builder -> builder.clientConnector(clientHttpConnector);
  }
}