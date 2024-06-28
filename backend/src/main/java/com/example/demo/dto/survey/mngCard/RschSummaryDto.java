package com.example.demo.dto.survey.mngCard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
public class RschSummaryDto {
    // 실태조사_총괄표(summary)
    // 기본키
    private String year;
    private String sggNm;
    private String hjDong;
    private String block;
    private String dayNight;
    // 기타 컬럼
    private String pop;
    private String households;
    private String vehicleCnt;
    private String landUsage;
    private String emptyLands;
    private String emptyArea;

    private String pfTotal;
    private String pfRDSum;
    private String pfRDResi;
    private String pfRDEtc;
    private String pfOutSum;
    private String pfOutPub;
    private String pfOutPri;
    private String pfSubSum;
    private String pfSubResi;
    private String pfSubNonRegi;
    private String pfEmptySum;

    private String pdTotal;
    private String pdRDSum;
    private String pdRDIn;
    private String pdRDOut;
    private String pdRDIll;
    private String pdOutSum;
    private String pdOutPub;
    private String pdOutPri;
    private String pdSubSum;
    private String pdSubResi;
    private String pdSubNonRegi;
    private String pdEmptySum;  // 남구 파일에 주차수요-빈터 소계가 있으나 총괄 취합되지는 않음
    private String regionAnalysis;
    private String solution;

    private Double use = 0.0;
    private Double secure = 0.0;
    private Double ilegal = 0.0;
    private Double idle = 0.0;

    private String createDtm;

    @Data
    @Builder
    @AllArgsConstructor
    public static class RschPkReq {
        // 파라미터 묶음.
        private String year;
        private String sggNm;
        private String hjDong;
        private String block;
        private String dayNight;
    }


    // insert, update
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Req {
        private String year;
        private String sggNm;
        private String hjDong;
        private String block;
        private String dayNight;
        private String pop;
        private String households;
        private String vehicleCnt;
        private String landUsage;
        private String emptyLands;
        private String emptyArea;
        private String pfTotal;
        private String pfRDSum;
        private String pfRDResi;
        private String pfRDEtc;
        private String pfOutSum;
        private String pfOutPub;
        private String pfOutPri;
        private String pfSubSum;
        private String pfSubResi;
        private String pfSubNonRegi;
        private String pfEmptySum;
        private String pdTotal;
        private String pdRDSum;
        private String pdRDIn;
        private String pdRDOut;
        private String pdRDIll;
        private String pdOutSum;
        private String pdOutPub;
        private String pdOutPri;
        private String pdSubSum;
        private String pdSubResi;
        private String pdSubNonRegi;
        private String pdEmptySum;
        private String regionAnalysis;
        private String solution;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Keyword{
        private String year;
        private String sggNm;
    }


}
