package com.example.demo.domain.survey.data.mngCard;

import com.example.demo.domain.CommonEntity;
import com.example.demo.domain.survey.data.pk.ResearchSummaryPk;
import com.example.demo.dto.survey.mngCard.RschSummaryDto;
import lombok.*;
import org.hibernate.annotations.Comment;

import javax.persistence.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Embeddable
@Table(name="DIM_SRVY_SMRYTB_T")
@IdClass(ResearchSummaryPk.class)
public class RschSummary extends CommonEntity {
    /*
    3년마다 실시되는 주차장수급 실태조사(관리카드) 엑셀 파일(.xlsx)_총괄표 데이터
    복합 기본키: 실태조사 실시연도(23년도에는 21년 실시 결과), 구군, 행정동, 블록 구분, 주야 구분
    관리카드에서 총괄표(summary) 시트만 추출하므로 RschMngCard 와 사실상 동의어.

    [24.02.19] 표준단어사전에 따라 '실태조사'를 RESEARCH -> SURVEY(설문) 으로 변경합니다.
     */

    @Id
    @Comment("실시연도")
    @Column(name = "YR", length = 4)
    private String year;

    @Id
    @Comment("구군")
    @Column(name = "SGG_NM", length = 5)
    private String sggNm;

    @Id
    @Comment("행정동")
    @Column(name = "DONG", length = 20)
    private String hjDong;

    @Id
    @Comment("블록구분")
    @Column(name = "BLCK_SE", length = 50)
    private String block;

    @Id
    @Comment("주간/야간 구분")
    @Column(name = "DY_NGHT", length = 2)
    private String dayNight;

    @Comment("인구")
    @Column(name = "PPLTN", length = 20)
    private String pop; //인구

    @Comment("가구수")
    @Column(name = "HSHLD_NOCS", length = 20)
    private String households; // 가구수

    @Comment("차량등록대수")
    @Column(name = "VHCL_REG_CNTOM", length = 20)
    private String vehicleCnt; // 차량 등록 대수

    @Comment("용도지역")
    @Column(name = "ZNG", length = 5)
    private String landUsage; //용도지역

    @Comment("빈터(공한지) 개소")
    @Column(name = "VCNTLT", length = 20)
    private String emptyLands; // 빈터(공한지) 개소

    @Comment("빈터(공한지) 면적")
    @Column(name = "VCNTLT_AREA", length = 20)
    //@Column(name = "VCNTLT_AR", length = 20)
    private String emptyArea; // 빈터(공한지) 면적

    // pf: parking facilities 주차시설
    @Comment("주차시설 합계")
    @Column(name = "PK_FCLT_TOT", length = 20)
    private String pfTotal; // 주차시설 합계

    @Comment("주차시설 노상 (소)계")
    @Column(name = "PK_FCLT_ROAD_SUM", length = 20)
    //@Column(name = "PK_FCLT_RD_SUM", length = 20)
    private String pfRDSum; // 주차시설 노상 계

    @Comment("주차시설 노상 거주자")
    @Column(name = "PK_FCLT_ROAD_RESI", length = 20)
    //@Column(name = "PK_FCLT_RD_RESI", length = 20)
    private String pfRDResi; // 주차시설 노상 거주자

    @Comment("주차시설 노상 그 외(공영)")
    @Column(name = "PK_FCLT_ROAD_ETC", length = 20)
    //@Column(name = "PK_FCLT_RD_ETC", length = 20)
    private String pfRDEtc; // 주차시설 노상 그 외(공영)

    @Comment("주차시설 노외(outside) 계")
    @Column(name = "PK_FCLT_OFFST_SUM", length = 20)
    private String pfOutSum; // 주차시설 노외(outside) 계

    @Comment("주차시설 노외 공영")
    @Column(name = "PK_FCLT_OFFST_PBLMN", length = 20)
    private String pfOutPub; // 주차시설 노외 공영

    @Comment("주차시설 노외 민영")
    @Column(name = "PK_FCLT_OFFST_PVTMN", length = 20)
    private String pfOutPri; // 주차시설 노외 민영

    @Comment("주차시설 부설(Attached) 계")
    @Column(name = "PK_FCLT_ATCHD_SUM", length = 20)
    private String pfSubSum; // 주차시설 부설(subsidiary) 계

    @Comment("주차시설 부설 주거")
    @Column(name = "PK_FCLT_ATCHD_RESI", length = 20)
    private String pfSubResi; // 주차시설 부설 주거

    @Comment("주차시설 부설 비주거")
    @Column(name = "PK_FCLT_ATCHD_NRESI", length = 20)
    private String pfSubNonRegi; // 주차시설 부설 비주거

