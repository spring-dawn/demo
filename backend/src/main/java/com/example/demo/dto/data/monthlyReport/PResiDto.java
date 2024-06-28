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
public class PResiDto {
    private String year;
    private String month;
    private String sggCd;
    private Long prevSpaces;
    private Long newSpaces;
    private Long lostSpaces;
    private Long reSpaces;
    private double variance;
    private Long thisSpaces;
    private double thisArea;
    private String varianceReason;
    private double nonUse;
    private double inUse;
    private String createDtm;

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
        private Long prevSpaces;
        private Long newSpaces;
        private Long lostSpaces;
        private Long reSpaces;
        private double variance;
        private Long thisSpaces;
        private double thisArea;
        private String varianceReason;
        private double nonUse;
        private double inUse;
        private String createDtm;

        private MrData mrData;
    }
}
