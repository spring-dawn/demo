package com.example.demo.domain.data.illegal.read;

import com.example.demo.domain.CommonEntity;
import com.example.demo.dto.data.illegal.FineLedgerDto;
import lombok.*;
import org.hibernate.annotations.Comment;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "DIM_PK_VLTN_FFNLG_LDGR_T")
public class FineLedger extends CommonEntity {
    /*
    주정차위반 과태료 대장 => Fine Ledger 과태료 대장
     */

    @Id
    @Comment("과태료대장 번호")
    @Column(name = "FFNLG_LDGR_NO")
    //@Column(name = "FFNLG_LDGR_NO")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Comment("자치단체코드")
    @Column(name = "LCLGVMN_CD", length = 4)
    //@Column(name = "SGB_CD", length = 4)
    private String sgbCd;
    @Comment("자치단체명")
    @Column(name = "LCLGVMN_NM", length = 60)
    //@Column(name = "SGB_NM", length = 60)
    private String sgbNm;
    @Comment("주정차위반대장키")
    @Column(name = "PK_VLTN_LDGR_KEY", length = 20)
    //@Column(name = "PRKG_VLT_ACB_KEY", length = 20)
    private String prkgVltAcbKey;
    @Comment("부서코드")
    @Column(name = "DEPT_CD", length = 4)
    //@Column(name = "DPT_CD", length = 4)
    private String dptCd;
    @Comment("부서명")
    @Column(name = "DEPT_NM", length = 200)
    //@Column(name = "DPT_NM", length = 200)
    private String dptNm;
    @Comment("특별회계사업코드")
    @Column(name = "SP_ACNTG_BIZ_CD", length = 4)
    //@Column(name = "SPCL_FIS_BIZ_CD", length = 4)
    private String spclFisBizCd;
    @Comment("특별회계사업명")
    @Column(name = "SP_ACNTG_BIZ_NM", length = 100)
    //@Column(name = "SPCL_FIS_BIZ_NM", length = 100)
    private String spclFisBizNm;
    @Comment("회계구분코드")
    @Column(name = "ACNTG_SE_CD", length = 4)
    //@Column(name = "ACT_SE_CD", length = 4)
    private String actSeCd;
    @Comment("회계구분명")
    @Column(name = "ACNTG_SE_NM", length = 100)
    //@Column(name = "ACT_SE_NM", length = 100)
    private String actSeNm;
    @Comment("대표세입과목코드")
    @Column(name = "RPRS_RVN_CLS_CD", length = 4)
    //@Column(name = "RPRS_TXM_CD", length = 4)
    private String rprsTxmCd;
    @Comment("대표세입과목명")
    @Column(name = "RPRS_RVN_CLS_NM", length = 100)
    //@Column(name = "RPRS_TXM_NM", length = 100)
    private String rprsTxmNm;
    @Comment("운영항목코드")
    @Column(name = "OPER_ITEM_CD", length = 4)
    //@Column(name = "OPER_ITEM_CD", length = 4)
    private String operItemCd;
    @Comment("운영항목명")
    @Column(name = "OPER_ITEM_NM", length = 100)
    //@Column(name = "OPER_ITEM_NM", length = 100)
    private String operItemNm;
    @Comment("차량명")
    @Column(name = "VHCL_NM", length = 150)
    //@Column(name = "VHCL_NM", length = 150)
    private String vhclNm;
    @Comment("단속구분코드")
    @Column(name = "CRDN_SE_CD", length = 4)
    //@Column(name = "RGTN_SE_CD", length = 4)
    private String rgtnSeCd;
    @Comment("단속구분명")
    @Column(name = "CRDN_SE_NM", length = 100)
    //@Column(name = "RGTN_SE_NM", length = 100)
    private String rgtnSeNm;
    @Comment("단속일자")
    @Column(name = "CRDN_YMD")
    //@Column(name = "RGTN_YMD")
    private LocalDate rgtnYmd;
    @Comment("단속시간")
    @Column(name = "CRDN_HR")
    //@Column(name = "RGTN_HR")
    private LocalDateTime rgtnHr;
    @Comment("시간초과여부")
    @Column(name = "HR_OVER_YN", length = 1)
    //@Column(name = "HR_EXCESS_YN", length = 1)
    private String hrExcessYn;
    @Comment("단속장소명")
    @Column(name = "CRDN_PLC_NM", length = 300)
    //@Column(name = "RGTN_PLC_NM", length = 300)
    private String rgtnPlcNm;
    @Comment("법조항내용")
    @Column(name = "LAWCLS_CN", length = 300)
    //@Column(name = "LAW_ATCL_CN", length = 300)
    private String lawAtclCn;
    @Comment("주정차위반구분코드")
    @Column(name = "PK_VLTN_SE_CD", length = 4)
    //@Column(name = "PRKG_VLT_SE_CD", length = 4)
    private String prkgVltSeCd;
    @Comment("주정차위반구분명")
    @Column(name = "PK_VLTN_SE_NM", length = 100)
    //@Column(name = "PRKG_VLT_SE_NM", length = 100)
    private String prkgVltSeNm;
    @Comment("주정차위반구분기타사유내용")
    @Column(name = "PK_VLTN_SE_ETC_RSN", length = 300)
    //@Column(name = "PRKG_VLT_SE_ETC_RSN_CN", length = 300)
    private String prkgVltSeEtcRsnCn;
    @Comment("도로교통법차종종별코드")
    @Column(name = "ROAD_LAW_CARMDL_CD", length = 4)
    //@Column(name = "ROAD_TRSPT_LAW_VINTP_ASM_CD", length = 4)
    private String roadTrsptLawVintpAsmCd;
    @Comment("도로교통법차종종별명")
    @Column(name = "ROAD_LAW_CARMDL_NM", length = 100)
    //@Column(name = "ROAD_TRSPT_LAW_VINTP_ASM_NM", length = 100)
    private String roadTrsptLawVintpAsmNm;
    @Comment("GPS좌표X축")
    @Column(name = "LOT", length = 20)
    //@Column(name = "GPS_CDN_X_AXS", length = 20)
    private String gpsCdnXaxs;
    @Comment("GPS좌표Y축")
    @Column(name = "LAT", length = 20)
    //@Column(name = "GPS_CDN_Y_AXS", length = 20)
    private String gpsCdnYaxs;
    @Comment("단속특별구역구분코드")
    @Column(name = "CRDN_SP_ZONE_SE_CD", length = 4)
    //@Column(name = "RGTN_SPECL_ZONE_SE_CD", length = 4)
    private String rgtnSpeclZoneSeCd;
    @Comment("단속특별구역구분명")
    @Column(name = "CRDN_SP_ZONE_SE_NM", length = 100)
    //@Column(name = "RGTN_SPECL_ZONE_SE_NM", length = 100)
    private String rgtnSpeclZoneSeNm;
    @Comment("사전통지일자")
    @Column(name = "PR_AVTSMT_YMD")
    //@Column(name = "BF_AVTSMT_YMD")
    private LocalDate bfAvtsmtYmd;
    @Comment("의견제출기한시작일자")
    @Column(name = "OPNN_SBMSN_TERM_BGNG_YMD")
    //@Column(name = "OPN_SBMSN_TERM_BGNG_YMD")
    private LocalDate opnSbmsnTermBgngYmd;
    @Comment("의견제출기한종료일자")
    @Column(name = "OPNN_SBMSN_TERM_END_YMD")
    //@Column(name = "OPN_SBMSN_TERM_END_YMD")
    private LocalDate opnSbmsnTermEndYmd;
    @Comment("최초과태료")
    @Column(name = "FRST_FFNLG")
    //@Column(name = "FRST_FIN")
    private Integer frstFin;
    @Comment("감경사유구분코드")
    @Column(name = "DCRS_RSN_SE_CD", length = 4)
    //@Column(name = "RDT_RSN_SE_CD", length = 4)
    private String rdtRsnSeCd;
    @Comment("감경사유구분명")
    @Column(name = "DCRS_RSN_SE_NM", length = 100)
    //@Column(name = "RDT_RSN_SE_NM", length = 100)
    private String rdtRsnSeNm;
    @Comment("감경율")
    @Column(name = "DCRS_RT")
    //@Column(name = "RDT_RT")
    private Integer rdtRt;
    @Comment("과태료")
    @Column(name = "FFNLG")
    //@Column(name = "FIN")
    private Integer fin;
    @Comment("사전감경율")
    @Column(name = "PR_DCRS_RT")
    //@Column(name = "BF_RDT_RT")
    private Integer bfRdtRt;
    @Comment("사전감경금액")
    @Column(name = "PR_DCRS_AMT")
    //@Column(name = "BF_RDT_AMT")
    private Integer bfRdtAmt;
    @Comment("비부과처리구분코드")
    @Column(name = "NLEVY_PRCS_SE_CD", length = 4)
    //@Column(name = "NLVY_PRCS_SE_CD", length = 4)
    private String nlvyPrcsSeCd;
    @Comment("비부과처리구분명")
    @Column(name = "NLEVY_PRCS_SE_NM", length = 100)
    //@Column(name = "NLVY_PRCS_SE_NM", length = 100)
    private String nlvyPrcsSeNm;
    @Comment("면제사유구분코드")
    @Column(name = "EXMPTN_RSN_SE_CD", length = 4)
    //@Column(name = "EPMT_RSN_SE_CD", length = 4)
    private String emptRsnSeCd;
    @Comment("면제사유구분명")
    @Column(name = "EXMPTN_RSN_SE_NM", length = 100)
    //@Column(name = "EPMT_RSN_SE_NM", length = 100)
    private String emptRsnSeNm;
    @Comment("서손(문서파기)구분코드")
    @Column(name = "DOCABL_SE_CD", length = 4)
    //@Column(name = "ERPP_SE_CD", length = 4)
    private String erppSeCd;
    @Comment("서손(문서파기)구분명")
    @Column(name = "DOCABL_SE_NM", length = 100)
    //@Column(name = "ERPP_SE_NM", length = 100)
    private String erppSeNm;
    @Comment("종료일자")
    @Column(name = "END_YMD")
    //@Column(name = "END_YMD")
    private LocalDate endYmd;
    @Comment("종료사유내용")
    @Column(name = "END_RSN_CN", columnDefinition = "text")
    //@Column(name = "END_RSN_CN", columnDefinition = "text")
    private String endRsnCn;
    @Comment("수납구분명")
    @Column(name = "RCVMT_SE_NM", length = 20)
    //@Column(name = "RCVMT_SE_NM", length = 20)
    private String rcvmtSeNm;


