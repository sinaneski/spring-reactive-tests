package com.swarts.customerservice.client;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientResponse;

@Getter
public class ClientException extends RuntimeException {

  private final HttpStatus status;

  public ClientException(HttpStatus status, String message) {
    super(message);
    this.status = status;
  }

  public static ClientException from(ClientResponse response) {
    return new ClientException(response.statusCode(),
        response.toEntity(ClientError.class).toString());
  }

  public boolean isClientError() {
    return status.is4xxClientError();
  }

  public boolean isServerError() {
    return status.is5xxServerError();
  }

  public static boolean isClientError(Throwable throwable) {
    return throwable instanceof ClientException
        && ((ClientException) throwable).isClientError();
  }

  public static boolean isServerError(Throwable throwable) {
    return throwable instanceof ClientException
        && ((ClientException) throwable).isServerError();
  }
}
