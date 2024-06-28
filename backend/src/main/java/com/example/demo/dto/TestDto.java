package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
public class TestDto {
    private Map<String, Object> data;
    private String guCode;
    private String dongCode;
    private String bunCode;
    private String jiCode;
    private Integer count;
}
