package com.example.demo.dto.system;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
public class FeedbackDto {
    private Long id;
    private String sggCd;
    private String dept;
    private String title;
    private String contents;
    private Long hit;
    private String status;
    private String createId;
    private String createDtm;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Req {
        private Long id;
        private String title;
        private String contents;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Keyword {
        private String sggCd;
        private String dept;
        private String title;
        private String contents;
        private String startDt;
        private String endDt;
    }
}
