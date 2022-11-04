package com.mtech.ique.ums.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "jwt")
@ConfigurationPropertiesScan
@Getter
@Setter
public class JWTConfig {

  private String publicKey;
  private String privateKey;
  private String secret;
  private String iss;
  private String sub;
  private Integer duration;
}
