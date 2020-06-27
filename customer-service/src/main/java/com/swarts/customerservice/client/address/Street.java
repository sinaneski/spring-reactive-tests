package com.swarts.customerservice.client.address;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Street {

  @JsonProperty("line1")
  private String line1;

  @JsonProperty("line2")
  private String line2;

  @JsonProperty("line3")
  private String line3;
}
