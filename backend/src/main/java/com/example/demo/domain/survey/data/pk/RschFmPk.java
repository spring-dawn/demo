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
public class RschFmPk implements Serializable {
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

    @Comment("시설형태")
    @Column(name = "FCLT_PTN", length = 1)
    private String typeF;

}
