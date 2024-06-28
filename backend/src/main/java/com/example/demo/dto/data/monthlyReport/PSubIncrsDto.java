package com.example.demo.dto.data.monthlyReport;

import com.example.demo.domain.data.monthlyReport.MrData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PSubIncrsDto {
    private Long id;
    @NotBlank @NotNull
    private String year;
    @NotBlank @NotNull
    private String month;
    @NotBlank @NotNull
    private String sggCd;
    private String buildType;
    private String buildNm;
    private String permitNo;
    private String buildOwner;
    private String location;
    private String approvalDt;
    private String mainUse;
    private String subUse;
    private double totalArea;
    private Long spaces;
    private Long households;
    private Long generation;
    private String createDtm;
    private String prmsnYmd;
    private double ttlFlarea;
    private String bldHo;
    private Long addPkspaceCnt;
    private double subau;
    private double subse;
    private String rmrk;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Keyword{
        private String year;
        private String month;
        private String sggCd;
        private String buildNm;
        private String buildOwner;
        private String permitNo;
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
        private String buildType;
        private String buildNm;
        private String permitNo;
        private String buildOwner;
        private String location;
        private String approvalDt;
        private String mainUse;
        private String subUse;
        private double totalArea;
        private Long spaces;
        private Long households;
        private Long generation;
        private String createDtm;
        private String prmsnYmd;
        private double ttlFlarea;
        private String bldHo;
        private Long addPkspaceCnt;
        private double subau;
        private double subse;
        private String rmrk;

        private MrData mrData;
    }
}
