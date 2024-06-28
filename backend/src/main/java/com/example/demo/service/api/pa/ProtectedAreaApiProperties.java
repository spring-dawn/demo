package com.example.demo.service.api.pa;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties("protected-area-api")
public class ProtectedAreaApiProperties {

    // 서비스키
    public String serviceKey;
}
