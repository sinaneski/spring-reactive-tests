package com.swarts.customerservice.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class Customer {

  private String id;

  private String firstName;

  private String lastName;

  private String email;

  private List<CustomerAddress> addressList;
}