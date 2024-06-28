package com.example.demo.dto.data.monthlyReport;

import com.example.demo.domain.data.monthlyReport.MrData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PSubDcrsDto {
    private Long id;
    private String year;
    private String month;
    private String sggCd;
    private String reportNo;
    private String location;
    private String owner;
    private String type;
    private Long spaces;
    private double totalArea;
    private String demolitionDt;
    private String demolitionReason;
    private String structure;
    private String buildUsage;
    private String createDtm;
    //추가 컬럼
    private String abtCelYn;
    private String abtIstYn;
    private String abtRoofYn;
    private String abtLagYn;
    private String abtEtcYn;
    private String abtNonYn;
    private String prmsnYmd;


    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Keyword {
        private String year;
        private String month;
        private String sggCd;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Req {
        private Long id;
        @NotBlank
        @NotNull
        private String year;
        @NotBlank
        @NotNull
        private String month;
        @NotBlank
        @NotNull
        private String sggCd;
        private String reportNo;
        private String location;
        private String owner;
        private String type;
        private Long spaces;
        private double totalArea;
        private String demolitionDt;
        private String demolitionReason;
        private String structure;
        private String buildUsage;
        private String createDtm;
        //추가 컬럼
        private String abtCelYn;
        private String abtIstYn;
        private String abtRoofYn;
        private String abtLagYn;
        private String abtEtcYn;
        private String abtNonYn;
        private String prmsnYmd;

        private MrData mrData;
    }
}
