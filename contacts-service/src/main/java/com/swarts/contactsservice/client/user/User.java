package com.swarts.contactsservice.client.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {

  @JsonProperty("id")
  private String id;

  @JsonProperty("fist_name")
  private String firstName;

  @JsonProperty("last_name")
  private String lastName;

  @JsonProperty("address_id")
  private String addressId;

}
