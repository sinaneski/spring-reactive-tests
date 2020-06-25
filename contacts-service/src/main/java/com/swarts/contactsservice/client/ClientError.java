package com.swarts.contactsservice.client;

import lombok.Data;

@Data
public class ClientError {

  private int code;
  private String message;
}
