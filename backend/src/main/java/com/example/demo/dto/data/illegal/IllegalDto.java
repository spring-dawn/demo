package com.example.demo.dto.data.illegal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class IllegalDto {
    private Long id;
    private String year;
    private String month;
    private String processStat;
    private String violationDt;
    private String evidenceNo;
    private String carNo;
    private String violationDtm;
    private String violationPlace;
    private String violatorNm;
    private String residentRegiNo;
    private String schoolZone;
    private Integer fflng;  // 과태료
    private String pic;
    private String requestData;
    private String isRll;
    private String code1;
    private String code2;
    private String violatedLaw;
    private String carType;
    private String carNm;
    private Integer capacity;
    private String inspectorNm;
    private String cameraNm;
    private String etc;
    private String comment;
    private String fflngNm;
    private String legalStat;
    private String crdnPtn; //crackdown
    private String preNoticeDay;
    private String preNoticePayTerm;
    private String nonPayReason;
    private String isUnrlCar;
    private String specialVioPlace;
    private String violatorAddr;
    private String createDtm;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Req {
        private Long id;
        private String year;
        private String month;
        private String processStat;
        private String violationDt;
        private String evidenceNo;
        private String carNo;
        private String violationDtm;
        private String violationPlace;
        private String violatorNm;
        private String residentRegiNo;
        private String schoolZone;
        private Integer fflng;  // 과태료
        private String pic;
        private String requestData;
        private String isRll;
        private String code1;
        private String code2;
        private String violatedLaw;
        private String carType;
        private String carNm;
        private Integer capacity;
        private String inspectorNm;
        private String cameraNm;
        private String etc;
        private String comment;
        private String fflngNm;
        private String legalStat;
        private String crdnPtn; //crackdown
        private String preNoticeDay;
        private String preNoticePayTerm;
        private String nonPayReason;
        private String isUnrlCar;
        private String specialVioPlace;
        private String violatorAddr;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Keyword {
        private String year;
        private String month;
    }
}
