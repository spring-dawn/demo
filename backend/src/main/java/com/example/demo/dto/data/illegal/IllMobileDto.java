package com.example.demo.dto.data.illegal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
public class IllMobileDto {
    private String seq;
    private String year;
    private String month;
    private String sgg;
    private String vhclNm;
    private String prchsYmd;
    private String crdnPrd;
    private String crdnCtrM;
    private Integer crdnNocs;
    private String rmrk;
    private Integer levyAmt;
    private Integer clctnNocs;
    private Integer clctnAmt;

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
        private String vhclNm;
        private String prchsYmd;
        private String crdnPrd;
        private String crdnCtrM;
        private Integer crdnNocs;
        private String rmrk;
        private Integer levyAmt;
        private Integer clctnNocs;
        private Integer clctnAmt;
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
