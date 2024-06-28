package com.example.demo.dto.survey.format;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FormatRdDto {
    private String year;
    private String seq;
    private String typeP;
    private String sggCd;
    private String blockNo;
    private String hjdCd;
    private String bjdCd;
    private String hjlCd;   // 울주군: 행정리코드
    private String name;
    private String spcsParked;
    private String spcsTotal;
    private String spcsCommon;
    private String spcsDis;
    private String spcsElec;
    private String spcsEtc;
    private String isPay;
    private String pay;
    private String typeF;
    private String isSlope;
    private String hasNonSlip;
    private String hasInfoSign;
    // 북구, 울주군
    private String isLegalD;
    private String typeCarD;
    private String blockSeqD;
    private String isLegalN;
    private String typeCarN;
    private String blockSeqN;
}
