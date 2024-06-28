package com.example.demo.service.api.bm;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties("building-management-api")
public class BuildingManagementApiProperties {

    // 서비스키
    public String serviceKey;
}
