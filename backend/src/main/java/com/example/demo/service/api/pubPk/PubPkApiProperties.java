package com.example.demo.service.api.pubPk;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties("pub-pk-api")
public class PubPkApiProperties {

    // 서비스키
    public String serviceKey;
}
