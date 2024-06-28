
package com.example.demo.dto.data.monthlyReport;

import com.example.demo.domain.data.monthlyReport.MrData;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PStatusTotalDto {
    @NotNull
    @NotBlank
    private String year;
    @NotNull
    @NotBlank
    @Size(min = 2)
    private String month;
    @NotNull
    @NotBlank
    private String sggCd;


    @JsonProperty("PBL_L_SUBTOTAL")
    private double PBL_L_SUBTOTAL;
    @JsonProperty("PBL_S_SUBTOTAL")
    private double PBL_S_SUBTOTAL;
    @JsonProperty("PBL_A_SUBTOTAL")
    private double PBL_A_SUBTOTAL;


    @JsonProperty("PBLRD_PAY_L_SUM")
    private double PBLRD_PAY_L_SUM;
    @JsonProperty("PBLRD_PAY_S_SUM")
    private double PBLRD_PAY_S_SUM;
    @JsonProperty("PBLRD_PAY_A_SUM")
    private double PBLRD_PAY_A_SUM;
    @JsonProperty("PBLRD_FREE_L_SUM")
    private double PBLRD_FREE_L_SUM;
    @JsonProperty("PBLRD_FREE_S_SUM")
    private double PBLRD_FREE_S_SUM;
    @JsonProperty("PBLRD_FREE_A_SUM")
    private double PBLRD_FREE_A_SUM;
    @JsonProperty("PBLRD_RESI_L_SUM")
    private double PBLRD_RESI_L_SUM;
    @JsonProperty("PBLRD_RESI_S_SUM")
    private double PBLRD_RESI_S_SUM;
    @JsonProperty("PBLRD_RESI_A_SUM")
    private double PBLRD_RESI_A_SUM;
    @JsonProperty("PBLOUT_PAY_L_SUM")
    private double PBLOUT_PAY_L_SUM;
    @JsonProperty("PBLOUT_PAY_S_SUM")
    private double PBLOUT_PAY_S_SUM;
    @JsonProperty("PBLOUT_PAY_A_SUM")
    private double PBLOUT_PAY_A_SUM;
    @JsonProperty("PBLOUT_FREE_L_SUM")
    private double PBLOUT_FREE_L_SUM;
    @JsonProperty("PBLOUT_FREE_S_SUM")
    private double PBLOUT_FREE_S_SUM;
    @JsonProperty("PBLOUT_FREE_A_SUM")
    private double PBLOUT_FREE_A_SUM;

    @JsonProperty("PRV_L_SUM")
    private double PRV_L_SUM;
    @JsonProperty("PRV_S_SUM")
    private double PRV_S_SUM;
    @JsonProperty("PRV_A_SUM")
    private double PRV_A_SUM;

    @JsonProperty("SUB_L_SUBTOTAL")
    private double SUB_L_SUBTOTAL;
    @JsonProperty("SUB_S_SUBTOTAL")
    private double SUB_S_SUBTOTAL;
    @JsonProperty("SUB_A_SUBTOTAL")
    private double SUB_A_SUBTOTAL;


    @JsonProperty("SUBSE_SUR_L_SUM")
    private double SUBSE_SUR_L_SUM;
    @JsonProperty("SUBSE_SUR_S_SUM")
    private double SUBSE_SUR_S_SUM;
    @JsonProperty("SUBSE_SUR_A_SUM")
    private double SUBSE_SUR_A_SUM;
    @JsonProperty("SUBSE_MOD_L_SUM")
    private double SUBSE_MOD_L_SUM;
    @JsonProperty("SUBSE_MOD_S_SUM")
    private double SUBSE_MOD_S_SUM;
    @JsonProperty("SUBSE_MOD_A_SUM")
    private double SUBSE_MOD_A_SUM;
    @JsonProperty("SUBAU_ATT_L_SUM")
    private double SUBAU_ATT_L_SUM;
    @JsonProperty("SUBAU_ATT_S_SUM")
    private double SUBAU_ATT_S_SUM;
    @JsonProperty("SUBAU_ATT_A_SUM")
    private double SUBAU_ATT_A_SUM;
    @JsonProperty("SUBAU_PRV_L_SUM")
    private double SUBAU_PRV_L_SUM;
    @JsonProperty("SUBAU_PRV_S_SUM")
    private double SUBAU_PRV_S_SUM;
    @JsonProperty("SUBAU_PRV_A_SUM")
    private double SUBAU_PRV_A_SUM;

    @JsonProperty("OWN_L_SUBTOTAL")
    private double OWN_L_SUBTOTAL;
    @JsonProperty("OWN_S_SUBTOTAL")
    private double OWN_S_SUBTOTAL;
    @JsonProperty("OWN_A_SUBTOTAL")
    private double OWN_A_SUBTOTAL;

    @JsonProperty("OWN_HOME_L_SUM")
    private double OWN_HOME_L_SUM;
    @JsonProperty("OWN_HOME_S_SUM")
    private double OWN_HOME_S_SUM;
    @JsonProperty("OWN_HOME_A_SUM")
    private double OWN_HOME_A_SUM;
    @JsonProperty("OWN_APT_L_SUM")
    private double OWN_APT_L_SUM;
    @JsonProperty("OWN_APT_S_SUM")
    private double OWN_APT_S_SUM;
    @JsonProperty("OWN_APT_A_SUM")
    private double OWN_APT_A_SUM;

    @JsonProperty("TOTAL_L_SUM")
    private double TOTAL_L_SUM;
    @JsonProperty("TOTAL_S_SUM")
    private double TOTAL_S_SUM;
    @JsonProperty("TOTAL_A_SUM")
    private double TOTAL_A_SUM;


    private String createDtm;
    private LocalDate localDt;


    @Data
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
        @NotNull
        @NotBlank
        private String year;
        @NotNull
        @NotBlank
        @Size(min = 2)
        private String month;
        @NotNull
        @NotBlank
        private String sggCd;
        private double PBL_L_SUBTOTAL;
        private double PBL_S_SUBTOTAL;
        private double PBL_A_SUBTOTAL;

        private double PBLRD_PAY_L_SUM;
        private double PBLRD_PAY_S_SUM;
        private double PBLRD_PAY_A_SUM;
        private double PBLRD_FREE_L_SUM;
        private double PBLRD_FREE_S_SUM;
        private double PBLRD_FREE_A_SUM;
        private double PBLRD_RESI_L_SUM;
        private double PBLRD_RESI_S_SUM;
        private double PBLRD_RESI_A_SUM;
        private double PBLOUT_PAY_L_SUM;
        private double PBLOUT_PAY_S_SUM;
        private double PBLOUT_PAY_A_SUM;
        private double PBLOUT_FREE_L_SUM;
        private double PBLOUT_FREE_S_SUM;
        private double PBLOUT_FREE_A_SUM;
        private double PRV_L_SUM;
        private double PRV_S_SUM;
        private double PRV_A_SUM;

        private double SUB_L_SUBTOTAL;
        private double SUB_S_SUBTOTAL;
        private double SUB_A_SUBTOTAL;

        private double SUBSE_SUR_L_SUM;
        private double SUBSE_SUR_S_SUM;
        private double SUBSE_SUR_A_SUM;
        private double SUBSE_MOD_L_SUM;
        private double SUBSE_MOD_S_SUM;
        private double SUBSE_MOD_A_SUM;
        private double SUBAU_ATT_L_SUM;
        private double SUBAU_ATT_S_SUM;
        private double SUBAU_ATT_A_SUM;
        private double SUBAU_PRV_L_SUM;
        private double SUBAU_PRV_S_SUM;
        private double SUBAU_PRV_A_SUM;

        private double OWN_L_SUBTOTAL;
        private double OWN_S_SUBTOTAL;
        private double OWN_A_SUBTOTAL;

        private double OWN_HOME_L_SUM;
        private double OWN_HOME_S_SUM;
        private double OWN_HOME_A_SUM;
        private double OWN_APT_L_SUM;
        private double OWN_APT_S_SUM;
        private double OWN_APT_A_SUM;

        private double TOTAL_L_SUM;
        private double TOTAL_S_SUM;
        private double TOTAL_A_SUM;

        private String createDtm;
        private LocalDate localDt;

        private MrData mrData;
    }
}