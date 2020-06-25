package com.swarts.contactsservice.client.address;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Street {

  @JsonProperty("line1")
  private String line1;

  @JsonProperty("line2")
  private String line2;

  @JsonProperty("line3")
  private String line3;
}
