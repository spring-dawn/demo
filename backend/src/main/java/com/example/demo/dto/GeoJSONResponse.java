package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GeoJSONResponse {

    public String type;
    public ArrayList<Feature> features;
    public int totalFeatures;
    public int numberMatched;
    public int numberReturned;
    public String timeStamp;
    public Crs crs;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Feature {
        public String type;
        public String id;
        public Geometry geometry;
        public String geometry_name;
        public Map<String, Object> properties;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Geometry {
        public String type;
        public List<Object> coordinates;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Crs {
        public String type;
        public Map<String, Object> properties;
    }
}
