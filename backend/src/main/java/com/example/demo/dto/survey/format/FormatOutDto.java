package com.example.demo.dto.survey.format;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FormatOutDto {
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
    private String isPub;
    private String name;
    private String spcsTotal;
    private String typeF;
    private String isPay;
    private String pay;
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
    private String gubun;

}
