package com.swarts.customerservice.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
  USER_SERVICE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "User service error"),
  USER_INVALID_REQUEST(HttpStatus.BAD_REQUEST, "User request invalid"),
  CUSTOMER_NOT_FOUND(HttpStatus.NOT_FOUND, "Customer not found"),
  ADDRESS_SERVICE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Address service error"),
  ADDRESS_INVALID_REQUEST(HttpStatus.BAD_REQUEST, "Address request invalid"),
  ADDRESS_NOT_FOUND(HttpStatus.NOT_FOUND, "Address not found");

  private final HttpStatus status;
  private final String message;

  ErrorCode(HttpStatus status, String message) {

    this.status = status;
    this.message = message;
  }
}
