package com.example.demo.domain.data.illegal;


import com.example.demo.domain.CommonEntity;
import com.example.demo.domain.common.file.FileInfo;
import com.example.demo.domain.data.illegal.pk.IllCrdnPrfmncPk;
import com.example.demo.domain.data.illegal.pk.IllFixedPk;
import com.example.demo.dto.data.illegal.IllCrdnPrfmncDto;
import com.example.demo.dto.data.illegal.IllDataDto;
import lombok.*;
import org.hibernate.annotations.Comment;

import javax.persistence.*;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Embeddable
@Table(name = "DIM_ILL_CRDN_PRFMNC_T") // 단속 실적 총괄
@IdClass(IllCrdnPrfmncPk.class)
public class IllCrdnPrfmnc extends CommonEntity {
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

    @Id
    @Column(name = "GUGUN")
    @Comment("구분")
    private String gubun;

    @Column(name = "CRDN_NOCS")
    @Comment("단속건수")
    private Integer crdnNocs;

    @Column(name = "LEVY_AMT")
    @Comment("부과금액")
    private Integer levyAmt;

    @Column(name = "CLCTN_NOCS")
    @Comment("징수건수")
    private Integer clctnNocs;

    @Column(name = "CLCTN_AMT")
    @Comment("징수금액")
    private Integer clctnAmt;

    @Column(name = "CLCTN_RATE")
    @Comment("징수율")
    private Double clctnRate;

    @Column(name = "CRDN_NOPE")
    @Comment("단속인원")
    private Integer crdnNope;

    // dto
    public IllCrdnPrfmncDto toRes() {
        return IllCrdnPrfmncDto.builder()
                .year(this.year)
                .month(this.month)
                .sgg(this.sgg)
                .gubun(this.gubun)
                .crdnNocs(this.crdnNocs)
                .levyAmt(this.levyAmt)
                .clctnNocs(this.clctnNocs)
                .clctnAmt(this.clctnAmt)
                .clctnRate(this.clctnRate)
                .crdnNope(this.crdnNope)
                .build();
    }
}
