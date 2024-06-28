package com.example.demo.domain.data.monthlyReport;

import com.example.demo.domain.CommonEntity;
import com.example.demo.dto.data.monthlyReport.PSubDcrsDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.Comment;

import javax.persistence.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "DIM_PS_DCRS_T", indexes = {
        @Index(name = "DIM_PS_DCRS_IX01", columnList = "YR"),
        @Index(name = "DIM_PS_DCRS_IX02", columnList = "MM"),
        @Index(name = "DIM_PS_DCRS_IX03", columnList = "SGG_CD"),
})
public class PSubDcrs extends CommonEntity {
    /*
    부설주차장 감소 현황
     */
    @Id
    @Column(name = "PKLT_DCRS_NO")
    //@Column(name = "PS_DCRS_NO")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Comment("실시연도 ex)2022")
    @Column(name = "YR", length = 4, nullable = false)
    private String year;

    @Comment("실시월 01~12")
    @Column(name = "MM", length = 2, nullable = false)
    private String month;

    @Comment("구군")
    @Column(name = "SGG_CD", length = 10, nullable = false)
    private String sggCd;

    @Comment("신고번호")
    @Column(name = "RPT_NO", length = 50)
    private String reportNo;

    @Comment("대지위치")
    @Column(name = "SIPSTN", length = 100)
    private String location;

    @Comment("소유자")
    @Column(name = "OWNR", length = 10)
    private String owner;

    @Comment("구분")
    @Column(name = "TYPE", length = 50)
    private String type;

    @Comment("주차장면수")
    @Column(name = "SPCS", length = 50)
    private Long spaces;

    @Comment("주차장면적(㎡)")
    @Column(name = "TOT_AREA", length = 50)
    private double totalArea;

    @Comment("철거/멸실예정일자")
    @Column(name = "DMLTN_YMD", length = 10)
    private String demolitionDt;

    @Comment("철거멸실사유")
    @Column(name = "DMLTN_RSN", length = 200)
    private String demolitionReason;

    @Comment("구조")
    @Column(name = "STRCT", length = 20)
    private String structure;

    // usage 가 mysql 예약어라서 변경.
    @Comment("용도")
    @Column(name = "BLDG_USG", length = 20)
    private String buildUsage;

    //추가 컬럼

    @Comment("석면천장재 함유유무")
    @Column(name = "ABT_CEILMTR_YN")
    private String abtCelYn;

    @Comment("석면/단열재 함유 유무")
    @Column(name = "ABT_ISLT_YN")
    private String abtIstYn;

    @Comment("석면/지붕재 함유 유무")
    @Column(name = "ABT_RFMTR_YN")
    private String abtRoofYn;

    @Comment("석면/보온재 함유 유무")
    @Column(name = "ABT_LAGG_YN")
    private String abtLagYn;

    @Comment("석면/기타 함유 유무")
    @Column(name = "ABT_ETC_YN")
    private String abtEtcYn;

    @Comment("석면/해당없음 유무")
    @Column(name = "ABT_USE_YN")
    //@Column(name = "ABT_NON_YN")
    private String abtNonYn;

    @Comment("허가일")
    @Column(name = "PRMSN_YMD")
    private String prmsnYmd;


    // 일괄 삭제 연관관계.
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MNTL_RPT_DATA_NO")
    private MrData mrData;


    // update
    public void update(PSubDcrsDto req) {
        this.year = req.getYear();
        this.month = req.getMonth();
        this.sggCd = req.getSggCd();
        this.reportNo = req.getReportNo();
        this.location = req.getLocation();
        this.owner = req.getOwner();
        this.type = req.getType();
        this.spaces = req.getSpaces();
        this.totalArea = req.getTotalArea();
        this.demolitionDt = req.getDemolitionDt();
        this.demolitionReason = req.getDemolitionReason();
        this.structure = req.getStructure();
        this.buildUsage = req.getBuildUsage();
        //추가컬럼 -sks
        this.abtCelYn = req.getAbtCelYn();
        this.abtIstYn = req.getAbtIstYn();
        this.abtRoofYn = req.getAbtRoofYn();
        this.abtLagYn = req.getAbtLagYn();
        this.abtEtcYn = req.getAbtEtcYn();
        this.abtNonYn = req.getAbtNonYn();
        this.prmsnYmd = req.getPrmsnYmd();
    }



    // dto
    public PSubDcrsDto toRes() {
        return PSubDcrsDto.builder()
                .id(this.id)
                .year(this.year)
                .month(this.month)
                .sggCd(this.sggCd)
                .reportNo(this.reportNo)
                .location(this.location)
                .owner(this.owner)
                .type(this.type)
                .spaces(this.spaces)
                .totalArea(this.totalArea)
                .demolitionDt(this.demolitionDt)
                .demolitionReason(this.demolitionReason)
                .structure(this.structure)
                .buildUsage(this.buildUsage)
                .createDtm(this.createDtm.toString())
                .abtCelYn(this.abtCelYn)
                .abtIstYn(this.abtIstYn)
                .abtRoofYn(this.abtRoofYn)
                .abtLagYn(this.abtLagYn)
                .abtEtcYn(this.abtEtcYn)
                .abtNonYn(this.abtNonYn)
                .prmsnYmd(this.prmsnYmd)
                .build();
    }

}

