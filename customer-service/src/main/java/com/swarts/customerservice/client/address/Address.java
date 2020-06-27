package com.swarts.customerservice.client.address;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Address {

  @JsonProperty("address_id")
  private String id;

  @JsonProperty("customer_id")
  private String customerId;

  @JsonProperty("street_lines")
  private Street street;

  @JsonProperty("post_code")
  private String postCode;

  @JsonProperty("town")
  private String town;

  @JsonProperty("country")
  private String country;
}
