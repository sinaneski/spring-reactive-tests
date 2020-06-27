package com.swarts.customerservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class CustomerAddress {

  private String id;

  private String customerId;

  private String line1;

  private String line2;

  private String line3;

  private String postCode;

  private String city;

  private String country;
}
