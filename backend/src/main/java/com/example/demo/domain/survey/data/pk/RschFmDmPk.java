package com.example.demo.domain.survey.data.pk;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
@Embeddable
@NoArgsConstructor
@EqualsAndHashCode
public class RschFmDmPk implements Serializable {
    /*
   주차시설 공통 복합키
    */
    @Comment("실시연도")
    @Column(name = "YR", length = 4)
    private String year;

    @Comment("연번")
    @Column(name = "SN", length = 10)
    private String seq;

    @Comment("시군구코드")
    @Column(name = "SGG_CD", length = 10)
    private String sggCd;

    // 북구, 울주군은 조사시간대별 시트 분리하며 연번이 중복됨. 한 테이블에 데이터 넣으려면 조사시간대까지 pk 로 지정.
    @Comment("조사시간대. 1: 주간, 2: 야간")
    @Column(name = "DY_NGHT", length = 1)
    private String rschTime;
}
