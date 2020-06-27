package com.swarts.customerservice.client.user;

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
@ConfigurationProperties(prefix = "user-service")
public class UserProperties {

  private String url;
  private String pathUser;
  private String pathUsers;
}
