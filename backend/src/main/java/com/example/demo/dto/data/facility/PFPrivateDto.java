package com.example.demo.dto.data.facility;

import com.example.demo.domain.data.facility.file.PFData;
import com.example.demo.domain.data.standardSet.StandardMng;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
public class PFPrivateDto {
//    private Long id;
    private String year;
    private String month;
    private String sggCd;
    // 1:1 표준관리대장
    private String mngNo;
    private String spcs;
    private Long totalSpcs;
    private String disabledSpcs;
    private String spcsIn;
    private String totalSpcsIn;
    private String disabledSpcsIn;
    private String lotNm;
    private String lotId;
    private String lotType;
    private String address;
    private String streetAddr;
    private String ceoCellNo;
    private String operateInfo;
    private String lat;
    private String lng;
    private String collectInfo;
    private String landRank;
    private String regiTm;
    private String regiType;
    // 중복 검사 컬럼
//    private String dupChk1;
//    private String dupChk2;
//    private String dupChk3;
//    private String dupChk4;
    private String createDtm;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Req {
//        private Long id;
        private String year;
        private String month;
        private String sggCd;
        // 표준관리대장
        private String mngNo;
        private String spcs;
        private Long totalSpcs;
        private String disabledSpcs;
        private String spcsIn;
        private String totalSpcsIn;
        private String disabledSpcsIn;
        private String lotNm;
        private String lotId;
        private String lotType;
        private String address;
        private String streetAddr;
        private String ceoCellNo;
        private String operateInfo;
        private String lat;
        private String lng;
        private String collectInfo;
        private String landRank;
        private String regiTm;
        private String regiType;
        // 중복 검사 컬럼
        private String dupChk1;
        private String dupChk2;
        private String dupChk3;
        private String dupChk4;
        // 원본파일
        private PFData pfData;

    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Keyword{
        private String year;
        private String month;
        private String sggCd;
        private String lotNm;
        private String address;
        private Long minSpcs;
        private Long maxSpcs;
    }
}
