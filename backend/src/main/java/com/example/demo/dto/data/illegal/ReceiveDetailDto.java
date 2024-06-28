package com.example.demo.dto.data.illegal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
public class ReceiveDetailDto {
    private Long id;
    private String sgbCd;
    private String lvyKey;
    private String dptNm;
    private String dptCd;
    private String spacBizCd;
    private Integer fyr;
    private String actSeCd;
    private String rprsTxmCd;
    private String rprsTxmNm;
    private Integer lvyNo;
    private Integer itmSn;
    private Integer rcvmtSn;
    private LocalDate rcvmtYmd;
    private Integer rcvmtPctAmt;
    private Integer rcvmtAdtnAmt;
    private Integer itmIntrAmt;
    private String rcvmtBank;
    private String rcvmtTyCd;
    private String rcvmtTyNm;
    private LocalDate actYmd;
    private LocalDate pmkYmd;
    private LocalDate frstPidYmd;
    private LocalDate lvyYmd;
    private String glNm;
    private String rcvmtSeCd;
    private String rcvmtSttSeCd;
    private String taxnNo;
    private String glMngNo;
    private String glAddr;

    public static class Keyword {
        private Long id;
    }
}
