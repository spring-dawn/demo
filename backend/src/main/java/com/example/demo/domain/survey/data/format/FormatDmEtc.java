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
@Table(name = "DIM_TMPLT_DMND_ETC_T")
public class FormatDmEtc extends CommonEntity {
    /*
       조사표 정리서식: 수요조사(노외, 부설, 기타) -> DEMAND ETC(P) -> DM ETC
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

    @Comment("조사구역명")
    @Column(name = "BLCK_NO", length = 10)
    private String blockNo;

    @Comment("지번 주소: 본번(번지)")
    @Column(name = "MNO", length = 10)
    private String lotNoAddr1;

    @Comment("지번 주소: 부번(호)")
    @Column(name = "SNO", length = 10)
    private String lotNoAddr2;

    // 주차장(총)면수, 이륜차 외 주차대수, 이륜차 주차대수, 용도지구 구분
    @Comment("주차면수(주차 가능한 수) 전체")
    @Column(name = "SPCS_TOT", length = 10)
    private String spcsTotal;

    @Comment("이륜차 외 주차대수")
    @Column(name = "CNTOM_ETC", length = 10)
    private String spcsNon2w;

    @Comment("이륜차 대수")
    @Column(name = "CNTOM_TWOWH", length = 10)
    private String spcs2w;

    @Comment("용도지구 구분. 수요조사코드표 참고.")
    @Column(name = "USG_SE", length = 10)
    private String useGubun;

    @Comment("등록구분: 시스템 내 자동 등록")
    @Column(name = "REG_YN", length = 5)
    private String isRegi;

    @Comment("삭제유무. Y:삭제, N : 존재")
    @Column(name = "DEL_YN", length = 1)
    private String rmYn;
}
