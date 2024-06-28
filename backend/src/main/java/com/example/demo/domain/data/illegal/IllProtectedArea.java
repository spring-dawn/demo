package com.example.demo.domain.data.illegal;


import com.example.demo.domain.CommonEntity;
import com.example.demo.domain.data.illegal.pk.IllMobilePk;
import com.example.demo.domain.data.illegal.pk.IllProtectedAreaPk;
import com.example.demo.dto.data.illegal.IllFireplugDto;
import com.example.demo.dto.data.illegal.IllProtectedAreaDto;
import lombok.*;
import org.hibernate.annotations.Comment;

import javax.persistence.*;
import java.util.HashMap;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Embeddable
@Table(name = "DIM_ILL_PROTECTED_AREA_T") // 보호구역
@IdClass(IllProtectedAreaPk.class)
public class IllProtectedArea extends CommonEntity {
    @Id
    @Column(name = "YEAR")
    @Comment("년도")
    private String year;

    @Id
    @Column(name = "MONTH")
    @Comment("월")
    private String month;

    @Id
    @Column(name = "SGG")
    @Comment("구군코드")
    private String sgg;

    @Column(name = "LEVY_YMD")
    @Comment("부과일자(년월)")
    private Integer levyYmd;

    @Column(name = "NOCS")
    @Comment("건수")
    private Integer nocs;

    @Column(name = "AMT")
    @Comment("금액(단위:천원)")
    private Integer amt;

    public void initExcelRow(HashMap<String, String> params, String year) {
        this.nocs = params.get("건수") == null || params.get("건수").isEmpty() ? 0 :  Integer.parseInt(params.get("건수").replaceAll(",", ""));
        this.amt = params.get("금액") == null || params.get("금액").isEmpty() ? 0 :  Integer.parseInt(params.get("금액").replaceAll(",", ""));
    }

    public IllProtectedAreaDto toRes() {
        return IllProtectedAreaDto.builder()
                .year(this.year)
                .month(this.month)
                .sgg(this.sgg)
                .amt(this.amt)
                .levyYmd(this.levyYmd)
                .nocs(this.nocs)
                .build();
    }
}
