package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class GisDto {

    @Data
    @Builder
    @AllArgsConstructor
    public static class CommonLayer {
        private String key;
        private String name;
        private Object data;
        private String type;
        private String group;
    }

    @Data
    @Builder
    @AllArgsConstructor
    public static class SearchReq {
        private String gugun;
        private String hd;
    }

    @Data
    @Builder
    @AllArgsConstructor
    public static class BuildingManagementReq {
        private String gugun;
        private String hd;
    }

    @Data
    @Builder
    @AllArgsConstructor
    public static class StandardData {
        private String no;
        private String name;
    }
}
