package com.example.demo.dto.data.illegal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import javax.persistence.Column;
import javax.persistence.Id;

@Data
@Builder
@AllArgsConstructor
public class IllFixedDto {
    private String seq;
    private String year;
    private String month;
    private String sgg;
    private String crdnBrnch;
    private String instlYmd;
    private String crdnPrd;
    private String crdnCtrM;
    private Integer crdnNocs;
    private String rmrk;
    private Integer levyAmt;
    private Integer clctnNocs;
    private Integer clctnAmt;
    private String lon;
    private String lat;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Req {
        private String seq;
        private String year;
        private String month;
        private String gugun;
        private String sgg;
        private String crdnBrnch;
        private Integer instlYmd;
        private String crdnPrd;
        private String crdnCtrM;
        private Integer crdnNocs;
        private String rmrk;
        private Integer levyAmt;
        private Integer clctnNocs;
        private Integer clctnAmt;
        private String lon;
        private String lat;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Keyword {
        private String year;
        private String month;
        private String gugun;
        private String seq;
    }
}
