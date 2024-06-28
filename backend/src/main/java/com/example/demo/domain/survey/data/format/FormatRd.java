package com.example.demo.domain.survey.data.format;

import com.example.demo.domain.CommonEntity;
import com.example.demo.domain.survey.data.pk.RschFmPk;
import com.example.demo.dto.survey.format.FormatRdDto;
import lombok.*;
import org.hibernate.annotations.Comment;

import javax.persistence.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@IdClass(RschFmPk.class)
@Table(name = "DIM_TMPLT_RD_T")
public class FormatRd extends CommonEntity {
    /*
    조사표 정리서식: 노상주차장
    pk: (시군)구코드, 연번, 연도
    숫자 계산은 가능한 Long 권장... 인데 엑셀 데이터라 String 이 불가피하다.
     */
    @Id
    @Comment("실시연도")
    @Column(name = "YR", length = 4)
    private String year;

    @Id
    @Comment("연번")
    @Column(name = "SN", length = 10)
    private String seq;

    @Id
    @Comment("시군구코드")
    @Column(name = "SGG_CD", length = 10)
    private String sggCd;

    @Id
    @Comment("시설형태")
    @Column(name = "FCLT_PTN", length = 1)
    private String typeF;

    @Comment("주차장 구분")
    @Column(name = "PKLT_TYPE", length = 1)
    private String typeP;

    @Comment("조사구역번호")
    @Column(name = "BLCK_NO", length = 10)
    private String blockNo;

    @Comment("행정동코드")
    @Column(name = "DONG_CD", length = 10)
    private String hjdCd;

    @Comment("법정동코드")
    @Column(name = "STDG_CD", length = 10)
    private String bjdCd;

    @Comment("울주군: 행정리코드")
    @Column(name = "LI_CD", length = 10)
    private String hjlCd;

    @Comment("주차장명")
    @Column(name = "PKLT_NM", length = 100)
    private String name;

    @Comment("주차대수(주차된 차의 수)")
    @Column(name = "PK_CNTOM", length = 10)
    private String spcsParked;

    @Comment("주차면수(주차 가능한 수) 전체")
    @Column(name = "PK_SPCS", length = 10)
    private String spcsTotal;

    @Comment("주차면수 일반")
    @Column(name = "PK_SPCS_CM", length = 10)
    private String spcsCommon;

    @Comment("주차면수 장애인")
    @Column(name = "PK_SPCS_PWDBS", length = 10)
    private String spcsDis;

    @Comment("주차면수 전기차")
    @Column(name = "PK_SPCS_ELCT", length = 10)
    private String spcsElec;

    @Comment("주차면수 기타")
    @Column(name = "PK_SPCS_ETC", length = 10)
    private String spcsEtc;

    // 1: 유료, 2: 무료
    @Comment("유/무료. 1: 유료, 2: 무료")
    @Column(name = "PAY_YN", length = 1)
    private String isPay;
    @Comment("요금")
    @Column(name = "CHAG", length = 30)
    private String pay;

    @Comment("경사여부. 0: 해당없음, 1: 경사")
    @Column(name = "SLP_YN", length = 1)
    private String isSlope;
    @Comment("미끄럼 방지시설 설치여부. 0: 미설치, 1: 설치")
    @Column(name = "SLIP_PRVNT_YN", length = 1)
    private String hasNonSlip;
    @Comment("안내표지 설치여부. 0: 미설치, 1: 설치")
    @Column(name = "INFO_SIGN_YN", length = 1)
    private String hasInfoSign;
    // 주간, 야간 주차대수. 그냥 숫자 아닌 코드 규칙.
    @Comment("주차대수(주간) 블럭별 연번")
    @Column(name = "DY_BLCK_SN", length = 10)
    private String blockSeqD;
    @Comment("주차대수(주간) 적/불법 여부. 1: 적법(구획 안), 2: 적법(구획 밖), 3:불법")
    @Column(name = "DY_LGL_YN", length = 1)
    private String isLegalD;
    @Comment("주차대수(주간) 차종")
    @Column(name = "DY_VHCL_TYPE", length = 1)
    private String typeCarD;
    @Comment("주차대수(야간) 블럭별 연번")
    @Column(name = "NGHT_BLCK_SN", length = 10)
    private String blockSeqN;
    @Comment("주차대수(야간) 적/불법 여부. 1: 적법(구획 안), 2: 적법(구획 밖), 3:불법")
    @Column(name = "NGHT_LGL_YN", length = 1)
    private String isLegalN;
    @Comment("주차대수(야간) 차종")
    @Column(name = "NGHT_VHCL_TYPE", length = 1)
    private String typeCarN;


    // dto
    public FormatRdDto toRes() {
        return FormatRdDto.builder()
                .year(this.year)
                .seq(this.seq)
                .typeP(this.typeP)
                .sggCd(this.sggCd)
                .blockNo(this.blockNo)
                .hjdCd(this.hjdCd)
                .bjdCd(this.bjdCd)
                .hjlCd(this.hjlCd)
                .name(this.name)
                .spcsParked(this.spcsParked)
                .spcsTotal(this.spcsTotal)
                .spcsCommon(this.spcsCommon)
                .spcsDis(this.spcsDis)
                .spcsElec(this.spcsElec)
                .spcsEtc(this.spcsEtc)
                .isPay(this.isPay)
                .pay(this.pay)
                .typeF(this.typeF)
                .isSlope(this.isSlope)
                .hasNonSlip(this.hasNonSlip)
                .hasInfoSign(this.hasInfoSign)
                .isLegalD(this.isLegalD)
                .typeCarD(this.typeCarD)
                .blockSeqD(this.blockSeqD)
                .isLegalN(this.isLegalN)
                .typeCarN(this.typeCarN)
                .blockSeqN(this.blockSeqN)
                .build();
    }


}
