package com.swarts.customerservice.client;

import lombok.Data;

@Data
public class ClientError {

  private int code;
  private String message;
}
