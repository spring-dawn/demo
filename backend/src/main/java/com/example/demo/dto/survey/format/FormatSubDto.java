package com.example.demo.dto.survey.format;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FormatSubDto {
    private String year;
    private String seq;
    private String sggCd;
    private String typeP;
    private String blockNo;
    private String hjdCd;
    private String bjdCd;
    private String hjlCd;
    private String lotNoAddr1;
    private String lotNoAddr2;
    private String name;
    private String spcsTotal;
    private String isResi;
    private String typeF;
    private String mainUsage;
    private String isPay;
    private String pay;
    private String chUsage;
    private String dysfunc;
    private String isSlope;
    private String hasNonSlip;
    private String hasInfoSign;
    private String cctv;
    private String monitor;
    private String backup;
    private String spcsNon2wD;
    private String spcs2wD;
    private String spcsNon2wN;
    private String spcs2wN;
    private String roadNmAddr;
    private String roadNmNo1;
    private String roadNmNo2;
    private String gubun;
    private String isApt;
}