    @Comment("주차시설 빈터 계")
    @Column(name = "PK_FCLT_VCNTLT_SUM", length = 20)
    private String pfEmptySum; // 주차시설 빈터 계

    // pd: parking demand 주차 수요
    @Comment("주차수요 합계")
    @Column(name = "PK_DMND_TOT", length = 20)
    private String pdTotal; // 주차수요 합계

    @Comment("주차수요 노상 소계")
    @Column(name = "PK_DMND_ROAD_SUM", length = 20)
    //@Column(name = "PK_DMND_RD_SUM", length = 20)
    private String pdRDSum; // 주차수요 노상 소계

    @Comment("주차수요 노상 구획내")
    @Column(name = "PK_DMND_ROAD_IN", length = 20)
    //@Column(name = "PK_DMND_RD_IN", length = 20)
    private String pdRDIn; // 주차수요 노상 구획내

    @Comment("주차수요 노상 구획외")
    @Column(name = "PK_DMND_ROAD_OUT", length = 20)
    //@Column(name = "PK_DMND_RD_OUT", length = 20)
    private String pdRDOut; // 주차수요 노상 구획외

    @Comment("주차수요 노상 불법")
    @Column(name = "PK_DMND_ROAD_ILGL", length = 20)
    //@Column(name = "PK_DMND_RD_ILGL", length = 20)
    private String pdRDIll; // 주차수요 노상 불법

    @Comment("주차수요 노외 소계")
    @Column(name = "PK_DMND_OFFST_SUM", length = 20)
    private String pdOutSum;  // 주차수요 노외 소계

    @Comment("주차수요 노외 공영")
    @Column(name = "PK_DMND_OFFST_PBLMN", length = 20)
    private String pdOutPub; // 주차수요 노외 공영

    @Comment("주차수요 노외 민영")
    @Column(name = "PK_DMND_OFFST_PVTMN", length = 20)
    private String pdOutPri; // 주차수요 노외 민영

    @Comment("주차수요 부설 소계")
    @Column(name = "PK_DMND_ATCHD_SUM", length = 20)
    private String pdSubSum; // 주차수요 부설 소계

    @Comment("주차수요 부설 주거")
    @Column(name = "PK_DMND_ATCHD_RESI", length = 20)
    private String pdSubResi; // 주차수요 부설 주거

    @Comment("주차수요 부설 비주거")
    @Column(name = "PK_DMND_ATCHD_NRESI", length = 20)
    private String pdSubNonRegi; // 주차수요 부설 비주거

    @Comment("주차수요 빈터 소계")
    @Column(name = "PK_DMND_VCNTLT_SUM", length = 20)
    private String pdEmptySum; // 주차수요 빈터 소계

    @Column(name = "RGN_ANLS", length = 100)
    @Comment("지역특성분석")
    private String regionAnalysis; // 지역특성분석, 적용 솔루션

    @Column(name = "SLTN", length = 100)
    @Comment("적용 솔루션")
    private String solution;


    // dto response
    public RschSummaryDto toRes() {
        return RschSummaryDto.builder()
                // 기본키
                .year(this.year)
                .sggNm(this.sggNm)
                .hjDong(this.hjDong)
                .block(this.block)
                .dayNight(this.dayNight)
                //
                .pop(this.pop)
                .households(this.households)
                .vehicleCnt(this.vehicleCnt)
                .landUsage(this.landUsage)
                .emptyLands(this.emptyLands)
                .emptyArea(this.emptyArea)
                .pfTotal(this.pfTotal)
                .pfRDSum(this.pfRDSum)
                .pfRDResi(this.pfRDResi)
                .pfRDEtc(this.pfRDEtc)
                .pfOutSum(this.pfOutSum)
                .pfOutPub(this.pfOutPub)
                .pfOutPri(this.pfOutPri)
                .pfSubSum(this.pfSubSum)
                .pfSubResi(this.pfSubResi)
                .pfSubNonRegi(this.pfSubNonRegi)
                .pfEmptySum(this.pfEmptySum)
                .pdTotal(this.pdTotal)
                .pdRDSum(this.pdRDSum)
                .pdRDIn(this.pdRDIn)
                .pdRDOut(this.pdRDOut)
                .pdRDIll(this.pdRDIll)
                .pdOutSum(this.pdOutSum)
                .pdOutPub(this.pdOutPub)
                .pdOutPri(this.pdOutPri)
                .pdSubSum(this.pdSubSum)
                .pdSubResi(this.pdSubResi)
                .pdSubNonRegi(this.pdSubNonRegi)
                .pdEmptySum(this.pdEmptySum)
                .regionAnalysis(this.regionAnalysis)
                .solution(this.solution)
                .createDtm(this.createDtm.toString())
                .build();
    }


}
