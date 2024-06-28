package com.example.demo.service.api.fh;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties("fire-hydrant-api")
public class FireHydrantApiProperties {

    // 서비스키
    public String serviceKey;
}
