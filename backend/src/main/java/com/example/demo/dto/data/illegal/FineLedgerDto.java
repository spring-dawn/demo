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
public class FineLedgerDto {
    private Long id;
    private LocalDateTime rgtnHr;
    private LocalDate bfAvtsmtYmd;
    private LocalDate opnSbmsnTermBgngYmd;
    private LocalDate opnSbmsnTermEndYmd;
    private LocalDate rgtnYmd;
    private LocalDate endYmd;
    private String sgbCd;
    private String dptCd;
    private Integer frstFin;
    private String rdtRsnSeCd;
    private Integer rdtRt;
    private Integer fin;
    private Integer bfRdtRt;
    private Integer bfRdtAmt;
    private String nlvyPrcsSeCd;
    private String emptRsnSeCd;
    private String erppSeCd;
    private String spclFisBizCd;
    private String actSeCd;
    private String rprsTxmCd;
    private String operItemCd;
    private String rgtnSeCd;
    private String prkgVltSeCd;
    private String rgtnSpeclZoneSeCd;
    private String roadTrsptLawVintpAsmCd;
    private String sgbNm;
    private String prkgVltAcbKey;
    private String dptNm;
    private String spclFisBizNm;
    private String actSeNm;
    private String rprsTxmNm;
    private String operItemNm;
    private String vhclNm;
    private String rgtnSeNm;
    private String hrExcessYn;
    private String rgtnPlcNm;
    private String lawAtclCn;
    private String prkgVltSeNm;
    private String prkgVltSeEtcRsnCn;
    private String roadTrsptLawVintpAsmNm;
    private String gpsCdnXaxs;
    private String gpsCdnYaxs;
    private String rgtnSpeclZoneSeNm;
    private String rdtRsnSeNm;
    private String nlvyPrcsSeNm;
    private String emptRsnSeNm;
    private String erppSeNm;
    private String endRsnCn;
    private String rcvmtSeNm;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Keyword{
        private Long id;
    }
}
