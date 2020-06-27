package com.swarts.customerservice.client.address;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Component
@ConfigurationProperties(prefix = "address-service")
public class AddressProperties {

  private String url;
  private String pathAddresses;
  private String pathAddress;
}
