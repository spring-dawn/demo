package com.example.demo.domain.survey.data.format;

import com.example.demo.domain.CommonEntity;
import com.example.demo.domain.survey.data.pk.RschFmDmPk;
import lombok.*;
import org.hibernate.annotations.Comment;

import javax.persistence.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@IdClass(RschFmDmPk.class)
@Table(name = "DIM_TMPLT_DMND_RD_T")
public class FormatDmRd extends CommonEntity {
    /*
    조사표 정리서식: 수요조사(노상) -> DEMAND ROAD(P) -> DM RD
    북구, 울주군은 주간/야간 시트를 따로 나누었으나 한 테이블로 취급.
   pk: (시군)구코드, 연번, 연도
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
    @Comment("조사시간대. 1: 주간, 2: 야간")
    @Column(name = "DY_NGHT", length = 1)
    private String rschTime;

    @Comment("법정동코드")
    @Column(name = "STDG_CD", length = 10)
    private String bjdCd;

    @Comment("행정동코드")
    @Column(name = "DONG_CD", length = 10)
    private String hjdCd;

    @Comment("조사구역번호(조사구역명)")
    @Column(name = "BLCK_NO", length = 10)
    private String blockNo;

    @Comment("차량번호")
    @Column(name = "VHCL_NO", length = 10)
    private String carNo;

    @Comment("적/불법여부. 1: 합법(주차구획선 내), 2: 합법(주차구획선 외), 3:불법")
    @Column(name = "LGL_YN", length = 1)
    private String isLegal;

    @Comment("차종. 1: 승용, 승합, 소형화물, 2: 대형화물, 특수")
    @Column(name = "VHCL_TYPE", length = 1)
    private String typeC;

    @Comment("등록구분: 시스템 내 자동 등록")
    @Column(name = "REG_YN", length = 5)
    private String isRegi;

    @Comment("삭제유무. Y:삭제, N : 존재")
    @Column(name = "DEL_YN", length = 1)
    private String rmYn;


}
