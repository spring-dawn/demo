
package com.example.demo.dto.data.monthlyReport;

import com.example.demo.domain.data.monthlyReport.MrData;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PStatusDto {
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
    @JsonProperty("PBLRD_PAY_L_I")
    private double PBLRD_PAY_L_I;
    @JsonProperty("PBLRD_PAY_L_D")
    private double PBLRD_PAY_L_D;
    @JsonProperty("PBLRD_PAY_S_I")
    private double PBLRD_PAY_S_I;
    @JsonProperty("PBLRD_PAY_S_D")
    private double PBLRD_PAY_S_D;
    @JsonProperty("PBLRD_PAY_A_I")
    private double PBLRD_PAY_A_I;
    @JsonProperty("PBLRD_PAY_A_D")
    private double PBLRD_PAY_A_D;
    @JsonProperty("PBLRD_FREE_L_I")
    private double PBLRD_FREE_L_I;
    @JsonProperty("PBLRD_FREE_L_D")
    private double PBLRD_FREE_L_D;
    @JsonProperty("PBLRD_FREE_S_I")
    private double PBLRD_FREE_S_I;
    @JsonProperty("PBLRD_FREE_S_D")
    private double PBLRD_FREE_S_D;
    @JsonProperty("PBLRD_FREE_A_I")
    private double PBLRD_FREE_A_I;
    @JsonProperty("PBLRD_FREE_A_D")
    private double PBLRD_FREE_A_D;
    @JsonProperty("PBLRD_RESI_L_I")
    private double PBLRD_RESI_L_I;
    @JsonProperty("PBLRD_RESI_L_D")
    private double PBLRD_RESI_L_D;
    @JsonProperty("PBLRD_RESI_S_I")
    private double PBLRD_RESI_S_I;
    @JsonProperty("PBLRD_RESI_S_D")
    private double PBLRD_RESI_S_D;
    @JsonProperty("PBLRD_RESI_A_I")
    private double PBLRD_RESI_A_I;
    @JsonProperty("PBLRD_RESI_A_D")
    private double PBLRD_RESI_A_D;
    @JsonProperty("PBLOUT_PAY_L_I")
    private double PBLOUT_PAY_L_I;
    @JsonProperty("PBLOUT_PAY_L_D")
    private double PBLOUT_PAY_L_D;
    @JsonProperty("PBLOUT_PAY_S_I")
    private double PBLOUT_PAY_S_I;
    @JsonProperty("PBLOUT_PAY_S_D")
    private double PBLOUT_PAY_S_D;
    @JsonProperty("PBLOUT_PAY_A_I")
    private double PBLOUT_PAY_A_I;
    @JsonProperty("PBLOUT_PAY_A_D")
    private double PBLOUT_PAY_A_D;
    @JsonProperty("PBLOUT_FREE_L_I")
    private double PBLOUT_FREE_L_I;
    @JsonProperty("PBLOUT_FREE_L_D")
    private double PBLOUT_FREE_L_D;
    @JsonProperty("PBLOUT_FREE_S_I")
    private double PBLOUT_FREE_S_I;
    @JsonProperty("PBLOUT_FREE_S_D")
    private double PBLOUT_FREE_S_D;
    @JsonProperty("PBLOUT_FREE_A_I")
    private double PBLOUT_FREE_A_I;
    @JsonProperty("PBLOUT_FREE_A_D")
    private double PBLOUT_FREE_A_D;
    @JsonProperty("PRV_L_I")
    private double PRV_L_I;
    @JsonProperty("PRV_L_D")
    private double PRV_L_D;
    @JsonProperty("PRV_S_I")
    private double PRV_S_I;
    @JsonProperty("PRV_S_D")
    private double PRV_S_D;
    @JsonProperty("PRV_A_I")
    private double PRV_A_I;
    @JsonProperty("PRV_A_D")
    private double PRV_A_D;
    @JsonProperty("SUBSE_SUR_L_I")
    private double SUBSE_SUR_L_I;
    @JsonProperty("SUBSE_SUR_L_D")
    private double SUBSE_SUR_L_D;
    @JsonProperty("SUBSE_SUR_S_I")
    private double SUBSE_SUR_S_I;
    @JsonProperty("SUBSE_SUR_S_D")
    private double SUBSE_SUR_S_D;
    @JsonProperty("SUBSE_SUR_A_I")
    private double SUBSE_SUR_A_I;
    @JsonProperty("SUBSE_SUR_A_D")
    private double SUBSE_SUR_A_D;
    @JsonProperty("SUBSE_MOD_L_I")
    private double SUBSE_MOD_L_I;
    @JsonProperty("SUBSE_MOD_L_D")
    private double SUBSE_MOD_L_D;
    @JsonProperty("SUBSE_MOD_S_I")
    private double SUBSE_MOD_S_I;
    @JsonProperty("SUBSE_MOD_S_D")
    private double SUBSE_MOD_S_D;
    @JsonProperty("SUBSE_MOD_A_I")
    private double SUBSE_MOD_A_I;
    @JsonProperty("SUBSE_MOD_A_D")
    private double SUBSE_MOD_A_D;
    @JsonProperty("SUBAU_ATT_L_I")
    private double SUBAU_ATT_L_I;
    @JsonProperty("SUBAU_ATT_L_D")
    private double SUBAU_ATT_L_D;
    @JsonProperty("SUBAU_ATT_S_I")
    private double SUBAU_ATT_S_I;
    @JsonProperty("SUBAU_ATT_S_D")
    private double SUBAU_ATT_S_D;
    @JsonProperty("SUBAU_ATT_A_I")
    private double SUBAU_ATT_A_I;
    @JsonProperty("SUBAU_ATT_A_D")
    private double SUBAU_ATT_A_D;
    @JsonProperty("SUBAU_PRV_L_I")
    private double SUBAU_PRV_L_I;
    @JsonProperty("SUBAU_PRV_L_D")
    private double SUBAU_PRV_L_D;
    @JsonProperty("SUBAU_PRV_S_I")
    private double SUBAU_PRV_S_I;
    @JsonProperty("SUBAU_PRV_S_D")
    private double SUBAU_PRV_S_D;
    @JsonProperty("SUBAU_PRV_A_I")
    private double SUBAU_PRV_A_I;
    @JsonProperty("SUBAU_PRV_A_D")
    private double SUBAU_PRV_A_D;
    @JsonProperty("OWN_HOME_L_I")
    private double OWN_HOME_L_I;
    @JsonProperty("OWN_HOME_L_D")
    private double OWN_HOME_L_D;
    @JsonProperty("OWN_HOME_S_I")
    private double OWN_HOME_S_I;
    @JsonProperty("OWN_HOME_S_D")
    private double OWN_HOME_S_D;
    @JsonProperty("OWN_HOME_A_I")
    private double OWN_HOME_A_I;
    @JsonProperty("OWN_HOME_A_D")
    private double OWN_HOME_A_D;
    @JsonProperty("OWN_APT_L_I")
    private double OWN_APT_L_I;
    @JsonProperty("OWN_APT_L_D")
    private double OWN_APT_L_D;
    @JsonProperty("OWN_APT_S_I")
    private double OWN_APT_S_I;
    @JsonProperty("OWN_APT_S_D")
    private double OWN_APT_S_D;
    @JsonProperty("OWN_APT_A_I")
    private double OWN_APT_A_I;
    @JsonProperty("OWN_APT_A_D")
    private double OWN_APT_A_D;
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
    public static class Total {
        //        total: 총계,
        @JsonProperty("TOTAL_L_SUM")
        private double TOTAL_L_SUM;
        @JsonProperty("TOTAL_S_SUM")
        private double TOTAL_S_SUM;
        @JsonProperty("TOTAL_A_SUM")
        private double TOTAL_A_SUM;
        //        subTotal: 소계,
        @JsonProperty("PBL_L_SUBTOTAL")
        private double PBL_L_SUBTOTAL;
        @JsonProperty("PBL_S_SUBTOTAL")
        private double PBL_S_SUBTOTAL;
        @JsonProperty("PBL_A_SUBTOTAL")
        private double PBL_A_SUBTOTAL;
        //        private double subTotalPRv;
        @JsonProperty("SUB_L_SUBTOTAL")
        private double SUB_L_SUBTOTAL;
        @JsonProperty("SUB_S_SUBTOTAL")
        private double SUB_S_SUBTOTAL;
        @JsonProperty("SUB_A_SUBTOTAL")
        private double SUB_A_SUBTOTAL;
        //
        @JsonProperty("OWN_L_SUBTOTAL")
        private double OWN_L_SUBTOTAL;
        @JsonProperty("OWN_S_SUBTOTAL")
        private double OWN_S_SUBTOTAL;
        @JsonProperty("OWN_A_SUBTOTAL")
        private double OWN_A_SUBTOTAL;
        //        sum: 증-감 합,
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
        //
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
        //
        @JsonProperty("PBLRD_RESI_L_SUM")
        private double PBLRD_RESI_L_SUM;
        @JsonProperty("PBLRD_RESI_S_SUM")
        private double PBLRD_RESI_S_SUM;
        @JsonProperty("PBLRD_RESI_A_SUM")
        private double PBLRD_RESI_A_SUM;
        // 민영주차장은 다른 분류가 없으므로 각각의 합이 곧 소계
//        private double sumPrvLots;
//        private double sumPrvSpaces;
//        private double sumPrvArea;
        //
        @JsonProperty("PRV_L_SUM")
        private double PRV_L_SUM;
        @JsonProperty("PRV_S_SUM")
        private double PRV_S_SUM;
        @JsonProperty("PRV_A_SUM")
        private double PRV_A_SUM;
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
        //
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
        private double PBLRD_PAY_L_I;
        private double PBLRD_PAY_L_D;
        private double PBLRD_PAY_S_I;
        private double PBLRD_PAY_S_D;
        private double PBLRD_PAY_A_I;
        private double PBLRD_PAY_A_D;
        private double PBLRD_FREE_L_I;
        private double PBLRD_FREE_L_D;
        private double PBLRD_FREE_S_I;
        private double PBLRD_FREE_S_D;
        private double PBLRD_FREE_A_I;
        private double PBLRD_FREE_A_D;
        private double PBLRD_RESI_L_I;
        private double PBLRD_RESI_L_D;
        private double PBLRD_RESI_S_I;
        private double PBLRD_RESI_S_D;
        private double PBLRD_RESI_A_I;
        private double PBLRD_RESI_A_D;
        private double PBLOUT_PAY_L_I;
        private double PBLOUT_PAY_L_D;
        private double PBLOUT_PAY_S_I;
        private double PBLOUT_PAY_S_D;
        private double PBLOUT_PAY_A_I;
        private double PBLOUT_PAY_A_D;
        private double PBLOUT_FREE_L_I;
        private double PBLOUT_FREE_L_D;
        private double PBLOUT_FREE_S_I;
        private double PBLOUT_FREE_S_D;
        private double PBLOUT_FREE_A_I;
        private double PBLOUT_FREE_A_D;
        private double PRV_L_I;
        private double PRV_L_D;
        private double PRV_S_I;
        private double PRV_S_D;
        private double PRV_A_I;
        private double PRV_A_D;
        private double SUBSE_SUR_L_I;
        private double SUBSE_SUR_L_D;
        private double SUBSE_SUR_S_I;
        private double SUBSE_SUR_S_D;
        private double SUBSE_SUR_A_I;
        private double SUBSE_SUR_A_D;
        private double SUBSE_MOD_L_I;
        private double SUBSE_MOD_L_D;
        private double SUBSE_MOD_S_I;
        private double SUBSE_MOD_S_D;
        private double SUBSE_MOD_A_I;
        private double SUBSE_MOD_A_D;
        private double SUBAU_ATT_L_I;
        private double SUBAU_ATT_L_D;
        private double SUBAU_ATT_S_I;
        private double SUBAU_ATT_S_D;
        private double SUBAU_ATT_A_I;
        private double SUBAU_ATT_A_D;
        private double SUBAU_PRV_L_I;
        private double SUBAU_PRV_L_D;
        private double SUBAU_PRV_S_I;
        private double SUBAU_PRV_S_D;
        private double SUBAU_PRV_A_I;
        private double SUBAU_PRV_A_D;
        private double OWN_HOME_L_I;
        private double OWN_HOME_L_D;
        private double OWN_HOME_S_I;
        private double OWN_HOME_S_D;
        private double OWN_HOME_A_I;
        private double OWN_HOME_A_D;
        private double OWN_APT_L_I;
        private double OWN_APT_L_D;
        private double OWN_APT_S_I;
        private double OWN_APT_S_D;
        private double OWN_APT_A_I;
        private double OWN_APT_A_D;
        private String createDtm;
        private LocalDate localDt;

        private MrData mrData;
    }
}
