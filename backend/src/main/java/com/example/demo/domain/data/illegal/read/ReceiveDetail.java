package com.example.demo.domain.data.illegal.read;

import com.example.demo.domain.CommonEntity;
import com.example.demo.dto.data.illegal.ReceiveDetailDto;
import lombok.*;
import org.hibernate.annotations.Comment;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "DIM_RCVMT_DTL_INFO_T")
public class ReceiveDetail extends CommonEntity {
    /*
    수납상세정보. 과태료적발대장을 바탕으로 이후의 수납 정보를 요청합니다.
     */

    @Id
    @Comment("수납상세정보 번호")
    @Column(name = "RCVMT_DTL_INFO_NO")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Comment("지방자치단체코드")
    @Column(name = "LCLGVMN_CD", length = 4)
    //@Column(name = "SGB_CD", length = 4)
    private String sgbCd;
    @Comment("부과키")
    @Column(name = "LEVY_KEY", length = 20)
    //@Column(name = "IVY_KEY", length = 20)
    private String lvyKey;
    @Comment("부서명")
    @Column(name = "DEPT_NM", length = 200)
    //@Column(name = "DPT_NM", length = 200)
    private String dptNm;
    @Comment("부서코드")
    @Column(name = "DEPT_CD", length = 4)
    //@Column(name = "DPT_CD", length = 4)
    private String dptCd;
    @Comment("특별회계사업코드")
    @Column(name = "SPCL_ACNTG_BIZ_CD", length = 4)
    //@Column(name = "SPAC_BIZ_CD", length = 4)
    private String spacBizCd;
    @Comment("회계연도")
    @Column(name = "FYR")
    //@Column(name = "FYR")
    private Integer fyr;
    @Comment("회계구분코드")
    @Column(name = "ACNTG_SE_CD", length = 4)
    //@Column(name = "ACT_SE_CD", length = 4)
    private String actSeCd;
    @Comment("대표세입과목코드")
    @Column(name = "RPRS_RVN_CLS_CD", length = 4)
    //@Column(name = "RPRS_TXM_CD", length = 4)
    private String rprsTxmCd;
    @Comment("대표세입과목명")
    @Column(name = "RPRS_RVN_CLS_NM", length = 100)
    //@Column(name = "RPRS_TXM_NM", length = 100)
    private String rprsTxmNm;
    @Comment("부과번호")
    @Column(name = "LEVY_NO")
    //@Column(name = "IVY_NO")
    private Integer lvyNo;
    @Comment("분납일련번호")
    @Column(name = "ITM_SN")
    //@Column(name = "ITM_SN")
    private Integer itmSn;
    @Comment("수납일련번호")
    @Column(name = "RCVMT_SN")
    //@Column(name = "RCVMT_SN")
    private Integer rcvmtSn;
    @Comment("수납일자")
    @Column(name = "RCVMT_YMD")
    //@Column(name = "RCVMT_YMD")
    private LocalDate rcvmtYmd;
    @Comment("수납본세금액")
    @Column(name = "RCVMT_MNTAX_AMT")
    //@Column(name = "RCVMT_PCT_AMT")
    private Integer rcvmtPctAmt;
    @Comment("수납가산금액")
    @Column(name = "RCVMT_ADTN_AMT")
    //@Column(name = "RCVMT_ADTN_AMT")
    private Integer rcvmtAdtnAmt;
    @Comment("분납이자금액")
    @Column(name = "ITM_INT_AMT")
    //@Column(name = "ITM_INTR_AMT")
    private Integer itmIntrAmt;
    @Comment("수납은행")
    @Column(name = "RCVMT_BANK", length = 80)
    //@Column(name = "RCVMT_BANK", length = 80)
    private String rcvmtBank;
    @Comment("수납유형코드")
    @Column(name = "RCVMT_TYPE_CD", length = 4)
    //@Column(name = "RCVMT_TY_CD", length = 4)
    private String rcvmtTyCd;
    @Comment("수납유형명")
    @Column(name = "RCVMT_TYPE_NM", columnDefinition = "text")
    //@Column(name = "RCVMT_TY_NM", columnDefinition = "text")
    private String rcvmtTyNm;
    @Comment("회계일자")
    @Column(name = "ACNTG_YMD")
    //@Column(name = "ACT_YMD")
    private LocalDate actYmd;
    @Comment("소인일자")
    @Column(name = "STAMP_YMD")
    //@Column(name = "PMK_YMD")
    private LocalDate pmkYmd;
    @Comment("최초납기일자")
    @Column(name = "FIRST_DUDT_YMD")
    //@Column(name = "FRST_PID_YMD")
    private LocalDate frstPidYmd;
    @Comment("부과일자")
    @Column(name = "LEVY_YMD")
    //@Column(name = "IVY_YMD")
    private LocalDate lvyYmd;
    @Comment("물건지명")
    @Column(name = "BLDOWNREST_NM", columnDefinition = "text")
    //@Column(name = "GL_NM", columnDefinition = "text")
    private String glNm;
    @Comment("수납구분코드")
    @Column(name = "RCVMT_SE_CD", length = 4)
    //@Column(name = "RCVMT_SE_CD", length = 4)
    private String rcvmtSeCd;
    @Comment("수납구분")
    @Column(name = "RCVMT_STTS_SE_CD", length = 4)
    //@Column(name = "RCVMT_STTS_SE_CD", length = 4)
    private String rcvmtSttSeCd;
    @Comment("과세번호")
    @Column(name = "TXTN_NO", length = 31)
    //@Column(name = "TAXN_NO", length = 31)
    private String taxnNo;
    @Comment("물건지관리번호")
    @Column(name = "BLDOWNREST_MNG_NM", length = 20)
    //@Column(name = "GL_MNG_NO", length = 20)
    private String glMngNo;
    @Comment("물건지주소")
    @Column(name = "BLDOWNREST_ADDR", columnDefinition = "text")
    //@Column(name = "GL_ADDR", columnDefinition = "text")
    private String glAddr;


    // dto
    public ReceiveDetailDto toRes() {
        return ReceiveDetailDto.builder()
                .id(id)
                .sgbCd(sgbCd)
                .lvyKey(lvyKey)
                .dptNm(dptNm)
                .dptCd(dptCd)
                .spacBizCd(spacBizCd)
                .fyr(fyr)
                .actSeCd(actSeCd)
                .rprsTxmCd(rprsTxmCd)
                .rprsTxmNm(rprsTxmNm)
                .lvyNo(lvyNo)
                .itmSn(itmSn)
                .rcvmtSn(rcvmtSn)
                .rcvmtYmd(rcvmtYmd)
                .rcvmtPctAmt(rcvmtPctAmt)
                .rcvmtAdtnAmt(rcvmtAdtnAmt)
                .itmIntrAmt(itmIntrAmt)
                .rcvmtBank(rcvmtBank)
                .rcvmtTyCd(rcvmtTyCd)
                .rcvmtTyNm(rcvmtTyNm)
                .actYmd(actYmd)
                .pmkYmd(pmkYmd)
                .frstPidYmd(frstPidYmd)
                .lvyYmd(lvyYmd)
                .glNm(glNm)
                .rcvmtSeCd(rcvmtSeCd)
                .rcvmtSttSeCd(rcvmtSttSeCd)
                .taxnNo(taxnNo)
                .glMngNo(glMngNo)
                .glAddr(glAddr)
                .build();
    }


}
