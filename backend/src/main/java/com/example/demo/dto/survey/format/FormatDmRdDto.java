package com.example.demo.dto.survey.format;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FormatDmRdDto {
    private String year;
    private String seq;
    private String sggCd;
    private String rschTime;
    private String bjdCd;
    private String hjdCd;
    private String blockNo;
    private String carNo;
    private String isLegal;
    private String typeC;
    private String isRegi;
    private String rmYn;
}
