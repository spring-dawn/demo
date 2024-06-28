package com.example.demo.service.api.residnt;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties("residnt-api")
public class ResidntApiProperties {

    // 서비스키
    public String serviceKey;
}
