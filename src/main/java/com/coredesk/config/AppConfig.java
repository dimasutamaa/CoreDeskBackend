package com.coredesk.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties("app")
public class AppConfig {
    private String jwtSecretKey;
    private Long jwtTokenExpiration;
    private String frontendUrl;
}
