package com.example.demo.domain.survey.data.pk;

import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode
public class ResearchSummaryPk implements Serializable {
    /*
    Research 테이블은 복합 기본키이므로 따로 설정합니다
     */
    private String year;
    private String sggNm;
    private String hjDong;
    private String block;
    private String dayNight;

}