    // dto
    public FineLedgerDto toRes() {
        return FineLedgerDto.builder()
                .id(id)
                .rgtnHr(rgtnHr)
                .bfAvtsmtYmd(bfAvtsmtYmd)
                .opnSbmsnTermBgngYmd(opnSbmsnTermBgngYmd)
                .opnSbmsnTermEndYmd(opnSbmsnTermEndYmd)
                .rgtnYmd(rgtnYmd)
                .endYmd(endYmd)
                .sgbCd(sgbCd)
                .dptCd(dptCd)
                .frstFin(frstFin)
                .rdtRsnSeCd(rdtRsnSeCd)
                .rdtRt(rdtRt)
                .fin(fin)
                .bfRdtRt(bfRdtRt)
                .bfRdtAmt(bfRdtAmt)
                .nlvyPrcsSeCd(nlvyPrcsSeCd)
                .emptRsnSeCd(emptRsnSeCd)
                .erppSeCd(erppSeCd)
                .spclFisBizCd(spclFisBizCd)
                .actSeCd(actSeCd)
                .rprsTxmCd(rprsTxmCd)
                .operItemCd(operItemCd)
                .rgtnSeCd(rgtnSeCd)
                .prkgVltSeCd(prkgVltSeCd)
                .rgtnSpeclZoneSeCd(rgtnSpeclZoneSeCd)
                .roadTrsptLawVintpAsmCd(roadTrsptLawVintpAsmCd)
                .sgbNm(sgbNm)
                .prkgVltAcbKey(prkgVltAcbKey)
                .dptNm(dptNm)
                .spclFisBizNm(spclFisBizNm)
                .actSeNm(actSeNm)
                .rprsTxmNm(rprsTxmNm)
                .operItemNm(operItemNm)
                .vhclNm(vhclNm)
                .rgtnSeNm(rgtnSeNm)
                .hrExcessYn(hrExcessYn)
                .rgtnPlcNm(rgtnPlcNm)
                .lawAtclCn(lawAtclCn)
                .prkgVltSeNm(prkgVltSeNm)
                .prkgVltSeEtcRsnCn(prkgVltSeEtcRsnCn)
                .roadTrsptLawVintpAsmNm(roadTrsptLawVintpAsmNm)
                .gpsCdnXaxs(gpsCdnXaxs)
                .gpsCdnYaxs(gpsCdnYaxs)
                .rgtnSpeclZoneSeNm(rgtnSpeclZoneSeNm)
                .rdtRsnSeNm(rdtRsnSeNm)
                .nlvyPrcsSeNm(nlvyPrcsSeNm)
                .emptRsnSeNm(emptRsnSeNm)
                .erppSeNm(erppSeNm)
                .endRsnCn(endRsnCn)
                .rcvmtSeNm(rcvmtSeNm)
                .build();
    }
}
