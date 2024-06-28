package com.example.demo.dto.survey.format;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FormatDmEtcDto {
    private String year;
    private String seq;
    private String sggCd;
    private String bjdCd;
    private String hjdCd;
    private String blockNo;
    private String rschTime;
    private String lotNoAddr1;
    private String lotNoAddr2;
    private String spcsTotal;
    private String spcsNon2w;
    private String spcs2w;
    private String useGubun;
    private String isRegi;
    private String rmYn;
}
