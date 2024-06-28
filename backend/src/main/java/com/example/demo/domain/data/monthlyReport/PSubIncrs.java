package com.example.demo.domain.data.monthlyReport;

import com.example.demo.domain.CommonEntity;
import com.example.demo.dto.data.monthlyReport.PSubIncrsDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.Comment;

import javax.persistence.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "DIM_PS_INCRS_T", indexes = {
        @Index(name = "DIM_PS_INCRS_IX01", columnList = "YR"),
        @Index(name = "DIM_PS_INCRS_IX02", columnList = "MM"),
        @Index(name = "DIM_PS_INCRS_IX03", columnList = "SGG_CD"),
})
public class PSubIncrs extends CommonEntity {
    /*
    부설주차장 증가 현황
     */

    @Id
    @Column(name = "PKLT_INCRS_NO")
    //@Column(name = "PS_INCRS_NO")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Comment("실시연도 ex)2022")
    @Column(name = "YR", length = 4, nullable = false)
    private String year;

    @Comment("실시월 01, 02~12")
    @Column(name = "MM", length = 2, nullable = false)
    private String month;

    @Comment("구군")
    @Column(name = "SGG_CD", length = 10, nullable = false)
    private String sggCd;

    @Comment("건축구분")
    @Column(name = "BLDG_TYPE", length = 10)
    private String buildType;

    @Comment("건물명")
    @Column(name = "BLDG_NM", length = 100)
    private String buildNm;

    @Comment("허가번호")
    @Column(name = "PRMSN_NO", length = 50)
    private String permitNo;

    @Comment("건축주명")
    @Column(name = "BLDG_OWNR", length = 50)
    private String buildOwner;

    @Comment("대지위치")
    @Column(name = "LCTN", length = 100)
    private String location;

    @Comment("사용승인일")
    @Column(name = "APRV_YMD", length = 10)
    private String approvalDt;

    @Comment("주용도")
//    TODO @Column(name = "MAIN_USG", length = 50)
    @Column(name = "MAIN_USG", length = 50)
    private String mainUse;

    @Comment("부속용도")
//   TODO @Column(name = "ANX_USG", length = 100)
    @Column(name = "ANX_USG", length = 100)
    private String subUse;

    @Comment("총주차장면적(㎡)")
//   TODO @Column(name = "TOT_AREA")
    @Column(name = "TOT_AREA")
    private double totalArea;

    @Comment("주차장대수")
//   TODO @Column(name = "PKLT_CNTOM")
    @Column(name = "PKLT_CNTOM")
    private Long spaces;

    @Comment("가구수: 공동주거 가족 단위")
//  TODO  @Column(name = "HSHLD")
    @Column(name = "HSHLD")
    private Long households;

    @Comment("세대수: 주민등록 기준 세대 단위. 가구수보다 많을 수 있음.")
//   TODO @Column(name = "HH")  //??
    @Column(name = "HH_CNT")
    private Long generation;

    //추가컬럼 -sks
    @Comment("허가일")
    @Column(name = "PRMSN_YMD")
    private String prmsnYmd;

    @Comment("연면적")
//TODO    @Column(name = "TOT_FLAREA")
    @Column(name = "GFA")
    //@Column(name = "TOT_FLAREA")
    private double ttlFlarea;

    @Comment("호수")
    @Column(name = "BLDGHO")
    private String bldHo;

    @Comment("추가면수")
    @Column(name = "ADD_SPCS")
    //@Column(name = "ADD_PKSPCS_CNT")
    private Long addPkspaceCnt;

    @Comment("기계식")
    @Column(name = "MCNCL")
    private double subau;

    @Comment("자주식")
    @Column(name = "SFPRPL")
    private double subse;

    @Comment("비고")
    @Column(name = "RMRK")
    private String rmrk;


    // 일괄 삭제 연관관계.
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @Comment("월간보고 파일업로드 번호")
    @JoinColumn(name = "MNTL_RPT_DATA_NO")
    private MrData mrData;

    // update
    public void update(PSubIncrsDto req) {
        this.id = req.getId();
        this.year = req.getYear();
        this.month = req.getMonth();
        this.sggCd = req.getSggCd();
        this.buildType = req.getBuildType();
        this.buildNm = req.getBuildNm();
        this.permitNo = req.getPermitNo();
        this.buildOwner = req.getBuildOwner();
        this.location = req.getLocation();
        this.approvalDt = req.getApprovalDt();
        this.mainUse = req.getMainUse();
        this.subUse = req.getSubUse();
        this.totalArea = req.getTotalArea();
        this.spaces = req.getSpaces();
        this.households = req.getHouseholds();
        this.generation = req.getGeneration();
        this.prmsnYmd = req.getPrmsnYmd();
        this.ttlFlarea = req.getTtlFlarea();
        this.bldHo = req.getBldHo();
        this.addPkspaceCnt = req.getAddPkspaceCnt();
        this.subau = req.getSubau();
        this.subse = req.getSubse();
        this.rmrk = req.getRmrk();
    }

    // dto
    public PSubIncrsDto toRes() {
        return PSubIncrsDto.builder()
                .id(this.id)
                .year(this.year)
                .month(this.month)
                .sggCd(this.sggCd)
                .buildType(this.buildType)
                .buildNm(this.buildNm)
                .permitNo(this.permitNo)
                .buildOwner(this.buildOwner)
                .location(this.location)
                .approvalDt(this.approvalDt)
                .mainUse(this.mainUse)
                .subUse(this.subUse)
                .totalArea(this.totalArea)
                .spaces(this.spaces)
                .households(this.households)
                .generation(this.generation)
                .prmsnYmd(this.prmsnYmd)
                .ttlFlarea(this.ttlFlarea)
                .bldHo(this.bldHo)
                .addPkspaceCnt(this.addPkspaceCnt)
                .subau(this.subau)
                .subse(this.subse)
                .rmrk(this.rmrk)
                .createDtm(this.createDtm == null ? null :
                        this.createDtm.toString().substring(0, 10)
                )
                .build();
    }

}
